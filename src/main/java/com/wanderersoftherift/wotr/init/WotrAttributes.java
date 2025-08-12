package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings("checkstyle:DeclarationOrder")
public class WotrAttributes {
    public static final DeferredRegister<Attribute> WOTR_ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE,
            WanderersOfTheRift.MODID);

    private static final List<DeferredHolder<Attribute, RangedAttribute>> PLAYER_ATTRIBUTES = new ArrayList<>();

    /* Ability Attributes */
    public static final DeferredHolder<Attribute, RangedAttribute> ABILITY_AOE = registerAttribute("ability_aoe",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.aoe"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> ABILITY_DAMAGE = registerAttribute(
            "ability_raw_damage",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.raw_damage"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> COOLDOWN = registerAttribute(
            "ability_cooldown",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.cooldown"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> HEAL_POWER = registerAttribute(
            "ability_heal_power",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.heal_amount"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> MANA_COST = registerAttribute(
            "mana_cost", () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.mana_cost"),
                    0, 0, Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> PROJECTILE_SPREAD = registerAttribute(
            "projectile_spread",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "projectile_spread"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> PROJECTILE_COUNT = registerAttribute(
            "projectile_count",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "projectile_count"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> PROJECTILE_SPEED = registerAttribute(
            "projectile_speed",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "projectile_speed"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> PROJECTILE_PIERCE = registerAttribute(
            "projectile_pierce",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "projectile_pierce"), 0, 0,
                    Integer.MAX_VALUE));

    /* Mana */
    public static final DeferredHolder<Attribute, RangedAttribute> MAX_MANA = registerAttribute("max_mana",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "max_mana"), 50, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> MANA_REGEN_RATE = registerAttribute(
            "mana_regen_rate",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "mana_regen_rate"), 0.05, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> MANA_DEGEN_RATE = registerAttribute(
            "mana_degen_rate",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "mana_degen_rate"), 0, 0,
                    Integer.MAX_VALUE));

    // Critical
    public static final DeferredHolder<Attribute, RangedAttribute> CRITICAL_CHANCE = registerAttribute(
            "critical_chance",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "critical_chance"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> CRITICAL_BONUS = registerAttribute(
            "critical_bonus", () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "critical_bonus"),
                    1.5, 0, Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> CRITICAL_AVOIDANCE = registerAttribute(
            "critical_avoidance",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "critical_avoidance"), 0, 0,
                    Integer.MAX_VALUE));

    // Thorns
    public static final DeferredHolder<Attribute, RangedAttribute> THORNS_CHANCE = registerAttribute(
            "thorns_chance", () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "thorns_chance"),
                    0, 0, Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> THORNS_DAMAGE = registerAttribute(
            "thorns_damage", () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "thorns_damage"),
                    0, 0, Integer.MAX_VALUE));

    // Life Leech
    public static final DeferredHolder<Attribute, RangedAttribute> LIFE_LEECH = registerAttribute(
            "life_leech",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "life_leech"), 0, 0, 1F));

    public static List<DeferredHolder<Attribute, RangedAttribute>> getWotrAttributes() {
        return Collections.unmodifiableList(PLAYER_ATTRIBUTES);

    }

    /*
     * This adds the different attributes to the player for the different abilities
     */

    private static DeferredHolder<Attribute, RangedAttribute> registerAttribute(
            final String name,
            final Supplier<? extends RangedAttribute> sup) {
        DeferredHolder<Attribute, RangedAttribute> result = WOTR_ATTRIBUTES.register(name, sup);
        PLAYER_ATTRIBUTES.add(result);
        return result;
    }

    @SubscribeEvent
    private static void addLivingAttribute(EntityAttributeModificationEvent event) {
        for (DeferredHolder<Attribute, RangedAttribute> attribute : WotrAttributes.PLAYER_ATTRIBUTES) {
            for (EntityType e : event.getTypes()) {
                event.add(e, attribute);
            }
        }
    }
}
