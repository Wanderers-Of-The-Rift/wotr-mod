package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftPostProcessingStep;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.template.SerializableRiftGeneratable;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record RiftGenerationConfig(RiftLayout.Factory layout, RiftRoomGenerator.Factory roomGenerator,
        List<RiftPostProcessingStep> postProcessingSteps, List<JigsawListProcessor> jigsawProcessors,
        SerializableRiftGeneratable emptyChunkGeneratable) {

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
                            .forGetter(RiftGenerationConfig::jigsawProcessors),
                    SerializableRiftGeneratable.BUILTIN_GENERATABLE_CODEC.fieldOf("empty_chunk_generatable")
                            .forGetter(RiftGenerationConfig::emptyChunkGeneratable)
            ).apply(instance, RiftGenerationConfig::new));
    public static final Codec<Holder<RiftGenerationConfig>> HOLDER_CODEC = LaxRegistryCodec
            .refOrDirect(WotrRegistries.Keys.GENERATOR_PRESETS, CODEC);

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftGenerationConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.fromCodec(RiftLayout.Factory.CODEC), RiftGenerationConfig::layout,
                ByteBufCodecs.fromCodec(RiftRoomGenerator.Factory.CODEC), RiftGenerationConfig::roomGenerator,
                ByteBufCodecs.fromCodec(RiftPostProcessingStep.CODEC).apply(ByteBufCodecs.list()), RiftGenerationConfig::postProcessingSteps,
                ByteBufCodecs.fromCodec(JigsawListProcessor.CODEC).apply(ByteBufCodecs.list()), RiftGenerationConfig::jigsawProcessors,
                ByteBufCodecs.fromCodec(SerializableRiftGeneratable.BUILTIN_GENERATABLE_CODEC), RiftGenerationConfig::emptyChunkGeneratable,
            RiftGenerationConfig::new);
    // spotless:on

    public static final RiftConfigDataType<RiftGenerationConfig> TYPE = RiftConfigDataType.create(CODEC,
            RiftGenerationConfig::initialize);

    private static final ResourceKey<RiftGenerationConfig> DEFAULT_PRESET_KEY = ResourceKey
            .create(WotrRegistries.Keys.GENERATOR_PRESETS, WanderersOfTheRift.id("default"));

    public static RiftGenerationConfig initialize(ItemStack itemStack, Long unused, RegistryAccess registries) {
        var preset = itemStack.get(WotrDataComponentType.RiftConfigComponents.GENERATOR_PRESET);
        if (preset == null) {
            preset = registries.holderOrThrow(DEFAULT_PRESET_KEY);
        }
        var config = preset.value();
        var layerEdits = itemStack.get(WotrDataComponentType.RiftConfigComponents.LAYOUT_LAYER_EDIT);
        if (layerEdits != null && config.layout() instanceof LayeredRiftLayout.Factory layered) {
            var layers = layered.layers();
            for (var edit : layerEdits) {
                layers = edit.apply(layers);
            }
            config = config.withLayout(layered.withLayers(layers));
        }
        var jigsawEdits = itemStack.get(WotrDataComponentType.RiftConfigComponents.JIGSAW_PROCESSORS_EDIT);
        if (jigsawEdits != null) {
            var jigsaws = config.jigsawProcessors();
            for (var edit : jigsawEdits) {
                jigsaws = edit.apply(jigsaws);
            }
            config = config.withJigsawProcessors(jigsaws);
        }
        var postStepsEdits = itemStack.get(WotrDataComponentType.RiftConfigComponents.POST_STEPS_EDIT);
        if (postStepsEdits != null) {
            var postSteps = config.postProcessingSteps();
            for (var edit : postStepsEdits) {
                postSteps = edit.apply(postSteps);
            }
            config = config.withPostProcessingSteps(postSteps);
        }
        return config;
    }

    public RiftGenerationConfig withLayout(RiftLayout.Factory layout) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, jigsawProcessors,
                emptyChunkGeneratable);
    }

    public RiftGenerationConfig withRoomGenerator(RiftRoomGenerator.Factory roomGenerator) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, jigsawProcessors,
                emptyChunkGeneratable);
    }

    public RiftGenerationConfig withJigsawProcessors(List<JigsawListProcessor> processors) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, processors, emptyChunkGeneratable);
    }

    public RiftGenerationConfig withPostProcessingSteps(List<RiftPostProcessingStep> steps) {
        return new RiftGenerationConfig(layout, roomGenerator, steps, jigsawProcessors, emptyChunkGeneratable);
    }

    public RiftGenerationConfig withEmptyChunkGeneratable(SerializableRiftGeneratable emptyChunkGeneratable) {
        return new RiftGenerationConfig(layout, roomGenerator, postProcessingSteps, jigsawProcessors,
                emptyChunkGeneratable);
    }

}
