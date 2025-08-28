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

public record RiftGenerationConfig(RiftLayout.Factory layout, RiftRoomGenerator.Factory roomGenerator,
        List<RiftPostProcessingStep> postProcessingSteps, List<JigsawListProcessor> jigsawProcessors) {

    public static final Codec<RiftGenerationConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RiftLayout.Factory.CODEC.fieldOf("layout").forGetter(RiftGenerationConfig::layout),
                    RiftRoomGenerator.Factory.CODEC.fieldOf("room_generator")
                            .forGetter(RiftGenerationConfig::roomGenerator),
                    RiftPostProcessingStep.CODEC.listOf()
                            .fieldOf("post_processing_steps")
                            .forGetter(RiftGenerationConfig::postProcessingSteps),
                    JigsawListProcessor.CODEC.listOf()
                            .fieldOf("jigsaw_processors")
                            .forGetter(RiftGenerationConfig::jigsawProcessors)
            ).apply(instance, RiftGenerationConfig::new));

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftGenerationConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.fromCodec(RiftLayout.Factory.CODEC), RiftGenerationConfig::layout,
                ByteBufCodecs.fromCodec(RiftRoomGenerator.Factory.CODEC), RiftGenerationConfig::roomGenerator,
                ByteBufCodecs.fromCodec(RiftPostProcessingStep.CODEC).apply(ByteBufCodecs.list()), RiftGenerationConfig::postProcessingSteps,
                ByteBufCodecs.fromCodec(JigsawListProcessor.CODEC).apply(ByteBufCodecs.list()), RiftGenerationConfig::jigsawProcessors,
            RiftGenerationConfig::new);
    // spotless:on

    public RiftGenerationConfig withSeed(long seed) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, jigsawProcessors);
    }

    public RiftGenerationConfig withLayout(RiftLayout.Factory layout) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, jigsawProcessors);
    }

    public RiftGenerationConfig withRoomGenerator(RiftRoomGenerator.Factory roomGenerator) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, jigsawProcessors);
    }

    public RiftGenerationConfig withJigsawProcessors(List<JigsawListProcessor> processors) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, processors);
    }

    public RiftGenerationConfig withPostProcessingSteps(List<RiftPostProcessingStep> steps) {
        return new RiftGenerationConfig(layout, roomGenerator, steps, jigsawProcessors);
    }

}
