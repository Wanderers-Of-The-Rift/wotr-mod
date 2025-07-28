package com.wanderersoftherift.wotr.item.riftkey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record RiftGenerationConfig(Optional<RiftLayout.Factory> layout,
        Optional<RiftRoomGenerator.Factory> roomGenerator, boolean generatePassages,
        List<JigsawListProcessor> jigsawProcessors, Optional<Integer> seed) {

    public static final Codec<RiftGenerationConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RiftLayout.Factory.CODEC.optionalFieldOf("layout").forGetter(RiftGenerationConfig::layout),
                    RiftRoomGenerator.Factory.CODEC.optionalFieldOf("room_generator")
                            .forGetter(RiftGenerationConfig::roomGenerator),
                    Codec.BOOL.optionalFieldOf("passages", true).forGetter(RiftGenerationConfig::generatePassages),
                    JigsawListProcessor.CODEC.listOf()
                            .optionalFieldOf("jigsaw_processors", Collections.emptyList())
                            .forGetter(RiftGenerationConfig::jigsawProcessors),
                    Codec.INT.optionalFieldOf("seed").forGetter(RiftGenerationConfig::seed)
            ).apply(instance, RiftGenerationConfig::new));

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftGenerationConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.fromCodec(RiftLayout.Factory.CODEC).apply(ByteBufCodecs::optional), RiftGenerationConfig::layout,
                ByteBufCodecs.fromCodec(RiftRoomGenerator.Factory.CODEC).apply(ByteBufCodecs::optional), RiftGenerationConfig::roomGenerator,
                ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional).map(it->it.orElse(true), Optional::of), RiftGenerationConfig::generatePassages,
                ByteBufCodecs.fromCodec(JigsawListProcessor.CODEC).apply(ByteBufCodecs.list()), RiftGenerationConfig::jigsawProcessors,
                ByteBufCodecs.INT.apply(ByteBufCodecs::optional), RiftGenerationConfig::seed,
            RiftGenerationConfig::new);
    // spotless:on

    public static final RiftGenerationConfig EMPTY = new RiftGenerationConfig(Optional.empty(), Optional.empty(), true,
            Collections.emptyList(), Optional.empty());

    public RiftGenerationConfig withSeedIfAbsent(int seed) {
        if (this.seed.isPresent()) {
            return this;
        } else {
            return withSeed(seed);
        }
    }

    public RiftGenerationConfig withLayoutIfAbsent(RiftLayout.Factory layout) {
        if (this.layout.isPresent()) {
            return this;
        } else {
            return withLayout(layout);
        }
    }

    public RiftGenerationConfig withRoomGeneratorIfAbsent(RiftRoomGenerator.Factory generator) {
        if (this.roomGenerator.isPresent()) {
            return this;
        } else {
            return withRoomGenerator(generator);
        }
    }

    public RiftGenerationConfig withSeed(int seed) {
        return new RiftGenerationConfig(layout, roomGenerator, generatePassages, jigsawProcessors, Optional.of(seed));
    }

    public RiftGenerationConfig withLayout(RiftLayout.Factory layout) {
        return new RiftGenerationConfig(Optional.of(layout), roomGenerator, generatePassages, jigsawProcessors, seed);
    }

    public RiftGenerationConfig withRoomGenerator(RiftRoomGenerator.Factory roomGenerator) {
        return new RiftGenerationConfig(layout, Optional.of(roomGenerator), generatePassages, jigsawProcessors, seed);
    }

    public RiftGenerationConfig withJigsawProcessors(List<JigsawListProcessor> processors) {
        return new RiftGenerationConfig(layout, roomGenerator, generatePassages, processors, seed);
    }

    public RiftGenerationConfig withPassages() {
        return new RiftGenerationConfig(layout, roomGenerator, true, jigsawProcessors, seed);
    }

    public RiftGenerationConfig withoutPassages() {
        return new RiftGenerationConfig(layout, roomGenerator, false, jigsawProcessors, seed);
    }

}
