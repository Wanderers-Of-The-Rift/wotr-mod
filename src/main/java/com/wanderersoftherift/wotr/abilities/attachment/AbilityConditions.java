package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class AbilityConditions {

    private final Multimap<Holder<Ability>, ResourceLocation> conditions = Multimaps.newMultimap(new HashMap<>(),
            () -> Collections.newSetFromMap(new LinkedHashMap<>()));

    public static AbilityConditions forEntity(IAttachmentHolder entity) {
        return entity.getData(WotrAttachments.ABILITY_CONDITIONS.get());
    }

    public Set<ResourceLocation> condition(Holder<Ability> ability) {
        return ImmutableSet.copyOf(conditions.get(ability));
    }

    public void addCondition(Holder<Ability> ability, ResourceLocation condition) {
        conditions.put(ability, condition);
    }

    public void removeCondition(Holder<Ability> ability, ResourceLocation condition) {
        conditions.remove(ability, condition);
    }

}
