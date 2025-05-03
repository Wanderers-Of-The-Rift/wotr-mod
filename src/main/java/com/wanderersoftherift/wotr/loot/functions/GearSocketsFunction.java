package com.wanderersoftherift.wotr.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.wanderersoftherift.wotr.init.ModLootItemFunctionTypes.GEAR_SOCKETS_FUNCTION;

public class GearSocketsFunction extends LootItemConditionalFunction {
    public static final MapCodec<GearSocketsFunction> CODEC = RecordCodecBuilder.mapCodec(inst -> commonFields(inst)
            .and(Codec.INT.fieldOf("min_sockets").forGetter(GearSocketsFunction::getMinSockets))
            .and(Codec.INT.fieldOf("max_sockets").forGetter(GearSocketsFunction::getMaxSockets))
            .apply(inst, GearSocketsFunction::new));

    private final int minSockets;
    private final int maxSockets;

    protected GearSocketsFunction(List<LootItemCondition> predicates, int minSockets, int maxSockets) {
        super(predicates);
        this.minSockets = minSockets;
        this.maxSockets = maxSockets;
    }

    public int getMinSockets() {
        return minSockets;
    }

    public int getMaxSockets() {
        return maxSockets;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return GEAR_SOCKETS_FUNCTION.get();
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        return generateItemStack(itemStack, lootContext.getRandom());
    }

    private @NotNull ItemStack generateItemStack(ItemStack itemStack, RandomSource random) {
        itemStack.set(ModDataComponentType.GEAR_SOCKETS, GearSockets.randomSockets(minSockets, maxSockets, random));
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setGearSockets(int minSockets, int maxSockets) {
        return simpleBuilder((lootItemConditions) -> {
            return new GearSocketsFunction(lootItemConditions, minSockets, maxSockets);
        });
    }
}
