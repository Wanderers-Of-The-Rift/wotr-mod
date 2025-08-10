package com.wanderersoftherift.wotr.abilities;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.source.AbilityUpgradeModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The context for processing an ability.
 *
 * @param instanceId  A unique id for this ability activation
 * @param ability     The ability itself
 * @param caster      The caster of the ability
 * @param abilityItem The item holding the ability (and any upgrades)
 * @param slot        The equipment slot the ability item is in, if any
 */
public record AbilityContext(UUID instanceId, Holder<Ability> ability, @NotNull LivingEntity caster,
        ItemStack abilityItem, WotrEquipmentSlot slot) {

    /**
     * @return The level the ability was used within
     */
    public Level level() {
        return caster.level();
    }

    /**
     * @return The current game time
     */
    public long gameTime() {
        return level().getGameTime();
    }

    /**
     * Enables all modifiers that impact the ability
     */
    public void enableUpgradeModifiers() {
        AbilityUpgradePool pool = abilityItem.get(WotrDataComponentType.ABILITY_UPGRADE_POOL);
        if (pool != null) {
            pool.forEachSelected((selection, upgrade) -> {
                ModifierSource source = new AbilityUpgradeModifierSource(selection);
                upgrade.modifierEffects().forEach(effect -> effect.enableModifier(0, caster, source));
            });
        }
    }

    /**
     * Disables all modifiers that were enabled by {@link #enableUpgradeModifiers()}
     */
    public void disableUpgradeModifiers() {
        AbilityUpgradePool pool = abilityItem.get(WotrDataComponentType.ABILITY_UPGRADE_POOL);
        if (pool != null) {
            pool.forEachSelected((selection, upgrade) -> {
                ModifierSource source = new AbilityUpgradeModifierSource(selection);
                upgrade.modifierEffects().forEach(effect -> effect.disableModifier(0, caster, source));
            });
        }
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
}
