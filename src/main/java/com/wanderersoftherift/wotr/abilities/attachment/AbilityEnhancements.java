package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.effect.EnhanceAbilityModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbilityEnhancements {

    private final Map<Holder<Ability>, LinkedHashMap<AbilityEnhancementKey, AbilityEnhancementValue>> effects = new HashMap<>();

    public static AbilityEnhancements forEntity(IAttachmentHolder entity) {
        return entity.getData(WotrAttachments.ABILITY_ENHANCEMENTS);
    }

    public List<EnhancingModifier> modifiers(Holder<Ability> ability) {
        var submap = effects.get(ability);
        if (submap != null) {
            return submap.entrySet()
                    .stream()
                    .map(it -> new EnhancingModifier(it.getValue().modifier().modifier(), it.getValue().roll,
                            it.getValue().modifier().tier(), it.getKey().source(), it.getKey().effectIndex))
                    .toList();
        }
        return Collections.emptyList();
    }

    public void putEnhancement(
            ModifierSource source,
            int effectIndex,
            EnhanceAbilityModifierEffect enhancement,
            double roll) {
        enhancement.abilities().forEach(ability -> {
            effects.computeIfAbsent(ability, (unused) -> new LinkedHashMap<>())
                    .put(
                            new AbilityEnhancementKey(source, effectIndex),
                            new AbilityEnhancementValue(enhancement, roll)
                    );
        });
    }

    public void removeEnhancement(ModifierSource source, int effectIndex, EnhanceAbilityModifierEffect enhancement) {
        enhancement.abilities().forEach(ability -> {
            var submap = effects.get(ability);
            if (submap != null) {
                submap.remove(new AbilityEnhancementKey(source, effectIndex));
            }
        });
    }

    public record AbilityEnhancementValue(EnhanceAbilityModifierEffect modifier, double roll) {
    }

    public record AbilityEnhancementKey(ModifierSource source, int effectIndex) {

    }

    public record EnhancingModifier(Holder<Modifier> modifier, double roll, int tier, ModifierSource originalSource,
            int originalIndex) {
        public static final Codec<EnhancingModifier> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Modifier.CODEC.fieldOf("modifier").forGetter(EnhancingModifier::modifier),
                        Codec.DOUBLE.fieldOf("roll").forGetter(EnhancingModifier::roll),
                        Codec.INT.fieldOf("tier").forGetter(EnhancingModifier::tier),
                        ModifierSource.DIRECT_CODEC.fieldOf("original_source")
                                .forGetter(EnhancingModifier::originalSource),
                        Codec.INT.fieldOf("effect_index").forGetter(EnhancingModifier::originalIndex)
                ).apply(instance, EnhancingModifier::new)
        );

    }
}
