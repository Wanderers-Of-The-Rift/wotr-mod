package com.wanderersoftherift.wotr.world.level.levelgen.theme;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RiftTheme(Map<ThemePieceType, Holder<StructureProcessorList>> processors) {
    public static final Codec<RiftTheme> DIRECT_CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.mapPair(ThemePieceType.CODEC.fieldOf("piece_type"), StructureProcessorType.LIST_CODEC.fieldOf("processors")).codec().listOf()
                            .xmap(RiftTheme::fromPairList, RiftTheme::fromMap).fieldOf("processors").forGetter(RiftTheme::processors)
            ).apply(builder, RiftTheme::new));

    public static final Codec<RiftTheme> DIRECT_SYNC_CODEC = Codec.unit(RiftTheme::new);

    public static final Codec<Holder<RiftTheme>> CODEC = RegistryFixedCodec.create(ModRiftThemes.RIFT_THEME_KEY);

    public RiftTheme() {
        this(Map.of());
    }

    public List<StructureProcessor> getProcessors(ThemePieceType pieceType){
        if(processors.containsKey(pieceType)) {
            return processors.get(pieceType).value().list();
        }else{
            return List.of();
        }
    }

    private static Map<ThemePieceType, Holder<StructureProcessorList>> fromPairList(List<Pair<ThemePieceType, Holder<StructureProcessorList>>> pairList){
        return pairList.stream().collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
    }

    private static List<Pair<ThemePieceType, Holder<StructureProcessorList>>> fromMap(Map<ThemePieceType, Holder<StructureProcessorList>> map){
        return map.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).toList();
    }
}
