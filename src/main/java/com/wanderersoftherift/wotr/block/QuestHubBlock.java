package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.guild.AvailableQuestsPayload;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A block that provides access to selecting and completing quests
 */
public class QuestHubBlock extends Block {

    public static final Component CONTAINER_TITLE = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.selection"));

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
        Registry<Quest> questRegistry = level.registryAccess().lookupOrThrow(WotrRegistries.Keys.QUESTS);
        if (activeQuests.isEmpty() && player instanceof ServerPlayer) {
            List<QuestState> quests = questRegistry.stream()
                    .map(x -> new QuestState(questRegistry.wrapAsHolder(x), x.generateGoals(level.random),
                            x.generateRewards(level.random)))
                    .toList();
            player.openMenu(new SimpleMenuProvider((containerId, playerInventory, p) -> new QuestGiverMenu(containerId,
                    playerInventory, ContainerLevelAccess.create(level, pos), quests), CONTAINER_TITLE));
            PacketDistributor.sendToPlayer(serverPlayer, new AvailableQuestsPayload(quests));

        } else {
            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestCompletionMenu(containerId, playerInventory,
                                    ContainerLevelAccess.create(level, pos), p.getData(WotrAttachments.ACTIVE_QUESTS),
                                    0),
                            Quest.title(activeQuests.getQuestState(0).getOrigin())));
        }
        return InteractionResult.CONSUME;
    }
}
