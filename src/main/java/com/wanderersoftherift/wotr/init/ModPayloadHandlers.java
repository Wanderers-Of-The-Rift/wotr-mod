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
import com.wanderersoftherift.wotr.network.LevelUpAbilityPayload;
import com.wanderersoftherift.wotr.network.ManaChangePayload;
import com.wanderersoftherift.wotr.network.SelectAbilitySlotPayload;
import com.wanderersoftherift.wotr.network.SelectAbilityUpgradePayload;
import com.wanderersoftherift.wotr.network.SetEffectMarkerPayload;
import com.wanderersoftherift.wotr.network.UpdateEffectMarkersPayload;
import com.wanderersoftherift.wotr.network.UseAbilityPayload;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
    }

    @SubscribeEvent
    public static void onPlayerJoinedEvent(PlayerEvent.PlayerLoggedInEvent event) {
        replicateAbilities(event.getEntity());
        replicateEffectMarkers(event.getEntity());
        replicateMana(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerSpawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        replicateAbilities(event.getEntity());
        replicateEffectMarkers(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        replicateAbilities(event.getEntity());
        replicateEffectMarkers(event.getEntity());
        replicateMana(event.getEntity());
    }

    private static void replicateMana(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PacketDistributor.sendToPlayer(serverPlayer,
                new ManaChangePayload(serverPlayer.getData(ModAttachments.MANA).getAmount()));
    }

    private static void replicateAbilities(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        AbilitySlots abilitySlots = serverPlayer.getData(ModAttachments.ABILITY_SLOTS);
        PacketDistributor.sendToPlayer(serverPlayer,
                new AbilitySlotsContentPayload(abilitySlots.getAbilitySlots(), abilitySlots.getSelectedSlot()));
        PacketDistributor.sendToPlayer(serverPlayer,
                new AbilitySlotsCooldownsPayload(player.getData(ModAttachments.ABILITY_COOLDOWNS)));
    }

    private static void replicateEffectMarkers(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        AttachedEffectData data = serverPlayer.getData(ModAttachments.ATTACHED_EFFECTS);
        Map<Holder<EffectMarker>, Integer> displayData = data.getDisplayData();
        if (!displayData.isEmpty()) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new UpdateEffectMarkersPayload(displayData, Collections.emptyList()));
        }
    }

}
