package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseAbilityPayload(int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UseAbilityPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "ability_type"));

    public static final StreamCodec<ByteBuf, UseAbilityPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT,
            UseAbilityPayload::slot, UseAbilityPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        AbilitySlots abilitySlots = context.player().getData(ModAttachments.ABILITY_SLOTS);
        ItemStack abilityItem = abilitySlots.getStackInSlot(slot());
        if (abilityItem.isEmpty() || !abilityItem.has(ModDataComponentType.ABILITY)) {
            return;
        }
        AbstractAbility ability = abilityItem.get(ModDataComponentType.ABILITY).value();
        abilitySlots.setSelectedSlot(slot());

        if (ability.isToggle()) // Should check last toggle, because pressing a button can send multiple packets
        {
            if (!ability.isToggled(context.player())) {
                ability.onActivate(context.player(), slot(), abilityItem);
            } else {
                ability.onDeactivate(context.player(), slot());
            }

            if (ability.canPlayerUse(context.player())) {
                ability.toggle(context.player());
            }
        } else {
            ability.onActivate(context.player(), slot(), abilityItem);
        }
    }
}
