package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.util.TemplateIdLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;

@Mixin(StructureTemplateManager.class)
public class MixinStructureTemplateManager implements TemplateIdLookup {
    @Shadow @Final private Map<ResourceLocation, Optional<StructureTemplate>> structureRepository;

    @Override
    public ResourceLocation idForTemplate(StructureTemplate template) {
        return structureRepository
                .entrySet()
                .stream()
                .filter((it)->it.getValue().isPresent() && it.getValue().get()==template )
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
