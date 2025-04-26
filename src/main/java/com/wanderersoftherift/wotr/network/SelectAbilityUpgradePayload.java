package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.AbilityBenchMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Request to server to select which upgrade out of a choice to use for an ability
 * 
 * @param choice
 * @param selection
 */
public record SelectAbilityUpgradePayload(int choice, int selection) implements CustomPacketPayload {
    public static final Type<SelectAbilityUpgradePayload> TYPE = new Type<>(
            WanderersOfTheRift.id("select_ability_upgrade_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectAbilityUpgradePayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, SelectAbilityUpgradePayload::choice, ByteBufCodecs.INT,
                    SelectAbilityUpgradePayload::selection, SelectAbilityUpgradePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(IPayloadContext context) {
        if (context.player().containerMenu instanceof AbilityBenchMenu menu && menu.stillValid(context.player())) {
            menu.selectAbility(choice(), selection());
        }
    }
}
