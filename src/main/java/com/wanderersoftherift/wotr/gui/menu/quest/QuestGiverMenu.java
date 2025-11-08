package com.wanderersoftherift.wotr.gui.menu.quest;

import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This menu presents the player with available quests that they can select. The quest list is replicated outside of the
 * menu.
 */
public class QuestGiverMenu extends AbstractContainerMenu {
    public static final StreamCodec<RegistryFriendlyByteBuf, List<QuestState>> QUEST_LIST_CODEC = QuestState.STREAM_CODEC
            .apply(ByteBufCodecs.list());

    private final ValidatingLevelAccess access;
    private final List<QuestState> availableQuests;

    public QuestGiverMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, ValidatingLevelAccess.NULL, QUEST_LIST_CODEC.decode(data));
    }

    public QuestGiverMenu(int containerId, Inventory playerInventory, ValidatingLevelAccess access,
            List<QuestState> availableQuests) {
        super(WotrMenuTypes.QUEST_GIVER_MENU.get(), containerId);

        this.access = access;
        this.availableQuests = availableQuests;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    public List<QuestState> getAvailableQuests() {
        return availableQuests;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return access.isValid(player);
    }

    public void acceptQuest(ServerPlayer player, int index) {
        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (activeQuests.isEmpty() && index >= 0 && index < availableQuests.size()) {
            activeQuests.add(availableQuests.get(index));
            player.closeContainer();
        }
    }
}
