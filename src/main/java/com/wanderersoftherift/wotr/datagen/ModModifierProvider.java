package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModAttributes;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.ModifierTier;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ModModifierProvider {
    public static void bootstrapModifiers(BootstrapContext<Modifier> context) {
        context.register(getResourceKey("flat_attack"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("flat_attack"), 7, Attributes.ATTACK_DAMAGE, 0, 11, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("percent_attack"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("percent_attack"), 7, Attributes.ATTACK_DAMAGE, 0, 1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("absorption"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("absorption"), 4, Attributes.MAX_ABSORPTION, 0, 6, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("armor"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("armor"), 4, Attributes.MAX_ABSORPTION, 2, 20, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("armor_toughness"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("armor_toughness"), 2, Attributes.ARMOR_TOUGHNESS, 0, 4, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("attack_speed"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("attack_speed"), 5, Attributes.ATTACK_SPEED, 0, 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("flat_health"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("flat_health"), 6, Attributes.MAX_HEALTH, 0, 12, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("percent_health"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("percent_health"), 6, Attributes.MAX_HEALTH, 0, 0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("knockback_resistance"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("knockback_resistance"), 3, Attributes.KNOCKBACK_RESISTANCE, 0, 1.5F, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("knockback"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("knockback"), 3, Attributes.ATTACK_KNOCKBACK, 0, 7.5F, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("movement_speed"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("movement_speed"), 3, Attributes.JUMP_STRENGTH, 0, 0.35F, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("step_height"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("step_height"), 1, Attributes.STEP_HEIGHT, 0.5F, 0.5F, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("sweeping_attack_damage"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("sweeping_attack_damage"), 4, Attributes.SWEEPING_DAMAGE_RATIO, 0, 1F, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("ability_aoe"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("ability_aoe"), 5, ModAttributes.ABILITY_AOE, 0, 1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("flat_ability_damage"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("ability_damage"), 7, ModAttributes.ABILITY_DAMAGE, 0, 7F, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("percent_ability_damage"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("percent_ability_damage"), 5, ModAttributes.ABILITY_DAMAGE, 0, 1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("flat_ability_heal_power"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("flat_ability_heal_power"), 5, ModAttributes.HEAL_POWER, 0, 10F, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("percent_ability_heal_power"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("percent_ability_heal_power"), 5, ModAttributes.HEAL_POWER, 0, 1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("ability_cooldown"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("ability_cooldown"), 5, ModAttributes.COOLDOWN, 0, -0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("mana_cost"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("mana_cost"), 5, ModAttributes.MANA_COST, 0, -0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("flat_max_mana"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("flat_max_mana"), 5, ModAttributes.MAX_MANA, 0, 50F, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("percent_max_mana"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("percent_max_mana"), 5, ModAttributes.MAX_MANA, 0, 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("mana_regen_rate"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("mana_regen_rate"), 5, ModAttributes.MANA_REGEN_RATE, 0, 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("mana_degen_rate"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("mana_degen_rate"), 5, ModAttributes.MANA_DEGEN_RATE, 0, 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("projectile_spread"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("projectile_spread"), 5, ModAttributes.PROJECTILE_SPREAD, 0, -0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("projectile_speed"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("projectile_speed"), 5, ModAttributes.PROJECTILE_SPEED, 0, 0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("projectile_count"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("projectile_count"), 2, ModAttributes.PROJECTILE_SPEED, 0, 0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("projectile_pierce"),
                new Modifier(generateEqualRollSpread(WanderersOfTheRift.id("projectile_pierce"), 2, ModAttributes.PROJECTILE_PIERCE, 0, 0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
    }

    private static List<ModifierTier> generateEqualRollSpread(ResourceLocation id, int tiers, Holder<Attribute> attribute, float totalMinRoll, float totalMaxRoll, AttributeModifier.Operation operation) {
        List<ModifierTier> modifierTiers = new ArrayList<>();
        float stepSize = (totalMaxRoll - totalMinRoll) / (float) tiers;
        for (int i = 0; i < tiers; i++) {
            AttributeModifierEffect modifierEffectTier = getTieredModifierEffect(id, attribute, totalMinRoll, operation, stepSize, i);
            List<AbstractModifierEffect> modifierEffects = List.of(modifierEffectTier);
            modifierTiers.add(new ModifierTier(i + 1, modifierEffects));
        }
        return modifierTiers;
    }

    private static @NotNull AttributeModifierEffect getTieredModifierEffect(ResourceLocation id, Holder<Attribute> attribute, float totalMinRoll, AttributeModifier.Operation operation, float stepSize, int i) {
        float minRoll = Math.round((totalMinRoll + (stepSize * i)) * 20) / 20F;
        float maxRoll = Math.round((totalMinRoll + (stepSize * (i + 1))) * 20) / 20F;
        AttributeModifierEffect modifierEffectTier = new AttributeModifierEffect(
                id,
                attribute,
                minRoll,
                maxRoll,
                operation
        );
        return modifierEffectTier;
    }

    private BiFunction<Double, Double, AbstractModifierEffect> attributeModifierEffectGetter(ResourceLocation id, Holder<Attribute> attribute, AttributeModifier.Operation operation) {
        return (minRoll, maxRoll) -> new AttributeModifierEffect(id, attribute, minRoll, maxRoll, operation);
    }

    private static @NotNull ResourceKey<Modifier> getResourceKey(String id) {
        return ResourceKey.create(ModDatapackRegistries.MODIFIER_KEY, WanderersOfTheRift.id(id));
    }
}