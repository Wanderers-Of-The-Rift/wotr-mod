package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings("checkstyle:DeclarationOrder")
public class WotrAttributes {
    public static final DeferredRegister<Attribute> WOTR_ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE,
            WanderersOfTheRift.MODID);

    private static final List<DeferredHolder<Attribute, RangedAttribute>> ENTITY_ATTRIBUTES = new ArrayList<>();
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
            () -> (RangedAttribute) new RangedAttribute(WanderersOfTheRift.translationId("attribute", "max_mana"), 50,
                    0, Integer.MAX_VALUE).setSyncable(true));
    public static final DeferredHolder<Attribute, RangedAttribute> MANA_REGEN_RATE = registerAttribute(
            "mana_regen_rate", () -> (RangedAttribute) new RangedAttribute(
                    WanderersOfTheRift.translationId("attribute", "mana_regen_rate"), 0.05, 0, Integer.MAX_VALUE)
                    .setSyncable(true));
    public static final DeferredHolder<Attribute, RangedAttribute> MANA_DEGEN_RATE = registerAttribute(
            "mana_degen_rate", () -> (RangedAttribute) new RangedAttribute(
                    WanderersOfTheRift.translationId("attribute", "mana_degen_rate"), 0, 0, Integer.MAX_VALUE)
                    .setSyncable(true));

    public static final DeferredHolder<Attribute, RangedAttribute> MAX_GOBLIN = registerAttribute("max_goblin",
            () -> (RangedAttribute) new RangedAttribute(WanderersOfTheRift.translationId("attribute", "max_goblin"), 50,
                    0, Integer.MAX_VALUE).setSyncable(true));
    public static final DeferredHolder<Attribute, RangedAttribute> GOBLIN_REGEN_RATE = registerAttribute(
            "goblin_regen_rate", () -> (RangedAttribute) new RangedAttribute(
                    WanderersOfTheRift.translationId("attribute", "goblin_regen_rate"), 0.05, 0, Integer.MAX_VALUE)
                    .setSyncable(true));
    public static final DeferredHolder<Attribute, RangedAttribute> GOBLIN_DEGEN_RATE = registerAttribute(
            "goblin_degen_rate", () -> (RangedAttribute) new RangedAttribute(
                    WanderersOfTheRift.translationId("attribute", "goblin_degen_rate"), 0.00, 0, Integer.MAX_VALUE)
                    .setSyncable(true));

    public static final DeferredHolder<Attribute, RangedAttribute> MAX_BLOOD = registerAttribute("max_blood",
            () -> (RangedAttribute) new RangedAttribute(WanderersOfTheRift.translationId("attribute", "max_blood"), 50,
                    0, Integer.MAX_VALUE).setSyncable(true));
    public static final DeferredHolder<Attribute, RangedAttribute> BLOOD_REGEN_RATE = registerAttribute(
            "blood_regen_rate", () -> (RangedAttribute) new RangedAttribute(
                    WanderersOfTheRift.translationId("attribute", "blood_regen_rate"), 0.05, 0, Integer.MAX_VALUE)
                    .setSyncable(true));
    public static final DeferredHolder<Attribute, RangedAttribute> BLOOD_DEGEN_RATE = registerAttribute(
            "blood_degen_rate", () -> (RangedAttribute) new RangedAttribute(
                    WanderersOfTheRift.translationId("attribute", "blood_degen_rate"), 0.00, 0, Integer.MAX_VALUE)
                    .setSyncable(true));

    /// Combat

    public static final DeferredHolder<Attribute, RangedAttribute> RANGED_ATTACK_DAMAGE = registerAttribute(
            "ranged_attack_damage",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ranged_attack_damage"), 0, 0,
                    2048.0));

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

    public static final DeferredHolder<Attribute, RangedAttribute> THORNS_CHANCE = registerAttribute(
            "thorns_chance", () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "thorns_chance"),
                    0, 0, Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> THORNS_DAMAGE = registerAttribute(
            "thorns_damage", () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "thorns_damage"),
                    0, 0, Integer.MAX_VALUE));

    public static final DeferredHolder<Attribute, RangedAttribute> LIFE_LEECH = registerAttribute(
            "life_leech",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "life_leech"), 0, 0, 1F));

    /// Primary stats

    public static final DeferredHolder<Attribute, RangedAttribute> STRENGTH = registerAttribute(
            "strength",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "strength"), 0, 0, 100));
    public static final DeferredHolder<Attribute, RangedAttribute> CONSTITUTION = registerAttribute(
            "constitution",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "constitution"), 0, 0, 100));
    public static final DeferredHolder<Attribute, RangedAttribute> DEXTERITY = registerAttribute(
            "dexterity",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "dexterity"), 0, 0, 100));
    public static final DeferredHolder<Attribute, RangedAttribute> CHARISMA = registerAttribute(
            "charisma",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "charisma"), 0, 0, 100));
    public static final DeferredHolder<Attribute, RangedAttribute> INTELLIGENCE = registerAttribute(
            "intelligence",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "intelligence"), 0, 0, 100));
    public static final DeferredHolder<Attribute, RangedAttribute> WISDOM = registerAttribute(
            "wisdom", () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "wisdom"), 0, 0, 100));

    /*
     * This adds the different attributes to the player for the different abilities
     */

    private static DeferredHolder<Attribute, RangedAttribute> registerAttribute(
            final String name,
            final Supplier<? extends RangedAttribute> sup) {
        DeferredHolder<Attribute, RangedAttribute> result = WOTR_ATTRIBUTES.register(name, sup);
        ENTITY_ATTRIBUTES.add(result);
        return result;
    }

    private static DeferredHolder<Attribute, RangedAttribute> registerPlayerAttribute(
            final String name,
            final Supplier<? extends RangedAttribute> sup) {
        DeferredHolder<Attribute, RangedAttribute> result = WOTR_ATTRIBUTES.register(name, sup);
        PLAYER_ATTRIBUTES.add(result);
        return result;
    }

    @SubscribeEvent
    private static void addLivingAttribute(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> e : event.getTypes()) {
            for (DeferredHolder<Attribute, RangedAttribute> attribute : WotrAttributes.ENTITY_ATTRIBUTES) {
                event.add(e, attribute);
            }
            if (EntityType.PLAYER.equals(e)) {
                for (DeferredHolder<Attribute, RangedAttribute> attribute : WotrAttributes.PLAYER_ATTRIBUTES) {
                    event.add(e, attribute);
                }
            }
        }
    }
}
