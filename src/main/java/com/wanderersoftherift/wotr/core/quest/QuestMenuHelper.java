package com.wanderersoftherift.wotr.core.quest;

import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.quest.AvailableQuestsPayload;
import com.wanderersoftherift.wotr.util.RandomUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
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

    private QuestMenuHelper() {
    }

    public static void openQuestMenu(
            Player player,
            Holder<NpcIdentity> npc,
            ValidatingLevelAccess access,
            HolderSet<Quest> choices,
            int choiceCount) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (activeQuests.isEmpty()) {
            List<QuestState> availableQuests = getAvailableQuests(level, player, npc, choices, choiceCount);

            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestGiverMenu(containerId, playerInventory,
                                    access, new ArrayList<>(availableQuests)),
                            NpcIdentity.getDisplayName(npc))
            );
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new AvailableQuestsPayload(availableQuests));
            }
        } else {
            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestCompletionMenu(containerId, playerInventory,
                                    access, activeQuests, activeQuests.getQuestList().getFirst().getId()),
                            NpcIdentity.getDisplayName(npc))
            );
        }
    }

    private static @NotNull List<QuestState> getAvailableQuests(
            ServerLevel level,
            Player player,
            Holder<NpcIdentity> npc,
            HolderSet<Quest> choices,
            int choiceCount) {
        AvailableQuests availableQuestData = player.getData(WotrAttachments.AVAILABLE_QUESTS);
        List<QuestState> availableQuests = availableQuestData.getQuests(npc);

        if (availableQuests.isEmpty()) {
            availableQuests = generateNewQuestList(level, player, npc, choices, choiceCount);
            availableQuestData.setQuests(npc, availableQuests);
        }
        return availableQuests;
    }

    private static @NotNull List<QuestState> generateNewQuestList(
            ServerLevel level,
            Player player,
            Holder<NpcIdentity> npc,
            HolderSet<Quest> choices,
            int choiceCount) {
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        return RandomUtil.randomSubset(
                choices.stream().filter(quest -> quest.value().isAvailable(player, level)).toList(), choiceCount,
                level.getRandom())
                .stream()
                .map(quest -> new QuestState(quest, npc, quest.value().generateGoals(params),
                        quest.value().generateRewards(params)))
                .collect(Collectors.toList());
    }
}
