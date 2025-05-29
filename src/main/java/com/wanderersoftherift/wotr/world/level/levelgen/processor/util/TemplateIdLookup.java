package com.wanderersoftherift.wotr.world.level.levelgen.processor.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public interface TemplateIdLookup {
    ResourceLocation idForTemplate(StructureTemplate template);
}
