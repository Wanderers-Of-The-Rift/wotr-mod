package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record CooldownCost(int ticks, int milliticks, int margin, Holder<Attribute> cooldownAttribute,
        CooldownMode mode) implements AbilityRequirement {

    public static final MapCodec<CooldownCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("ticks", 20).forGetter(CooldownCost::ticks),
            Codec.INT.optionalFieldOf("milliticks", 0).forGetter(CooldownCost::ticks),
            Codec.INT.optionalFieldOf("margin_milliticks", 1000).forGetter(CooldownCost::margin),
            Attribute.CODEC.optionalFieldOf("cooldown_attribute", WotrAttributes.COOLDOWN)
                    .forGetter(CooldownCost::cooldownAttribute),
            CooldownMode.CODEC.optionalFieldOf("mode", CooldownMode.NORMAL).forGetter(CooldownCost::mode)
    ).apply(instance, CooldownCost::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        return !context.caster().getData(WotrAttachments.ABILITY_COOLDOWNS).isOnCooldown(context.source(), margin);
    }

    @Override
    public void pay(AbilityContext context) {
        var cooldownData = context.caster().getData(WotrAttachments.ABILITY_COOLDOWNS);
        var cooldown = cooldownData.remainingCooldown(context.source());
        double newCooldown = switch (mode) {
            case NORMAL -> context.getAbilityAttribute(cooldownAttribute, ticks + 0.001f * milliticks) * 1000;
            case INVERTED -> {
                var ticksFractional = ticks + 0.001f * milliticks;
                yield 20_000f / context.getAbilityAttribute(cooldownAttribute, 20f / ticksFractional);
            }
        };

        cooldownData.setCooldown(context.source(), (int) (newCooldown + cooldown));
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return ticks > 0 && modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.COOLDOWN.equals(attributeModifierEffect.attribute());
    }

    public enum CooldownMode implements StringRepresentable {
        /**
         * in this mode the cooldown attribute is interpreted as time between casts cooldownFinal = cooldownAttribute +
         * abilityCooldown
         */
        NORMAL("normal"),
        /**
         * in this mode the cooldown attribute is interpreted as casting speed - increasing it reduces cooldown
         * cooldownFinal = 1 / (cooldownAttribute + 1/abilityCooldown)
         */
        INVERTED("inverted");

        private static final Map<String, CooldownMode> ENTRIES = Arrays.stream(values())
                .collect(Collectors.toUnmodifiableMap(value -> value.name, value -> value));

        public static final Codec<CooldownMode> CODEC = Codec.STRING.xmap(ENTRIES::get,
                CooldownMode::getSerializedName);

        private final String name;

        CooldownMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
