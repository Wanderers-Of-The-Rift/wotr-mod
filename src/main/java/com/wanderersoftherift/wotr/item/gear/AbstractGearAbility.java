package com.wanderersoftherift.wotr.item.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.effects.AbstractEffect;
import com.wanderersoftherift.wotr.codec.LaxRegistryCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.GEAR_ABILITIES;

public abstract class AbstractGearAbility {
    public static final Codec<AbstractGearAbility> DIRECT_CODEC = WotrRegistries.GEAR_ABILITY_TYPES.byNameCodec()
            .dispatch(AbstractGearAbility::getCodec, Function.identity());
    public static final Codec<Holder<AbstractGearAbility>> CODEC = LaxRegistryCodec.create(GEAR_ABILITIES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AbstractGearAbility>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(GEAR_ABILITIES);

    private final ResourceLocation name;

    private final List<AbstractEffect> effects;

    private Holder<Attribute> durationAttribute = null;
    private boolean isToggle = false;

    private Holder<Item> gearPiece;
    private ResourceLocation type;

    public AbstractGearAbility(ResourceLocation abilityName, Holder<Item> gear, List<AbstractEffect> effects,
            ResourceLocation type) {
        this.name = abilityName;
        this.gearPiece = gear;
        this.effects = effects;
        this.type = type;
    }

    public abstract MapCodec<? extends AbstractGearAbility> getCodec();

    public ResourceLocation getName() {
        return name;
    }

    public List<AbstractEffect> getEffects() {
        return this.effects;
    }

    public abstract void onActivate(Player player, ItemStack gear);

    public abstract void onDeactivate(Player player);

    public Holder<Item> getItem() {
        return gearPiece;
    }

    public ResourceLocation getType() {
        return type;
    }
}
