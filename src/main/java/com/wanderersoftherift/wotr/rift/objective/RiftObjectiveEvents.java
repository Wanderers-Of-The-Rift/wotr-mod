package com.wanderersoftherift.wotr.rift.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftEntryState;
import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.gui.menu.RiftCompleteMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.init.loot.WotrLootContextParams;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Event subscriber for objective handling
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID)
public class RiftObjectiveEvents {
    private static final ResourceKey<LootTable> SUCCESS_TABLE = ResourceKey.create(Registries.LOOT_TABLE,
            WanderersOfTheRift.id("rift_objective/success"));
    private static final ResourceKey<LootTable> FAIL_TABLE = ResourceKey.create(Registries.LOOT_TABLE,
            WanderersOfTheRift.id("rift_objective/fail"));
    private static final ResourceKey<LootTable> SURVIVE_TABLE = ResourceKey.create(Registries.LOOT_TABLE,
            WanderersOfTheRift.id("rift_objective/survive"));

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRiftOpened(RiftEvent.Created.Pre event) {
        Holder<ObjectiveType> objectiveType = event.getConfig()
                .objective()
                .orElseGet(() -> event.getFirstPlayer()
                        .getServer()
                        .registryAccess()
                        .lookupOrThrow(WotrRegistries.Keys.OBJECTIVES)
                        .getRandomElementOf(WotrTags.Objectives.RANDOM_SELECTABLE,
                                RandomSource.create(event.getConfig().riftGen().seed().get() + 668_453_148))
                        .orElseThrow(() -> new IllegalStateException("No objectives available")));

        event.setConfig(event.getConfig().withObjective(objectiveType));
    }

    @SubscribeEvent
    public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        var objective = RiftData.get(player.serverLevel()).getObjective();
        PacketDistributor.sendToPlayer(player, new S2CRiftObjectiveStatusPacket(objective));
        if (objective.isEmpty()) {
            return;
        }
        Component objectiveStartMessage = objective.get().getObjectiveStartMessage();
        if (objectiveStartMessage != null) {
            player.displayClientMessage(objectiveStartMessage, false);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        var deathRiftEntryState = player.getData(WotrAttachments.DEATH_RIFT_ENTRY_STATE);
        if (deathRiftEntryState == RiftEntryState.EMPTY) {
            return;
        }
        // TODO: what if player dies in multiple rifts simultaneously?
        player.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, p) -> new RiftCompleteMenu(containerId, playerInventory,
                        ContainerLevelAccess.create(player.level(), p.getOnPos()), RiftCompleteMenu.FLAG_FAILED,
                        deathRiftEntryState.statSnapshot().getCustomStatDelta(player)),
                Component.translatable(WanderersOfTheRift.translationId("container", "rift_complete"))));
        if (player.containerMenu instanceof RiftCompleteMenu menu) {
            // TODO: Do we need rift config for losing a rift?
            generateObjectiveLoot(menu, player, FAIL_TABLE, new RiftConfig(0));
        }
        player.setData(WotrAttachments.DEATH_RIFT_ENTRY_STATE, RiftEntryState.EMPTY);
    }

    @SubscribeEvent
    public static void onPlayerLeaveLevel(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        var exitedRiftEntryState = player.getData(WotrAttachments.EXITED_RIFT_ENTRY_STATE);
        ServerLevel riftLevel = RiftLevelManager.getRiftLevel(exitedRiftEntryState.riftDimension());

        if (riftLevel == null || exitedRiftEntryState == RiftEntryState.EMPTY) {
            return;
        }
        RiftData riftData = RiftData.get(riftLevel);
        if (riftData.containsPlayer(player)) {
            // Player hasn't actually left the level
            return;
        }

        var objective = riftData.getObjective();
        boolean success = objective.isPresent() && objective.get().isComplete();

        NeoForge.EVENT_BUS.post(new RiftEvent.PlayerCompletedRift(player, success, riftLevel, riftData.getConfig()));

        player.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, p) -> new RiftCompleteMenu(containerId, playerInventory,
                        ContainerLevelAccess.create(player.level(), p.getOnPos()),
                        success ? RiftCompleteMenu.FLAG_SUCCESS : RiftCompleteMenu.FLAG_SURVIVED, exitedRiftEntryState
                                .statSnapshot()
                                .getCustomStatDelta(player)/*
                                                            * this will include stats from subrifts, maybe todo only
                                                            * this specific rift?
                                                            */),
                Component.translatable(WanderersOfTheRift.translationId("container", "rift_complete"))));

        if (player.containerMenu instanceof RiftCompleteMenu menu) {
            generateObjectiveLoot(menu, player, success ? SUCCESS_TABLE : SURVIVE_TABLE, riftData.getConfig());
        }
        player.setData(WotrAttachments.EXITED_RIFT_ENTRY_STATE, RiftEntryState.EMPTY);
    }

    private static void generateObjectiveLoot(
            RiftCompleteMenu menu,
            ServerPlayer player,
            ResourceKey<LootTable> table,
            RiftConfig riftConfig) {
        ServerLevel level = player.serverLevel();
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(table);
        LootParams lootParams = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(WotrLootContextParams.RIFT_TIER, riftConfig.tier())
                .create(LootContextParamSets.EMPTY);
        lootTable.getRandomItems(lootParams).forEach(item -> menu.addReward(item, player));
    }
}
