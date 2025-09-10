package com.wanderersoftherift.wotr.mixinextension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public interface TemplateIdLookup {
    ResourceLocation wotr$idForTemplate(StructureTemplate template);
}
