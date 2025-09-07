package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.EnhancingModifierInstance;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.effect.EnhanceAbilityModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AbilityEnhancements {

    private final Table<Holder<Ability>, AbilityEnhancementKey, AbilityEnhancementValue> effects = Tables
            .newCustomTable(new HashMap<>(), LinkedHashMap::new);

    public static AbilityEnhancements forEntity(IAttachmentHolder entity) {
        return entity.getData(WotrAttachments.ABILITY_ENHANCEMENTS);
    }

    public List<EnhancingModifierInstance> modifiers(Holder<Ability> ability) {
        if (!effects.containsRow(ability)) {
            return Collections.emptyList();
        }
        return effects.row(ability)
                .entrySet()
                .stream()
                .map(it -> EnhancingModifierInstance.create(it.getValue(), it.getKey()))
                .toList();
    }

    public void putEnhancement(
            ModifierSource source,
            int effectIndex,
            EnhanceAbilityModifierEffect enhancement,
            double roll) {
        enhancement.abilities().forEach(ability -> {
            effects.put(ability, new AbilityEnhancementKey(source, effectIndex),
                    new AbilityEnhancementValue(enhancement, roll));
        });
    }

    public void removeEnhancement(ModifierSource source, int effectIndex, EnhanceAbilityModifierEffect enhancement) {
        enhancement.abilities().forEach(ability -> {
            effects.remove(ability, new AbilityEnhancementKey(source, effectIndex));
        });
    }

    public record AbilityEnhancementValue(EnhanceAbilityModifierEffect modifier, double roll) {
    }

    public record AbilityEnhancementKey(ModifierSource source, int effectIndex) {

    }

}
