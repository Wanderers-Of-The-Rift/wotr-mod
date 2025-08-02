package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.effects.AbstractEffect;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.ABILITIES;

public abstract class AbstractAbility {

    public static final Codec<AbstractAbility> DIRECT_CODEC = WotrRegistries.ABILITY_TYPES.byNameCodec()
            .dispatch(AbstractAbility::getCodec, Function.identity());
    public static final Codec<Holder<AbstractAbility>> CODEC = LaxRegistryCodec.create(ABILITIES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AbstractAbility>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(ABILITIES);

    private ResourceLocation icon = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");
    private Optional<ResourceLocation> smallIcon = Optional.empty();

    private final List<AbstractEffect> effects;
    private int baseCooldown = 0;
    private int baseManaCost;

    private Holder<Attribute> durationAttribute = null;
    private boolean isToggle = false;

    public AbstractAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, List<AbstractEffect> effects,
            int baseCooldown) {
        this.effects = effects;
        this.icon = icon;
        this.smallIcon = smallIcon;
        this.baseCooldown = baseCooldown;
    }

    public static Component getDisplayName(Holder<AbstractAbility> ability) {
        return Component.translatable(ResourceLocation.parse(ability.getRegisteredName()).toLanguageKey("ability"));
    }

    public abstract MapCodec<? extends AbstractAbility> getCodec();

    public void setIcon(ResourceLocation location) {
        icon = location;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public Optional<ResourceLocation> getSmallIcon() {
        return smallIcon;
    }

    public List<AbstractEffect> getEffects() {
        return this.effects;
    }

    public int getBaseManaCost() {
        return baseManaCost;
    }

    public void setBaseManaCost(int baseManaCost) {
        this.baseManaCost = baseManaCost;
    }

    public abstract boolean onActivate(LivingEntity caster, ItemStack abilityItem);

    // TODO: Any use case for this?
    public boolean canUse(LivingEntity caster) {
        return true;
    }

    public int getBaseCooldown() {
        return baseCooldown;
    }

    /*
     * TOGGLE STUFF BELOW
     */
    public boolean isToggle() {
        return this.isToggle;
    }

    public void setIsToggle(boolean shouldToggle) {
        this.isToggle = shouldToggle;
    }

    public boolean isToggled(Player player) {
//        return ModAbilities.TOGGLE_ATTACHMENTS.containsKey(this.getName()) && p.getData(ModAbilities.TOGGLE_ATTACHMENTS.get(this.getName()));
        return false;
    }

    public void toggle(Player player) {
        // Change the toggle to opposite and then tell the player
//        if(TOGGLE_ATTACHMENTS.containsKey(this.getName())) p.setData(TOGGLE_ATTACHMENTS.get(this.getName()), !IsToggled(p));
//        PacketDistributor.sendToPlayer((ServerPlayer) p, new ToggleState(this.getName().toString(), IsToggled(p)));
    }

    public boolean isRelevantModifier(AbstractModifierEffect modifierEffect) {
        if (modifierEffect instanceof AttributeModifierEffect attributeModifierEffect) {
            Holder<Attribute> attribute = attributeModifierEffect.getAttribute();
            if (WotrAttributes.COOLDOWN.equals(attribute) && baseCooldown > 0) {
                return true;
            }
            if (WotrAttributes.MANA_COST.equals(attribute) && baseManaCost > 0) {
                return true;
            }
        }
        for (AbstractEffect effect : effects) {
            if (effect.isRelevant(modifierEffect)) {
                return true;
            }
        }
        return false;
    }
}
