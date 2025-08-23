package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.ABILITIES;

/**
 * Base type for all abilities.
 */
public abstract class Ability {

    public static final Codec<Ability> DIRECT_CODEC = WotrRegistries.ABILITY_TYPES.byNameCodec()
            .dispatch(Ability::getCodec, Function.identity());
    public static final Codec<Holder<Ability>> CODEC = LaxRegistryCodec.create(ABILITIES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Ability>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(ABILITIES);

    private final ResourceLocation icon;
    private final Optional<ResourceLocation> smallIcon;

    private final int baseCooldown;
    private final List<AbilityRequirement> activationRequirements;
    private final List<AbilityRequirement> activationCosts;

    public Ability(ResourceLocation icon, Optional<ResourceLocation> smallIcon, int baseCooldown,
            List<AbilityRequirement> activationRequirements, List<AbilityRequirement> activationCosts) {
        this.icon = icon;
        this.smallIcon = smallIcon;
        this.baseCooldown = baseCooldown;
        this.activationRequirements = activationRequirements;
        this.activationCosts = activationCosts;
    }

    public static Component getDisplayName(Holder<Ability> ability) {
        return Component.translatable(ResourceLocation.parse(ability.getRegisteredName()).toLanguageKey("ability"));
    }

    public abstract MapCodec<? extends Ability> getCodec();

    /**
     * @return An icon for displaying the ability in the ability bar. Will also be used for the ability holder if there
     *         is no small icon
     */
    public ResourceLocation getIcon() {
        return icon;
    }

    /**
     * @return An icon for displaying in the ability holder.
     */
    public Optional<ResourceLocation> getSmallIcon() {
        return smallIcon;
    }

    /**
     * @param context
     * @return Whether the ability can be activated
     */
    public boolean canActivate(AbilityContext context) {
        return true;
    }

    /**
     * @param context
     * @return Whether the ability is finished
     */
    public abstract boolean activate(AbilityContext context);

    /**
     * Used to apply client-side effects
     * 
     * @param context
     */
    public void clientActivate(AbilityContext context) {

    }

    /**
     * Ticks the ability while it is active
     * 
     * @param contest
     * @param age
     * @return Whether the ability is finished
     */
    public boolean tick(AbilityContext contest, long age) {
        return true;
    }

    ///  Cooldown

    public int getBaseCooldown() {
        return baseCooldown;
    }

    ///  Costs

    public List<AbilityRequirement> getActivationRequirements() {
        return activationRequirements;
    }

    public List<AbilityRequirement> getActivationCosts() {
        return activationCosts;
    }

    ///  Upgrade support

    public boolean isRelevantModifier(AbstractModifierEffect modifierEffect) {
        if (getBaseCooldown() > 0 && modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.COOLDOWN.equals(attributeModifierEffect.getAttribute())) {
            return true;
        }
        if (activationCosts.stream().anyMatch(x -> x.isRelevantModifier(modifierEffect))) {
            return true;
        }
        return false;
    }

}
