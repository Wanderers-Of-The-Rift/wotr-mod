package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.worldgen.WotrProcessors;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

/**
 * Basic BiomeProcessor that applies a biome to an entire structure
 */
public class BiomeProcessor extends StructureProcessor implements RiftFinalProcessor {

    public static final MapCodec<BiomeProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Biome.CODEC.fieldOf("biome").forGetter(BiomeProcessor::getBiome)
    ).apply(builder, builder.stable(BiomeProcessor::new)));

    private final Holder<Biome> biome;

    public BiomeProcessor(Holder<Biome> biome) {
        this.biome = biome;
    }

    public Holder<Biome> getBiome() {
        return biome;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return WotrProcessors.BIOME.get();
    }

    @Override
    public void finalizeRoomProcessing(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize) {
        for (int x = structurePos.getX(); x < structurePos.getX() + pieceSize.getX(); x++) {
            for (int y = structurePos.getY(); y < structurePos.getY() + pieceSize.getY(); y++) {
                for (int z = structurePos.getZ(); z < structurePos.getZ() + pieceSize.getZ(); z++) {
                    room.setBiome(x, y, z, biome);
                }
            }
        }
    }
}
