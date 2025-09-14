package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import com.wanderersoftherift.wotr.util.LongRange;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.ABILITIES;

/**
 * Base type for all abilities.
 */
public interface Ability {

    Codec<Ability> DIRECT_CODEC = WotrRegistries.ABILITY_TYPES.byNameCodec()
            .dispatch(Ability::getCodec, Function.identity());
    Codec<Holder<Ability>> CODEC = LaxRegistryCodec.create(ABILITIES);
    StreamCodec<RegistryFriendlyByteBuf, Holder<Ability>> STREAM_CODEC = ByteBufCodecs.holderRegistry(ABILITIES);

    MapCodec<? extends Ability> getCodec();

    static Component getDisplayName(Holder<Ability> ability) {
        return ability.value().getDisplayName(ability.getKey().location());
    }

    static Component getDisplayName(Holder<Ability> ability, LivingEntity entity, AbilitySource source) {
        return ability.value().getDisplayName(ability.getKey().location(), entity, source);
    }

    default Component getDisplayName(ResourceLocation abilityId) {
        return Component.translatable(abilityId.toLanguageKey("ability"));
    }

    default Component getDisplayName(ResourceLocation abilityId, LivingEntity entity, AbilitySource source) {
        return getDisplayName(abilityId);
    }

    boolean isInCreativeMenu();

    /**
     * @return An icon for displaying the ability in the ability bar. Will also be used for the ability holder if there
     *         is no small icon
     */
    ResourceLocation getIcon();

    default ResourceLocation getIcon(LivingEntity entity, AbilitySource source) {
        return getIcon();
    }

    /**
     * @return An icon for displaying in the ability holder.
     */
    ResourceLocation getEmblem();

    /**
     * @param context
     * @return Whether the ability can be activated
     */
    default boolean canActivate(AbilityContext context) {
        return true;
    }

    /**
     * @param context
     * @return Whether the ability is finished
     */
    boolean activate(AbilityContext context);

    /**
     * Used to apply client-side effects
     * 
     * @param context
     */
    default void clientActivate(AbilityContext context) {
    }

    /**
     * Ticks the ability while it is active
     * 
     * @param context
     * @return Whether the ability is finished
     */
    default boolean tick(AbilityContext context) {
        return true;
    }

    default void deactivate(AbilityContext context) {
    }

    ///  Upgrade support

    boolean isRelevantModifier(ModifierEffect modifierEffect);

    /**
     * @return Is this ability interrupted by other actions
     */
    default boolean isChannelled() {
        return false;
    }

    default LongRange getCooldown(LivingEntity entity, AbilitySource abilitySource) {
        return entity.getData(WotrAttachments.ABILITY_COOLDOWNS).getCooldown(abilitySource);
    }

    default boolean isActive(LivingEntity entity, AbilitySource abilitySource) {
        return entity.getData(WotrAttachments.ABILITY_STATES).isActive(abilitySource);
    }
}
