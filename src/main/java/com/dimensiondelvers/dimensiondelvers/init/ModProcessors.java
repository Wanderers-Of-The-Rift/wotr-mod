package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.world.level.levelgen.processor.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR,DimensionDelvers.MODID);

    public static final Supplier<StructureProcessorType<ThemeProcessor>> RIFT_THEME = PROCESSORS.register("rift_theme", () -> () -> ThemeProcessor.CODEC);

    public static final Supplier<StructureProcessorType<GradientReplaceProcessor>> GRADIENT_SPOT_REPLACE = PROCESSORS.register("spot_gradient", () -> () -> GradientReplaceProcessor.CODEC);
    public static final Supplier<StructureProcessorType<WeightedReplaceProcessor>> WEIGHTED_REPLACE = PROCESSORS.register("weighted_replace", () -> () -> WeightedReplaceProcessor.CODEC);
    public static final Supplier<StructureProcessorType<AttachmentProcessor>> ATTACHMENT = PROCESSORS.register("attachment", () -> () -> AttachmentProcessor.CODEC);
    public static final Supplier<StructureProcessorType<VineProcessor>> VINES = PROCESSORS.register("vines", () -> () -> VineProcessor.CODEC);
    public static final Supplier<StructureProcessorType<MushroomProcessor>> MUSHROOMS = PROCESSORS.register("mushrooms", () -> () -> MushroomProcessor.CODEC);
    public static final Supplier<StructureProcessorType<RiftChestProcessor>> RIFT_CHESTS = PROCESSORS.register("rift_chests", () -> () -> RiftChestProcessor.CODEC);
}
