package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffectData;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectMarker;
import com.wanderersoftherift.wotr.network.AbilityCooldownUpdatePayload;
import com.wanderersoftherift.wotr.network.AbilitySlotsContentPayload;
import com.wanderersoftherift.wotr.network.AbilitySlotsCooldownsPayload;
import com.wanderersoftherift.wotr.network.AbilitySlotsUpdatePayload;
import com.wanderersoftherift.wotr.network.AbilityToggleStatePayload;
import com.wanderersoftherift.wotr.network.BannedFromRiftPayload;
import com.wanderersoftherift.wotr.network.LevelUpAbilityPayload;
import com.wanderersoftherift.wotr.network.ManaChangePayload;
import com.wanderersoftherift.wotr.network.SelectAbilitySlotPayload;
import com.wanderersoftherift.wotr.network.SelectAbilityUpgradePayload;
import com.wanderersoftherift.wotr.network.SetEffectMarkerPayload;
import com.wanderersoftherift.wotr.network.UpdateEffectMarkersPayload;
import com.wanderersoftherift.wotr.network.UseAbilityPayload;
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
public class ModPayloadHandlers {
    public static final String PROTOCOL_VERSION = "0.0.1";

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(WanderersOfTheRift.MODID).versioned(PROTOCOL_VERSION);

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

        registrar.playToClient(BannedFromRiftPayload.TYPE, BannedFromRiftPayload.STREAM_CODEC,
                BannedFromRiftPayload::handleOnClient);
    }

    @SubscribeEvent
    public static void onPlayerJoinedEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            replicateEffectMarkers(player);
            replicateMana(player);
            BannedFromRiftPayload.sendTo(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            replicateEffectMarkers(player);
            BannedFromRiftPayload.sendTo(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            replicateEffectMarkers(player);
            replicateMana(player);
            BannedFromRiftPayload.sendTo(player);
        }
    }

    private static void replicateMana(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new ManaChangePayload(player.getData(ModAttachments.MANA).getAmount()));
    }

    private static void replicateAbilities(ServerPlayer player) {
        AbilitySlots abilitySlots = player.getData(ModAttachments.ABILITY_SLOTS);
        PacketDistributor.sendToPlayer(player,
                new AbilitySlotsContentPayload(abilitySlots.getAbilitySlots(), abilitySlots.getSelectedSlot()));
        PacketDistributor.sendToPlayer(player,
                new AbilitySlotsCooldownsPayload(player.getData(ModAttachments.ABILITY_COOLDOWNS)));
    }

    private static void replicateEffectMarkers(ServerPlayer player) {
        AttachedEffectData data = player.getData(ModAttachments.ATTACHED_EFFECTS);
        Map<Holder<EffectMarker>, Integer> displayData = data.getDisplayData();
        if (!displayData.isEmpty()) {
            PacketDistributor.sendToPlayer(player,
                    new UpdateEffectMarkersPayload(displayData, Collections.emptyList()));
        }
    }

}
