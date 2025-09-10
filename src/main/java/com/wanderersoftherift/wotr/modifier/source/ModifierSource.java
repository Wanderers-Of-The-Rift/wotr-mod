package com.wanderersoftherift.wotr.modifier.source;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ModifierSource extends StringRepresentable {

    Codec<ModifierSource> DIRECT_CODEC = WotrRegistries.MODIFIER_SOURCES.byNameCodec()
            .dispatch(ModifierSource::getType, DualCodec::codec);
    StreamCodec<RegistryFriendlyByteBuf, ModifierSource> STREAM_CODEC = ByteBufCodecs
            .registry(WotrRegistries.Keys.MODIFIER_SOURCES)
            .dispatch(ModifierSource::getType, DualCodec::streamCodec);

    DualCodec<? extends ModifierSource> getType();

    List<AbstractModifierEffect> getModifierEffects(Entity entity);

    interface ItemModifierSource extends ModifierSource {
        ItemStack getItem(Entity entity);
    }

    interface SlotModifierSource extends ItemModifierSource {

        WotrEquipmentSlot slot();

        default ItemStack getItem(Entity entity) {
            return slot().getContent(entity);
        }
    }
}
