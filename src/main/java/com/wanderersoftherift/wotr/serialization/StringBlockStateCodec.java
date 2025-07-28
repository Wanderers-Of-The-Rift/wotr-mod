package com.wanderersoftherift.wotr.serialization;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StringBlockStateCodec implements Codec<BlockState> {
    public static final Codec<BlockState> INSTANCE = new StringBlockStateCodec();
    private static final Codec<BlockState> SIMPLE = Codec.STRING.xmap(string -> {
        try {
            return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, string, false).blockState();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }, BlockStateParser::serialize);

    @Override
    public <T> DataResult<Pair<BlockState, T>> decode(DynamicOps<T> ops, T input) {
        if (!(ops instanceof RegistryOps<?> registryOps)) {
            return SIMPLE.decode(ops, input);
        }
        var regInfo = registryOps.lookupProvider.lookup(Registries.BLOCK).get();
        if (!(regInfo instanceof RegistryOps.RegistryInfo<Block>(HolderLookup lookup, var a, var b))) {
            return SIMPLE.decode(ops, input);
        }
        var stringResult = Codec.STRING.decode(ops, input);
        if (!stringResult.isSuccess()) {
            return (DataResult<Pair<BlockState, T>>) (DataResult) stringResult;
        }

        try {
            return DataResult.success(new Pair<>(BlockStateParser
                    .parseForBlock((HolderLookup<Block>) lookup, stringResult.getOrThrow().getFirst(), false)
                    .blockState(), stringResult.getOrThrow().getSecond()));
        } catch (CommandSyntaxException e) {
            return DataResult.error(() -> e.getMessage());
        }
    }

    @Override
    public <T> DataResult<T> encode(BlockState input, DynamicOps<T> ops, T prefix) {
        return Codec.STRING.encode(BlockStateParser.serialize(input), ops, prefix);
    }
}
