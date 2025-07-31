package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.quest.AvailableQuestsPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A block that provides access to selecting and completing quests
 */
public class QuestHubBlock extends Block {

    public static final Component CONTAINER_TITLE = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.selection"));
    private static final int QUEST_SELECTION_SIZE = 5;

    public QuestHubBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (activeQuests.isEmpty()) {
            List<QuestState> availableQuests = getAvailableQuests(level, serverPlayer);

            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestGiverMenu(containerId, playerInventory,
                                    ContainerLevelAccess.create(level, pos), new ArrayList<>(availableQuests)),
                            CONTAINER_TITLE)
            );
            PacketDistributor.sendToPlayer(serverPlayer, new AvailableQuestsPayload(availableQuests));
        } else {
            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestCompletionMenu(containerId, playerInventory,
                                    ContainerLevelAccess.create(level, pos), activeQuests,
                                    activeQuests.getQuestList().getFirst().getId()),
                            Component.empty())
            );
        }
        return InteractionResult.CONSUME;
    }

    private static @NotNull List<QuestState> getAvailableQuests(Level level, ServerPlayer serverPlayer) {
        List<QuestState> availableQuests = serverPlayer.getData(WotrAttachments.AVAILABLE_QUESTS);
        if (serverPlayer.getData(WotrAttachments.AVAILABLE_QUESTS).isEmpty()) {
            availableQuests = generateNewQuestList(level, serverPlayer);
            serverPlayer.setData(WotrAttachments.AVAILABLE_QUESTS, availableQuests);
        }
        return availableQuests;
    }

    private static @NotNull List<QuestState> generateNewQuestList(Level level, ServerPlayer serverPlayer) {
        LootParams params = new LootParams.Builder(serverPlayer.serverLevel()).create(LootContextParamSets.EMPTY);
        Registry<Quest> questRegistry = level.registryAccess().lookupOrThrow(WotrRegistries.Keys.QUESTS);
        List<Quest> quests = questRegistry.stream().collect(Collectors.toList());
        List<QuestState> generatedQuests = new ArrayList<>();
        for (int i = 0; i < QUEST_SELECTION_SIZE && !quests.isEmpty(); i++) {
            int index = level.random.nextInt(quests.size());
            Quest quest;
            if (index < quests.size() - 1) {
                quest = quests.set(index, quests.removeLast());
            } else {
                quest = quests.removeLast();
            }
            generatedQuests.add(new QuestState(questRegistry.wrapAsHolder(quest), quest.generateGoals(params),
                    quest.generateRewards(params)));
        }
        return generatedQuests;
    }
}
