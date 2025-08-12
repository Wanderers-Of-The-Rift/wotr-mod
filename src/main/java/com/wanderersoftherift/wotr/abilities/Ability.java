package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.ABILITIES;

public abstract class Ability {

    public static final Codec<Ability> DIRECT_CODEC = WotrRegistries.ABILITY_TYPES.byNameCodec()
            .dispatch(Ability::getCodec, Function.identity());
    public static final Codec<Holder<Ability>> CODEC = LaxRegistryCodec.create(ABILITIES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Ability>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(ABILITIES);

    private ResourceLocation icon;
    private Optional<ResourceLocation> smallIcon;

    private int baseCooldown = 0;
    private int baseManaCost;

    public Ability(ResourceLocation icon, Optional<ResourceLocation> smallIcon, int baseCooldown) {
        this.icon = icon;
        this.smallIcon = smallIcon;
        this.baseCooldown = baseCooldown;
    }

    public static Component getDisplayName(Holder<Ability> ability) {
        return Component.translatable(ResourceLocation.parse(ability.getRegisteredName()).toLanguageKey("ability"));
    }

    public abstract MapCodec<? extends Ability> getCodec();

    public void setIcon(ResourceLocation location) {
        icon = location;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public Optional<ResourceLocation> getSmallIcon() {
        return smallIcon;
    }

    public int getBaseManaCost() {
        return baseManaCost;
    }

    public void setBaseManaCost(int baseManaCost) {
        this.baseManaCost = baseManaCost;
    }

    public abstract boolean onActivate(LivingEntity caster, ItemStack abilityItem, @Nullable WotrEquipmentSlot slot);

    // TODO: Any use case for this?
    public boolean canUse(LivingEntity caster) {
        return true;
    }

    public int getBaseCooldown() {
        return baseCooldown;
    }

    public abstract boolean isRelevantModifier(AbstractModifierEffect modifierEffect);
}
