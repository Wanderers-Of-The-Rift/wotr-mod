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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ModModifierProvider {

    public static Map<ResourceKey<Modifier>, Modifier> DATA = new LinkedHashMap<>();

    public static void bootstrapModifiers(BootstrapContext<Modifier> context) {
        registerModifier(context, getResourceKey("ability_aoe"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, 1F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("ability_aoe"),
                                        ModAttributes.ABILITY_AOE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("ability_cooldown"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, -0.1F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("ability_cooldown"),
                                        ModAttributes.COOLDOWN, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("ability_damage_flat"),
                new Modifier(generateEqualRollSpread(7,
                        List.of(new ToBeTieredModifierEffect(0, 7F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("ability_damage_flat"),
                                        ModAttributes.ABILITY_DAMAGE, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("ability_damage_heavy"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, 10F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("ability_damage_heavy"),
                                        ModAttributes.ABILITY_DAMAGE, AttributeModifier.Operation.ADD_VALUE))),
                        List.of(new AttributeModifierEffect(
                                WanderersOfTheRift.id("ability_damage_heavy"), ModAttributes.COOLDOWN, 0.5, 1F,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE))))
        );
        registerModifier(context, getResourceKey("ability_damage_percent"),
                new Modifier(generateEqualRollSpread(5, List.of(new ToBeTieredModifierEffect(0, 1F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("ability_damage_percent"),
                                ModAttributes.ABILITY_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("ability_heal_power_flat"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, 10F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("ability_heal_power_flat"),
                                        ModAttributes.HEAL_POWER, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("ability_heal_power_percent"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, 1F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("ability_heal_power_percent"),
                                        ModAttributes.HEAL_POWER, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("absorption"),
                new Modifier(generateEqualRollSpread(4,
                        List.of(new ToBeTieredModifierEffect(0, 6,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("absorption"),
                                        Attributes.MAX_ABSORPTION, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("armor"),
                new Modifier(generateEqualRollSpread(4,
                        List.of(new ToBeTieredModifierEffect(2, 20,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("armor"), Attributes.MAX_ABSORPTION,
                                        AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("armor_heavy"), new Modifier(generateEqualRollSpread(
                4, List
                        .of(new ToBeTieredModifierEffect(8, 30,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("armor_heavy"),
                                        Attributes.MAX_ABSORPTION, AttributeModifier.Operation.ADD_VALUE))),
                List.of(new AttributeModifierEffect(
                        WanderersOfTheRift.id("armor_heavy"), Attributes.MOVEMENT_SPEED, -0.03F, -0.001F,
                        AttributeModifier.Operation.ADD_VALUE))))
        );
        registerModifier(context, getResourceKey("armor_toughness"),
                new Modifier(generateEqualRollSpread(2,
                        List.of(new ToBeTieredModifierEffect(0, 4,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("armor_toughness"),
                                        Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("attack_flat"),
                new Modifier(generateEqualRollSpread(7,
                        List.of(new ToBeTieredModifierEffect(0, 11,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("attack_flat"),
                                        Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("attack_heavy"), new Modifier(generateEqualRollSpread(5, List.of(
                new ToBeTieredModifierEffect(0, 2F, attributeModifierEffectGetter(WanderersOfTheRift.id("attack_heavy"),
                        Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))),
                List.of(new AttributeModifierEffect(
                        WanderersOfTheRift.id("attack_heavy"), Attributes.ATTACK_SPEED, -1, -0.5F,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE))))
        );
        registerModifier(context, getResourceKey("attack_percent"),
                new Modifier(generateEqualRollSpread(7,
                        List.of(new ToBeTieredModifierEffect(0, 1F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("attack_percent"),
                                        Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("attack_speed"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, 1,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("attack_speed"),
                                        Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("health_flat"),
                new Modifier(generateEqualRollSpread(6,
                        List.of(new ToBeTieredModifierEffect(0, 12,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("health_flat"),
                                        Attributes.MAX_HEALTH, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("health_hybrid"), new Modifier(generateEqualRollSpread(5, List.of(
                new ToBeTieredModifierEffect(0, 5,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("health_flat"), Attributes.MAX_HEALTH,
                                AttributeModifier.Operation.ADD_VALUE)),
                new ToBeTieredModifierEffect(0, 0.1F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("health_hybrid"), Attributes.MAX_HEALTH,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("health_percent"),
                new Modifier(generateEqualRollSpread(6,
                        List.of(new ToBeTieredModifierEffect(0, 0.2F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("health_percent"),
                                        Attributes.MAX_HEALTH, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("jump_height"), new Modifier(generateEqualRollSpread(3, List.of(
                new ToBeTieredModifierEffect(0, 0.5F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("jump_height"), Attributes.JUMP_STRENGTH,
                                AttributeModifier.Operation.ADD_VALUE)),
                new ToBeTieredModifierEffect(0, 4,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("fall_reduction"),
                                Attributes.SAFE_FALL_DISTANCE, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("knockback"),
                new Modifier(generateEqualRollSpread(3,
                        List.of(new ToBeTieredModifierEffect(0, 7.5F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("knockback"),
                                        Attributes.ATTACK_KNOCKBACK, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("knockback_heavy"),
                new Modifier(generateEqualRollSpread(3,
                        List.of(new ToBeTieredModifierEffect(0, 15F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("knockback_heavy"),
                                        Attributes.ATTACK_KNOCKBACK, AttributeModifier.Operation.ADD_VALUE))),
                        List.of(new AttributeModifierEffect(
                                WanderersOfTheRift.id("knockback_heavy"), Attributes.ATTACK_SPEED, -1, -0.5F,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE))))
        );
        registerModifier(context, getResourceKey("knockback_resistance"),
                new Modifier(generateEqualRollSpread(3,
                        List.of(new ToBeTieredModifierEffect(0, 1.5F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("knockback_resistance"),
                                        Attributes.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("mana_cost"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, -0.1F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("mana_cost"),
                                        ModAttributes.MANA_COST, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("mana_degen_rate"),
                new Modifier(generateEqualRollSpread(5, List.of(new ToBeTieredModifierEffect(0, 0.25F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("mana_degen_rate"),
                                ModAttributes.MANA_DEGEN_RATE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("mana_regen_rate"),
                new Modifier(generateEqualRollSpread(5, List.of(new ToBeTieredModifierEffect(0, 0.25F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("mana_regen_rate"),
                                ModAttributes.MANA_REGEN_RATE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("max_mana_flat"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, 50F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("max_mana_flat"),
                                        ModAttributes.MAX_MANA, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("max_mana_percent"),
                new Modifier(generateEqualRollSpread(5,
                        List.of(new ToBeTieredModifierEffect(0, 0.25F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("max_mana_percent"),
                                        ModAttributes.MAX_MANA, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("movement_speed"),
                new Modifier(generateEqualRollSpread(3,
                        List.of(new ToBeTieredModifierEffect(0, 0.1F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("movement_speed"),
                                        Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("projectile_count"),
                new Modifier(generateEqualRollSpread(2, List.of(new ToBeTieredModifierEffect(0, 0.1F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("projectile_count"),
                                ModAttributes.PROJECTILE_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("projectile_pierce"),
                new Modifier(generateEqualRollSpread(2, List.of(new ToBeTieredModifierEffect(0, 0.1F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("projectile_pierce"),
                                ModAttributes.PROJECTILE_PIERCE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("projectile_speed"),
                new Modifier(generateEqualRollSpread(5, List.of(new ToBeTieredModifierEffect(0, 0.1F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("projectile_speed"),
                                ModAttributes.PROJECTILE_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("projectile_spread"),
                new Modifier(generateEqualRollSpread(5, List.of(new ToBeTieredModifierEffect(0, -0.1F,
                        attributeModifierEffectGetter(WanderersOfTheRift.id("projectile_spread"),
                                ModAttributes.PROJECTILE_SPREAD, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))))
        );
        registerModifier(context, getResourceKey("step_height"),
                new Modifier(generateEqualRollSpread(1,
                        List.of(new ToBeTieredModifierEffect(0.5F, 0.5F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("step_height"),
                                        Attributes.STEP_HEIGHT, AttributeModifier.Operation.ADD_VALUE)))))
        );
        registerModifier(context, getResourceKey("sweeping_attack_damage"),
                new Modifier(generateEqualRollSpread(4,
                        List.of(new ToBeTieredModifierEffect(0, 1F,
                                attributeModifierEffectGetter(WanderersOfTheRift.id("sweeping_attack_damage"),
                                        Attributes.SWEEPING_DAMAGE_RATIO, AttributeModifier.Operation.ADD_VALUE)))))
        );
    }

    private static List<ModifierTier> generateEqualRollSpread(
            int tiers,
            List<ToBeTieredModifierEffect> toBeTieredModifierEffectList,
            List<AbstractModifierEffect> untieredModifiers) {
        List<ModifierTier> modifierTiers = new ArrayList<>();
        for (int i = 0; i < tiers; i++) {
            List<AbstractModifierEffect> modifierEffects = new ArrayList<>();
            for (ToBeTieredModifierEffect toBeTieredModifierEffect : toBeTieredModifierEffectList) {
                AbstractModifierEffect modifierEffectTier = getTieredModifierEffect(tiers, i, toBeTieredModifierEffect);
                modifierEffects.add(modifierEffectTier);
                modifierEffects.addAll(untieredModifiers);
            }
            modifierTiers.add(new ModifierTier(i + 1, modifierEffects));
        }
        return modifierTiers;
    }

    private static List<ModifierTier> generateEqualRollSpread(
            int tiers,
            List<ToBeTieredModifierEffect> toBeTieredModifierEffectList) {
        return generateEqualRollSpread(tiers, toBeTieredModifierEffectList, List.of());
    }

    private static AbstractModifierEffect getTieredModifierEffect(
            int tiers,
            int tier,
            ToBeTieredModifierEffect toBeTieredModifierEffect) {
        float stepSize = (toBeTieredModifierEffect.totalMaxRoll - toBeTieredModifierEffect.totalMinRoll)
                / (float) tiers;
        float minRoll = Math.round((toBeTieredModifierEffect.totalMinRoll + (stepSize * tier)) * 20) / 20F;
        float maxRoll = Math.round((toBeTieredModifierEffect.totalMinRoll + (stepSize * (tier + 1))) * 20) / 20F;
        return toBeTieredModifierEffect.buildModifierEffect.apply(minRoll, maxRoll);
    }

    private static BiFunction<Float, Float, AbstractModifierEffect> attributeModifierEffectGetter(
            ResourceLocation id,
            Holder<Attribute> attribute,
            AttributeModifier.Operation operation) {
        return (minRoll, maxRoll) -> new AttributeModifierEffect(id, attribute, minRoll, maxRoll, operation);
    }

    private static @NotNull ResourceKey<Modifier> getResourceKey(String id) {
        return ResourceKey.create(ModDatapackRegistries.MODIFIER_KEY, WanderersOfTheRift.id(id));
    }

    private record ToBeTieredModifierEffect(float totalMinRoll, float totalMaxRoll,
            BiFunction<Float, Float, AbstractModifierEffect> buildModifierEffect) {
    }

    private static void registerModifier(
            BootstrapContext<Modifier> context,
            ResourceKey<Modifier> resourceKey,
            Modifier modifier) {
        DATA.put(resourceKey, modifier);
        context.register(resourceKey, modifier);
    }
}