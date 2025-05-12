package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.ModifierTier;
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

public class ModModifierProvider {
    public static void bootstrapModifiers(BootstrapContext<Modifier> context) {
        context.register(getResourceKey("flat_attack"),
                new Modifier(generateAbilitySpread(WanderersOfTheRift.id("flat_attack"), Attributes.ATTACK_DAMAGE, 7, 0, 11, AttributeModifier.Operation.ADD_VALUE))
                );
        context.register(getResourceKey("absorption"),
                new Modifier(generateAbilitySpread(WanderersOfTheRift.id("absorption"), Attributes.MAX_ABSORPTION, 4, 0, 6, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("armor"),
                new Modifier(generateAbilitySpread(WanderersOfTheRift.id("armor"), Attributes.MAX_ABSORPTION, 4, 2, 20, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("armor_toughness"),
                new Modifier(generateAbilitySpread(WanderersOfTheRift.id("armor_toughness"), Attributes.ARMOR_TOUGHNESS, 2, 0, 4, AttributeModifier.Operation.ADD_VALUE))
        );
        context.register(getResourceKey("attack_speed"),
                new Modifier(generateAbilitySpread(WanderersOfTheRift.id("attack_speed"), Attributes.ATTACK_SPEED, 5, 0, 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("health"),
                new Modifier(generateAbilitySpread(WanderersOfTheRift.id("health"), Attributes.MAX_HEALTH, 6, 0, 12, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
        context.register(getResourceKey("knockback_resistance"),
                new Modifier(generateAbilitySpread(WanderersOfTheRift.id("knockback_resistance"), Attributes.KNOCKBACK_RESISTANCE, 3, 0, 1.5F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
        );
    }

    //TODO: Properly round this.
    private static List<ModifierTier> generateAbilitySpread(ResourceLocation id, Holder<Attribute> attribute, int tiers, float minRoll, float maxRoll, AttributeModifier.Operation operation) {
        List<ModifierTier> modifierTiers = new ArrayList<>();
        float stepSize = (maxRoll - minRoll) / (float) tiers;
        for(int i = 0; i < tiers; i++) {
            modifierTiers.add(new ModifierTier(i+1, List.of(new AttributeModifierEffect(
                    id,
                    attribute,
                    Math.round(minRoll + (stepSize * i)),
                    Math.ceil(minRoll + (stepSize * (i+1))),
                    operation
            ))));
        }
        return modifierTiers;
    }

    private static @NotNull ResourceKey<Modifier> getResourceKey(String id) {
        return ResourceKey.create(ModDatapackRegistries.MODIFIER_KEY, WanderersOfTheRift.id(id));
    }
}
