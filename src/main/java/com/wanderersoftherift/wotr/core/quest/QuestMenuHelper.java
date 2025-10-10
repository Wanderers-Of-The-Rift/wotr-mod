package com.wanderersoftherift.wotr.core.quest;

import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.quest.AvailableQuestsPayload;
import com.wanderersoftherift.wotr.util.HolderSetUtil;
import com.wanderersoftherift.wotr.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Common handling of opening quest related menus - at least while we have both the quest hub block and npc quest
 * givers.
 */
public final class QuestMenuHelper {

    private static final int DEFAULT_SELECTION_SIZE = 5;

    private QuestMenuHelper() {
    }

    /**
     * Opens the quest menu from a block
     * 
     * @param player
     * @param title
     * @param pos
     * @param block
     */
    public static void openQuestMenu(ServerPlayer player, Component title, BlockPos pos, Block block) {
        openQuestMenu(player, title, ValidatingLevelAccess.create(player.serverLevel(), pos, block),
                HolderSetUtil.registryToHolderSet(player.level().registryAccess(), WotrRegistries.Keys.QUESTS),
                DEFAULT_SELECTION_SIZE);
    }

    /**
     * Opens the quest menu from a given questGiver
     * 
     * @param player
     * @param questGiver
     */
    public static void openQuestMenu(ServerPlayer player, Mob questGiver, HolderSet<Quest> choices, int choiceCount) {
        openQuestMenu(player, questGiver.getDisplayName(), ValidatingLevelAccess.create(questGiver), choices,
                choiceCount);
    }

    private static void openQuestMenu(
            ServerPlayer player,
            Component title,
            ValidatingLevelAccess access,
            HolderSet<Quest> choices,
            int choiceCount) {
        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (activeQuests.isEmpty()) {
            List<QuestState> availableQuests = getAvailableQuests(player.level(), player, choices, choiceCount);

            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestGiverMenu(containerId, playerInventory,
                                    access, new ArrayList<>(availableQuests)),
                            title)
            );
            PacketDistributor.sendToPlayer(player, new AvailableQuestsPayload(availableQuests));
        } else {
            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestCompletionMenu(containerId, playerInventory,
                                    access, activeQuests, activeQuests.getQuestList().getFirst().getId()),
                            Component.empty())
            );
        }
    }

    private static @NotNull List<QuestState> getAvailableQuests(
            Level level,
            ServerPlayer serverPlayer,
            HolderSet<Quest> choices,
            int choiceCount) {
        List<QuestState> availableQuests = serverPlayer.getData(WotrAttachments.AVAILABLE_QUESTS);
        if (serverPlayer.getData(WotrAttachments.AVAILABLE_QUESTS).isEmpty()) {
            availableQuests = generateNewQuestList(level, serverPlayer, choices, choiceCount);
            serverPlayer.setData(WotrAttachments.AVAILABLE_QUESTS, availableQuests);
        }
        return availableQuests;
    }

    private static @NotNull List<QuestState> generateNewQuestList(
            Level level,
            ServerPlayer serverPlayer,
            HolderSet<Quest> choices,
            int choiceCount) {
        LootParams params = new LootParams.Builder(serverPlayer.serverLevel()).create(LootContextParamSets.EMPTY);
        return RandomUtil.randomSubset(choices.stream().toList(), choiceCount, level.getRandom())
                .stream()
                .map(quest -> new QuestState(quest, quest.value().generateGoals(params),
                        quest.value().generateRewards(params)))
                .collect(Collectors.toList());
    }
}
