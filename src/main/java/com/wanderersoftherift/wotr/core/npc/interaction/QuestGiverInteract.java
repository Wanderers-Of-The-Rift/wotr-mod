package com.wanderersoftherift.wotr.core.npc.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.quest.AvailableQuests;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.util.HolderSetUtil;
import com.wanderersoftherift.wotr.util.RandomUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MobInteraction attachment for providing Quest Giver behavior
 */
public record QuestGiverInteract(Optional<HolderSet<Quest>> quests, int choiceCount) implements MenuInteraction {
    public static final MapCodec<QuestGiverInteract> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Quest.SET_CODEC.optionalFieldOf("quests").forGetter(QuestGiverInteract::quests),
            Codec.INT.optionalFieldOf("choice_count", 3).forGetter(QuestGiverInteract::choiceCount)
    ).apply(instance, QuestGiverInteract::new));

    @Override
    public MapCodec<? extends NpcInteraction> getCodec() {
        return CODEC;
    }

    @Override
    public void interact(Holder<NpcIdentity> npc, ValidatingLevelAccess access, ServerLevel level, Player player) {
        HolderSet<Quest> choices = quests.orElse(
                HolderSetUtil.registryToHolderSet(level.registryAccess(), WotrRegistries.Keys.QUESTS));

        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (activeQuests.isEmpty()) {
            List<QuestState> availableQuests = getAvailableQuests(level, player, npc, choices, choiceCount);
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> new QuestGiverMenu(containerId, playerInventory, access,
                            new ArrayList<>(availableQuests)),
                    NpcIdentity.getDisplayName(npc)),
                    registryFriendlyByteBuf -> QuestGiverMenu.QUEST_LIST_CODEC.encode(registryFriendlyByteBuf,
                            availableQuests));

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
