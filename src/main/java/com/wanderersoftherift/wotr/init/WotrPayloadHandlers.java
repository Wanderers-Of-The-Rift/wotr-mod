package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffectData;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectMarker;
import com.wanderersoftherift.wotr.network.C2SRuneAnvilApplyPacket;
import com.wanderersoftherift.wotr.network.ability.AbilityCooldownUpdatePayload;
import com.wanderersoftherift.wotr.network.ability.AbilitySlotsContentPayload;
import com.wanderersoftherift.wotr.network.ability.AbilitySlotsCooldownsPayload;
import com.wanderersoftherift.wotr.network.ability.AbilitySlotsUpdatePayload;
import com.wanderersoftherift.wotr.network.ability.AbilityToggleStatePayload;
import com.wanderersoftherift.wotr.network.ability.LevelUpAbilityPayload;
import com.wanderersoftherift.wotr.network.ability.ManaChangePayload;
import com.wanderersoftherift.wotr.network.ability.SelectAbilitySlotPayload;
import com.wanderersoftherift.wotr.network.ability.SelectAbilityUpgradePayload;
import com.wanderersoftherift.wotr.network.ability.SetEffectMarkerPayload;
import com.wanderersoftherift.wotr.network.ability.UpdateEffectMarkersPayload;
import com.wanderersoftherift.wotr.network.ability.UseAbilityPayload;
import com.wanderersoftherift.wotr.network.guild.SelectTradePayload;
import com.wanderersoftherift.wotr.network.guild.WalletReplicationPayload;
import com.wanderersoftherift.wotr.network.guild.WalletUpdatePayload;
import com.wanderersoftherift.wotr.network.rift.BannedFromRiftPayload;
import com.wanderersoftherift.wotr.network.rift.S2CLevelListUpdatePacket;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Collections;
import java.util.Map;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class WotrPayloadHandlers {
    public static final String PROTOCOL_VERSION = "0.0.1";

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(WanderersOfTheRift.MODID).versioned(PROTOCOL_VERSION);

        // Ability
        registrar.playToServer(SelectAbilityUpgradePayload.TYPE, SelectAbilityUpgradePayload.STREAM_CODEC,
                SelectAbilityUpgradePayload::handleOnServer);
        registrar.playToServer(LevelUpAbilityPayload.TYPE, LevelUpAbilityPayload.STREAM_CODEC,
                LevelUpAbilityPayload::handleOnServer);
        registrar.playToServer(SelectAbilitySlotPayload.TYPE, SelectAbilitySlotPayload.STREAM_CODEC,
                SelectAbilitySlotPayload::handleOnServer);
        registrar.playToClient(AbilitySlotsContentPayload.TYPE, AbilitySlotsContentPayload.STREAM_CODEC,
                AbilitySlotsContentPayload::handleOnClient);
        registrar.playToClient(AbilitySlotsUpdatePayload.TYPE, AbilitySlotsUpdatePayload.STREAM_CODEC,
                AbilitySlotsUpdatePayload::handleOnClient);
        registrar.playToClient(AbilitySlotsCooldownsPayload.TYPE, AbilitySlotsCooldownsPayload.STREAM_CODEC,
                AbilitySlotsCooldownsPayload::handleOnClient);

        // Ability effect markers
        registrar.playToClient(SetEffectMarkerPayload.TYPE, SetEffectMarkerPayload.STREAM_CODEC,
                SetEffectMarkerPayload::handleOnClient);
        registrar.playToClient(UpdateEffectMarkersPayload.TYPE, UpdateEffectMarkersPayload.STREAM_CODEC,
                UpdateEffectMarkersPayload::handleOnClient);

        registrar.playToServer(UseAbilityPayload.TYPE, UseAbilityPayload.STREAM_CODEC,
                UseAbilityPayload::handleOnServer);
        registrar.playToClient(AbilityCooldownUpdatePayload.TYPE, AbilityCooldownUpdatePayload.STREAM_CODEC,
                AbilityCooldownUpdatePayload::handleOnClient);
        registrar.playToClient(AbilityToggleStatePayload.TYPE, AbilityToggleStatePayload.STREAM_CODEC,
                AbilityToggleStatePayload::handleOnClient);
        registrar.playToClient(ManaChangePayload.TYPE, ManaChangePayload.STREAM_CODEC,
                ManaChangePayload::handleOnClient);

        // Rift
        registrar.playToClient(BannedFromRiftPayload.TYPE, BannedFromRiftPayload.STREAM_CODEC,
                BannedFromRiftPayload::handleOnClient);
        registrar.playToClient(S2CRiftObjectiveStatusPacket.TYPE, S2CRiftObjectiveStatusPacket.STREAM_CODEC,
                new S2CRiftObjectiveStatusPacket.S2CRiftObjectiveStatusPacketHandler());
        registrar.playToServer(C2SRuneAnvilApplyPacket.TYPE, C2SRuneAnvilApplyPacket.STREAM_CODEC,
                new C2SRuneAnvilApplyPacket.C2SRuneAnvilApplyPacketHandler());
        registrar.playToClient(S2CLevelListUpdatePacket.TYPE, S2CLevelListUpdatePacket.STREAM_CODEC,
                new S2CLevelListUpdatePacket.S2CLevelListUpdatePacketHandler());

        // Guild
        registrar.playToClient(WalletReplicationPayload.TYPE, WalletReplicationPayload.STREAM_CODEC,
                WalletReplicationPayload::handleOnClient);
        registrar.playToClient(WalletUpdatePayload.TYPE, WalletUpdatePayload.STREAM_CODEC,
                WalletUpdatePayload::handleOnClient);
        registrar.playToServer(SelectTradePayload.TYPE, SelectTradePayload.STREAM_CODEC,
                SelectTradePayload::handleOnServer);
    }

    @SubscribeEvent
    public static void onPlayerJoinedEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            replicateEffectMarkers(player);
            replicateMana(player);
            replicateWallet(player);
            BannedFromRiftPayload.sendTo(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            replicateEffectMarkers(player);
            replicateWallet(player);
            BannedFromRiftPayload.sendTo(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            replicateEffectMarkers(player);
            replicateMana(player);
            replicateWallet(player);
            BannedFromRiftPayload.sendTo(player);
        }
    }

    private static void replicateMana(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new ManaChangePayload(player.getData(WotrAttachments.MANA).getAmount()));
    }

    private static void replicateAbilities(ServerPlayer player) {
        AbilitySlots abilitySlots = player.getData(WotrAttachments.ABILITY_SLOTS);
        PacketDistributor.sendToPlayer(player,
                new AbilitySlotsContentPayload(abilitySlots.getAbilitySlots(), abilitySlots.getSelectedSlot()));
        PacketDistributor.sendToPlayer(player,
                new AbilitySlotsCooldownsPayload(player.getData(WotrAttachments.ABILITY_COOLDOWNS)));
    }

    private static void replicateEffectMarkers(ServerPlayer player) {
        AttachedEffectData data = player.getData(WotrAttachments.ATTACHED_EFFECTS);
        Map<Holder<EffectMarker>, Integer> displayData = data.getDisplayData();
        if (!displayData.isEmpty()) {
            PacketDistributor.sendToPlayer(player,
                    new UpdateEffectMarkersPayload(displayData, Collections.emptyList()));
        }
    }

    private static void replicateWallet(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new WalletReplicationPayload(player.getData(WotrAttachments.WALLET)));
    }

}
