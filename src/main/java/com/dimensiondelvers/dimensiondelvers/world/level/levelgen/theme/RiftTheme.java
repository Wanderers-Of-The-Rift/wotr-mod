package com.dimensiondelvers.dimensiondelvers.world.level.levelgen.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import static com.dimensiondelvers.dimensiondelvers.init.ModRiftThemes.RIFT_THEME_KEY;

public class RiftTheme {
    public static final Codec<RiftTheme> DIRECT_CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(RiftTheme::getProcessors)
            ).apply(builder, RiftTheme::new));

    public static final Codec<Holder<RiftTheme>> CODEC = RegistryFixedCodec.create(RIFT_THEME_KEY);

    private final Holder<StructureProcessorList> processors;

    public RiftTheme(Holder<StructureProcessorList> processors) {
        this.processors = processors;
    }

    public Holder<StructureProcessorList> getProcessors() {
        return processors;
    }
}
