package com.wanderersoftherift.wotr.world.level.levelgen.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.ArrayList;

import static com.wanderersoftherift.wotr.init.ModRiftThemes.RIFT_THEME_KEY;

public record RiftTheme(Holder<StructureProcessorList> processors) {
    public static final Codec<RiftTheme> DIRECT_CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(RiftTheme::processors)
            ).apply(builder, RiftTheme::new));

    public static final Codec<RiftTheme> DIRECT_SYNC_CODEC = Codec.unit(RiftTheme::new);

    public static final Codec<Holder<RiftTheme>> CODEC = RegistryFixedCodec.create(RIFT_THEME_KEY);

    public RiftTheme() {
        this(Holder.direct(new StructureProcessorList(new ArrayList<>())));
    }
}
