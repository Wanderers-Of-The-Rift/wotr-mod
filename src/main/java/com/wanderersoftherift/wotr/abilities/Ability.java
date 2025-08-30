package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
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

    private final List<AbilityRequirement> activationRequirements;

    public Ability(ResourceLocation icon, Optional<ResourceLocation> smallIcon,
            List<AbilityRequirement> activationRequirements) {
        this.icon = icon;
        this.smallIcon = smallIcon;
        this.activationRequirements = activationRequirements;
    }

    public static Component getDisplayName(Holder<Ability> ability) {
        return Component.translatable(ability.getKey().location().toLanguageKey("ability"));
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

    ///  Costs

    public List<AbilityRequirement> getActivationRequirements() {
        return activationRequirements;
    }

    ///  Upgrade support

    public boolean isRelevantModifier(AbstractModifierEffect modifierEffect) {
        return activationRequirements.stream().anyMatch(x -> x.isRelevant(modifierEffect));
    }

}
