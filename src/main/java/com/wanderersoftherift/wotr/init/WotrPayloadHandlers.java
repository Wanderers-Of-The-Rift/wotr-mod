package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.network.C2SRuneAnvilApplyPacket;
import com.wanderersoftherift.wotr.network.ability.AbilityCooldownReplicationPayload;
import com.wanderersoftherift.wotr.network.ability.AbilityCooldownUpdatePayload;
import com.wanderersoftherift.wotr.network.ability.AbilityResourceChangePayload;
import com.wanderersoftherift.wotr.network.ability.AbilitySlotsContentPayload;
import com.wanderersoftherift.wotr.network.ability.AbilitySlotsUpdatePayload;
import com.wanderersoftherift.wotr.network.ability.AbilityStateReplicationPayload;
import com.wanderersoftherift.wotr.network.ability.AbilityToggleStatePayload;
import com.wanderersoftherift.wotr.network.ability.AddEffectMarkerPayload;
import com.wanderersoftherift.wotr.network.ability.LevelUpAbilityPayload;
import com.wanderersoftherift.wotr.network.ability.RemoveEffectMarkerPayload;
import com.wanderersoftherift.wotr.network.ability.ResourceRechargeTriggerablePayload;
import com.wanderersoftherift.wotr.network.ability.SelectAbilitySlotPayload;
import com.wanderersoftherift.wotr.network.ability.SelectAbilityUpgradePayload;
import com.wanderersoftherift.wotr.network.ability.UpdateSlotAbilityStatePayload;
import com.wanderersoftherift.wotr.network.ability.UseAbilityPayload;
import com.wanderersoftherift.wotr.network.charactermenu.OpenCharacterMenuPayload;
import com.wanderersoftherift.wotr.network.guild.GuildStatusReplicationPayload;
import com.wanderersoftherift.wotr.network.guild.GuildStatusUpdatePayload;
import com.wanderersoftherift.wotr.network.guild.WalletReplicationPayload;
import com.wanderersoftherift.wotr.network.guild.WalletUpdatePayload;
import com.wanderersoftherift.wotr.network.quest.AbandonQuestPayload;
import com.wanderersoftherift.wotr.network.quest.AcceptQuestPayload;
import com.wanderersoftherift.wotr.network.quest.ActiveQuestsReplicationPayload;
import com.wanderersoftherift.wotr.network.quest.AvailableQuestsPayload;
import com.wanderersoftherift.wotr.network.quest.CompleteQuestPayload;
import com.wanderersoftherift.wotr.network.quest.HandInQuestItemPayload;
import com.wanderersoftherift.wotr.network.quest.QuestAcceptedPayload;
import com.wanderersoftherift.wotr.network.quest.QuestGoalUpdatePayload;
import com.wanderersoftherift.wotr.network.quest.QuestRemovedPayload;
import com.wanderersoftherift.wotr.network.quest.QuestRewardsPayload;
import com.wanderersoftherift.wotr.network.rift.BannedFromRiftPayload;
import com.wanderersoftherift.wotr.network.rift.S2CLevelListUpdatePacket;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

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

        // Ability effect markers
        registrar.playToClient(AddEffectMarkerPayload.TYPE, AddEffectMarkerPayload.STREAM_CODEC,
                AddEffectMarkerPayload::handleOnClient);
        registrar.playToClient(RemoveEffectMarkerPayload.TYPE, RemoveEffectMarkerPayload.STREAM_CODEC,
                RemoveEffectMarkerPayload::handleOnClient);

        registrar.playToServer(UseAbilityPayload.TYPE, UseAbilityPayload.STREAM_CODEC,
                UseAbilityPayload::handleOnServer);
        registrar.playToClient(AbilityCooldownReplicationPayload.TYPE, AbilityCooldownReplicationPayload.STREAM_CODEC,
                AbilityCooldownReplicationPayload::handleOnClient);
        registrar.playToClient(AbilityCooldownUpdatePayload.TYPE, AbilityCooldownUpdatePayload.STREAM_CODEC,
                AbilityCooldownUpdatePayload::handleOnClient);
        registrar.playToClient(AbilityToggleStatePayload.TYPE, AbilityToggleStatePayload.STREAM_CODEC,
                AbilityToggleStatePayload::handleOnClient);
        registrar.playToClient(AbilityResourceChangePayload.TYPE, AbilityResourceChangePayload.STREAM_CODEC,
                AbilityResourceChangePayload::handleOnClient);
        registrar.playToClient(UpdateSlotAbilityStatePayload.TYPE, UpdateSlotAbilityStatePayload.STREAM_CODEC,
                UpdateSlotAbilityStatePayload::handleOnClient);
        registrar.playToClient(AbilityStateReplicationPayload.TYPE, AbilityStateReplicationPayload.STREAM_CODEC,
                AbilityStateReplicationPayload::handleOnClient);
        registrar.playToClient(ResourceRechargeTriggerablePayload.TYPE, ResourceRechargeTriggerablePayload.STREAM_CODEC,
                ResourceRechargeTriggerablePayload::handleOnClient);

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
        registrar.playToClient(GuildStatusReplicationPayload.TYPE, GuildStatusReplicationPayload.STREAM_CODEC,
                GuildStatusReplicationPayload::handleOnClient);
        registrar.playToClient(GuildStatusUpdatePayload.TYPE, GuildStatusUpdatePayload.STREAM_CODEC,
                GuildStatusUpdatePayload::handleOnClient);

        // Quest
        registrar.playToServer(AcceptQuestPayload.TYPE, AcceptQuestPayload.STREAM_CODEC,
                AcceptQuestPayload::handleOnServer);
        registrar.playToClient(ActiveQuestsReplicationPayload.TYPE, ActiveQuestsReplicationPayload.STREAM_CODEC,
                ActiveQuestsReplicationPayload::handleOnClient);
        registrar.playToClient(QuestAcceptedPayload.TYPE, QuestAcceptedPayload.STREAM_CODEC,
                QuestAcceptedPayload::handleOnClient);
        registrar.playToClient(QuestRemovedPayload.TYPE, QuestRemovedPayload.STREAM_CODEC,
                QuestRemovedPayload::handleOnClient);
        registrar.playToClient(QuestGoalUpdatePayload.TYPE, QuestGoalUpdatePayload.STREAM_CODEC,
                QuestGoalUpdatePayload::handleOnClient);
        registrar.playToServer(HandInQuestItemPayload.TYPE, HandInQuestItemPayload.STREAM_CODEC,
                HandInQuestItemPayload::handleOnServer);
        registrar.playToServer(CompleteQuestPayload.TYPE, CompleteQuestPayload.STREAM_CODEC,
                CompleteQuestPayload::handleOnServer);
        registrar.playToServer(AbandonQuestPayload.TYPE, AbandonQuestPayload.STREAM_CODEC,
                AbandonQuestPayload::handleOnServer);
        registrar.playToClient(AvailableQuestsPayload.TYPE, AvailableQuestsPayload.STREAM_CODEC,
                AvailableQuestsPayload::handleOnClient);
        registrar.playToClient(QuestRewardsPayload.TYPE, QuestRewardsPayload.STREAM_CODEC,
                QuestRewardsPayload::handleOnClient);

        // Character Menu
        registrar.playToServer(OpenCharacterMenuPayload.TYPE, OpenCharacterMenuPayload.STREAM_CODEC,
                OpenCharacterMenuPayload::handleOnServer);
    }

    @SubscribeEvent
    public static void onPlayerJoinedEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            PacketDistributor.sendToPlayer(player,
                    new AbilityCooldownReplicationPayload(player.getData(WotrAttachments.ABILITY_COOLDOWNS)));
            replicateMana(player);
            replicateWallet(player);
            BannedFromRiftPayload.sendTo(player);
            PacketDistributor.sendToPlayer(player,
                    new ActiveQuestsReplicationPayload(player.getData(WotrAttachments.ACTIVE_QUESTS)));
            PacketDistributor.sendToPlayer(player,
                    new AbilityStateReplicationPayload(player.getData(WotrAttachments.ABILITY_STATES)));
            player.getData(WotrAttachments.ATTACHED_EFFECTS).replicateEffects();
            player.getData(WotrAttachments.GUILD_STATUS).replicate();
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            PacketDistributor.sendToPlayer(player,
                    new AbilityCooldownReplicationPayload(player.getData(WotrAttachments.ABILITY_COOLDOWNS)));
            replicateWallet(player);
            BannedFromRiftPayload.sendTo(player);
            PacketDistributor.sendToPlayer(player,
                    new ActiveQuestsReplicationPayload(player.getData(WotrAttachments.ACTIVE_QUESTS)));
            player.getData(WotrAttachments.GUILD_STATUS).replicate();
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replicateAbilities(player);
            PacketDistributor.sendToPlayer(player,
                    new AbilityCooldownReplicationPayload(player.getData(WotrAttachments.ABILITY_COOLDOWNS)));
            replicateMana(player);
            replicateWallet(player);
            BannedFromRiftPayload.sendTo(player);
            PacketDistributor.sendToPlayer(player,
                    new ActiveQuestsReplicationPayload(player.getData(WotrAttachments.ACTIVE_QUESTS)));
            PacketDistributor.sendToPlayer(player,
                    new AbilityStateReplicationPayload(player.getData(WotrAttachments.ABILITY_STATES)));
            player.getData(WotrAttachments.ATTACHED_EFFECTS).replicateEffects();
            player.getData(WotrAttachments.GUILD_STATUS).replicate();
        }
    }

    private static void replicateMana(ServerPlayer player) {
        var resourceData = player.getData(WotrAttachments.ABILITY_RESOURCE_DATA);
        resourceData.getAmounts()
                .forEach((resourceKey, amount) -> player.registryAccess()
                        .get(resourceKey)
                        .ifPresent(resource -> PacketDistributor.sendToPlayer(player,
                                new AbilityResourceChangePayload(resource, amount))));
    }

    private static void replicateAbilities(ServerPlayer player) {
        AbilitySlots abilitySlots = player.getData(WotrAttachments.ABILITY_SLOTS);
        PacketDistributor.sendToPlayer(player,
                new AbilitySlotsContentPayload(abilitySlots.getRawSlots(), abilitySlots.getSelectedSlot()));
    }

    private static void replicateWallet(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player,
                new WalletReplicationPayload(player.getData(WotrAttachments.WALLET).getAll()));
    }

}
