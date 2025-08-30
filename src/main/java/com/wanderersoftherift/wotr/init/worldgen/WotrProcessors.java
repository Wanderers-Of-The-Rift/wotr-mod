package com.wanderersoftherift.wotr.init.worldgen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.AttachmentProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.GradientReplaceProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.MushroomProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftChestProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftMobSpawnerProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.VineProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.WeightedReplaceProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.theme.ThemeProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister
            .create(Registries.STRUCTURE_PROCESSOR, WanderersOfTheRift.MODID);

    public static final Supplier<StructureProcessorType<ThemeProcessor>> RIFT_THEME = PROCESSORS.register("rift_theme",
            () -> () -> ThemeProcessor.CODEC);

    public static final Supplier<StructureProcessorType<GradientReplaceProcessor>> GRADIENT_SPOT_REPLACE = PROCESSORS
            .register("spot_gradient", () -> () -> GradientReplaceProcessor.CODEC);
    public static final Supplier<StructureProcessorType<WeightedReplaceProcessor>> WEIGHTED_REPLACE = PROCESSORS
            .register("weighted_replace", () -> () -> WeightedReplaceProcessor.CODEC);
    public static final Supplier<StructureProcessorType<AttachmentProcessor>> ATTACHMENT = PROCESSORS
            .register("attachment", () -> () -> AttachmentProcessor.CODEC);
    public static final Supplier<StructureProcessorType<VineProcessor>> VINES = PROCESSORS.register("vines",
            () -> () -> VineProcessor.CODEC);
    public static final Supplier<StructureProcessorType<MushroomProcessor>> MUSHROOMS = PROCESSORS.register("mushrooms",
            () -> () -> MushroomProcessor.CODEC);
    public static final Supplier<StructureProcessorType<RiftChestProcessor>> RIFT_CHESTS = PROCESSORS
            .register("rift_chests", () -> () -> RiftChestProcessor.CODEC);
    public static final Supplier<StructureProcessorType<RiftMobSpawnerProcessor>> RIFT_MOB_SPAWNER = PROCESSORS
            .register("trial_spawner", () -> () -> RiftMobSpawnerProcessor.CODEC);
}
