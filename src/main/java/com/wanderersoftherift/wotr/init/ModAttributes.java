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
public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE,
            WanderersOfTheRift.MODID);

    /* Ability Attributes */
    public static final DeferredHolder<Attribute, RangedAttribute> ABILITY_AOE = registerPlayerAttribute("ability_aoe",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.aoe"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> ABILITY_DAMAGE = registerPlayerAttribute(
            "ability_raw_damage",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.raw_damage"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> COOLDOWN = registerPlayerAttribute(
            "ability_cooldown",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.cooldown"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> HEAL_POWER = registerPlayerAttribute(
            "ability_heal_power",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.heal_amount"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> MANA_COST = registerPlayerAttribute("mana_cost",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "ability.mana_cost"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> PROJECTILE_SPREAD = registerPlayerAttribute(
            "projectile_spread",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "projectile_spread"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> PROJECTILE_COUNT = registerPlayerAttribute(
            "projectile_count",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "projectile_count"), 0, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> PROJECTILE_SPEED = registerPlayerAttribute(
            "projectile_speed",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "projectile_speed"), 0, 0,
                    Integer.MAX_VALUE));

    /* Mana */
    public static final DeferredHolder<Attribute, RangedAttribute> MAX_MANA = registerPlayerAttribute("max_mana",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "max_mana"), 50, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> MANA_REGEN_RATE = registerPlayerAttribute(
            "mana_regen_rate",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "mana_regen_rate"), 0.05, 0,
                    Integer.MAX_VALUE));
    public static final DeferredHolder<Attribute, RangedAttribute> MANA_DEGEN_RATE = registerPlayerAttribute(
            "mana_degen_rate",
            () -> new RangedAttribute(WanderersOfTheRift.translationId("attribute", "mana_degen_rate"), 0, 0,
                    Integer.MAX_VALUE));

    private static final List<DeferredHolder<Attribute, RangedAttribute>> PLAYER_ATTRIBUTES = new ArrayList<>();

    public static List<DeferredHolder<Attribute, RangedAttribute>> getPlayerAttributes() {
        return Collections.unmodifiableList(PLAYER_ATTRIBUTES);
    }

    /*
     * This adds the different attributes to the player for the different abilities
     */
    @SubscribeEvent
    private static void addAttributesToPlayer(EntityAttributeModificationEvent event) {
        for (DeferredHolder<Attribute, RangedAttribute> attribute : PLAYER_ATTRIBUTES) {
            event.add(EntityType.PLAYER, attribute);
        }
    }

    private static DeferredHolder<Attribute, RangedAttribute> registerPlayerAttribute(final String name,
            final Supplier<? extends RangedAttribute> sup) {
        DeferredHolder<Attribute, RangedAttribute> result = ATTRIBUTES.register(name, sup);
        PLAYER_ATTRIBUTES.add(result);
        return result;
    }
}
