package com.wanderersoftherift.wotr.abilities;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEnhancements;
import com.wanderersoftherift.wotr.abilities.effects.ConditionalEffect;
import com.wanderersoftherift.wotr.abilities.sources.AbilityEnhancementModifierSource;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import com.wanderersoftherift.wotr.modifier.effect.EnhanceAbilityModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.AbilityUpgradeModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.util.ExceptionlessAutoClosable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The context for processing an ability.
 *
 * @param instanceId     A unique id for this ability activation
 * @param ability        The ability itself
 * @param caster         The caster of the ability
 * @param abilityItem    The item holding the ability (and any upgrades)
 * @param source         The source the ability was provided by
 * @param level          The level the ability is present in
 * @param age            How long the ability has been active
 * @param upgrades
 * @param enhancements   Extra modifiers added by {@link EnhanceAbilityModifierEffect}
 * @param conditions     Conditions added for changing effects of {@link ConditionalEffect}
 * @param dataComponents Data component map for the context
 */
// TODO: What should we convert to be data components?
public record AbilityContext(UUID instanceId, Holder<Ability> ability, @NotNull LivingEntity caster,
        ItemStack abilityItem, AbilitySource source, Level level, long age,
        @NotNull List<Holder<AbilityUpgrade>> upgrades, @Nonnull List<EnhancingModifierInstance> enhancements,
        Set<ResourceLocation> conditions, PatchedDataComponentMap dataComponents)
        implements MutableDataComponentHolder {

    public AbilityContext(Holder<Ability> ability, LivingEntity caster, AbilitySource source) {
        this(UUID.randomUUID(), ability, caster, source.getItem(caster), source, caster.level(), 0,
                source.upgrades(caster), AbilityEnhancements.forEntity(caster).modifiers(ability), new HashSet<>(),
                new PatchedDataComponentMap(DataComponentMap.EMPTY));
    }

    /**
     * @return The current game time
     */
    public long age() {
        return age;
    }

    /**
     * Enables all modifiers that impact the ability
     */
    public ExceptionlessAutoClosable enableTemporaryUpgradeModifiers() {
        enhancements.forEach(enhancement -> {
            enhancement.modifier()
                    .modifier()
                    .value()
                    .enableModifier(enhancement.modifier().roll(), caster, new AbilityEnhancementModifierSource(
                            enhancement.originalSource(), enhancement.originalIndex()), enhancement.modifier().tier());
        });
        if (upgrades.isEmpty()) {
            if (enhancements.isEmpty()) {
                return ExceptionlessAutoClosable.NOOP;
            } else {
                return this::disableEnhancements;
            }
        }
        for (int index = 0; index < upgrades.size(); index++) {
            ModifierSource source = new AbilityUpgradeModifierSource(source(), index);
            AbilityUpgrade upgrade = upgrades.get(index).value();
            List<ModifierEffect> modifierEffects = upgrade.modifierEffects();
            for (int i = 0; i < modifierEffects.size(); i++) {
                ModifierEffect effect = modifierEffects.get(i);
                effect.enableModifier(0, caster, source, i);
            }
        }
        return this::disableUpgradeModifiers;
    }

    /**
     * Used to get the value of an attribute for use by the ability
     *
     * @param attributeHolder The attribute to get the value of
     * @param baseValue       The base value of the attribute
     * @return The final total value of the attribute
     */
    public float getAbilityAttribute(Holder<Attribute> attributeHolder, float baseValue) {
        AttributeInstance attribute = caster.getAttribute(attributeHolder);
        if (attribute == null) {
            return 0;
        }

        AttributeModifier baseModifier = new AttributeModifier(WanderersOfTheRift.id("base_value"), baseValue,
                AttributeModifier.Operation.ADD_VALUE);
        attribute.addTransientModifier(baseModifier);
        float value = (float) attribute.getValue();
        attribute.removeModifier(baseModifier);
        return value;
    }

    /**
     * Disables all modifiers from {@link #enhancements()}
     */
    private void disableEnhancements() {
        enhancements.forEach(enhancement -> {
            enhancement.modifier()
                    .modifier()
                    .value()
                    .disableModifier((float) enhancement.modifier().roll(), caster,
                            new AbilityEnhancementModifierSource(
                                    enhancement.originalSource(), enhancement.originalIndex()),
                            enhancement.modifier().tier());
        });
    }

    /**
     * Disables all modifiers that were enabled by {@link #enableTemporaryUpgradeModifiers()}
     */
    private void disableUpgradeModifiers() {
        disableEnhancements();
        for (int index = 0; index < upgrades.size(); index++) {
            ModifierSource source = new AbilityUpgradeModifierSource(source(), index);
            AbilityUpgrade upgrade = upgrades.get(index).value();
            List<ModifierEffect> modifierEffects = upgrade.modifierEffects();
            for (int i = 0; i < modifierEffects.size(); i++) {
                ModifierEffect effect = modifierEffects.get(i);
                effect.disableModifier(0, caster, source, i);
            }
        }
    }

    /**
     * @param newAbility
     * @param newSource
     * @return A new context for a new ability and source, carrying across all other values
     */
    public AbilityContext forSubAbility(Holder<Ability> newAbility, AbilitySource newSource) {
        return new AbilityContext(instanceId, newAbility, caster, abilityItem, newSource, level, 0, upgrades,
                enhancements, conditions, dataComponents.copy());
    }

    /// Data Component Holder methods

    @Override
    public <T> @Nullable T set(@NotNull DataComponentType<? super T> componentType, @Nullable T value) {
        return dataComponents.set(componentType, value);
    }

    @Override
    public <T> @Nullable T remove(@NotNull DataComponentType<? extends T> componentType) {
        return dataComponents.remove(componentType);
    }

    @Override
    public void applyComponents(@NotNull DataComponentPatch patch) {
        dataComponents.applyPatch(patch);
    }

    @Override
    public void applyComponents(@NotNull DataComponentMap components) {
        dataComponents.setAll(components);
    }

    @Override
    public @NotNull DataComponentMap getComponents() {
        return dataComponents;
    }
}
