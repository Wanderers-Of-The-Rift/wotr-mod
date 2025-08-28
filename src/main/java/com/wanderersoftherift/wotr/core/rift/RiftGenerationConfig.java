package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftPostProcessingStep;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;

public record RiftGenerationConfig(Optional<RiftLayout.Factory> layout,
        Optional<RiftRoomGenerator.Factory> roomGenerator, Optional<List<RiftPostProcessingStep>> postProcessingSteps,
        Optional<List<JigsawListProcessor>> jigsawProcessors) {

    public static final Codec<RiftGenerationConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RiftLayout.Factory.CODEC.optionalFieldOf("layout").forGetter(RiftGenerationConfig::layout),
                    RiftRoomGenerator.Factory.CODEC.optionalFieldOf("room_generator")
                            .forGetter(RiftGenerationConfig::roomGenerator),
                    RiftPostProcessingStep.CODEC.listOf()
                            .optionalFieldOf("post_processing_steps")
                            .forGetter(RiftGenerationConfig::postProcessingSteps),
                    JigsawListProcessor.CODEC.listOf()
                            .optionalFieldOf("jigsaw_processors")
                            .forGetter(RiftGenerationConfig::jigsawProcessors)
            ).apply(instance, RiftGenerationConfig::new));

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftGenerationConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.fromCodec(RiftLayout.Factory.CODEC).apply(ByteBufCodecs::optional), RiftGenerationConfig::layout,
                ByteBufCodecs.fromCodec(RiftRoomGenerator.Factory.CODEC).apply(ByteBufCodecs::optional), RiftGenerationConfig::roomGenerator,
                ByteBufCodecs.fromCodec(RiftPostProcessingStep.CODEC).apply(ByteBufCodecs.list()).apply(ByteBufCodecs::optional), RiftGenerationConfig::postProcessingSteps,
                ByteBufCodecs.fromCodec(JigsawListProcessor.CODEC).apply(ByteBufCodecs.list()).apply(ByteBufCodecs::optional), RiftGenerationConfig::jigsawProcessors,
            RiftGenerationConfig::new);
    // spotless:on

    public static final RiftGenerationConfig EMPTY = new RiftGenerationConfig(Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty());

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

    public RiftGenerationConfig withJigsawProcessorsIfAbsent(List<JigsawListProcessor> processors) {
        if (this.jigsawProcessors.isPresent()) {
            return this;
        } else {
            return withJigsawProcessors(processors);
        }
    }

    public RiftGenerationConfig withCorridorBlenderIfAbsent(List<RiftPostProcessingStep> steps) {
        if (this.roomGenerator.isPresent()) {
            return this;
        } else {
            return withPostProcessingSteps(steps);
        }
    }

    public RiftGenerationConfig withSeed(long seed) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, jigsawProcessors);
    }

    public RiftGenerationConfig withLayout(RiftLayout.Factory layout) {
        return new RiftGenerationConfig(Optional.of(layout), roomGenerator, postProcessingSteps, jigsawProcessors);
    }

    public RiftGenerationConfig withRoomGenerator(RiftRoomGenerator.Factory roomGenerator) {
        return new RiftGenerationConfig(layout, Optional.of(roomGenerator), postProcessingSteps, jigsawProcessors);
    }

    public RiftGenerationConfig withJigsawProcessors(List<JigsawListProcessor> processors) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, Optional.of(processors));
    }

    public RiftGenerationConfig withPostProcessingSteps(List<RiftPostProcessingStep> steps) {
        return new RiftGenerationConfig(layout, roomGenerator, Optional.of(steps), jigsawProcessors);
    }

}
