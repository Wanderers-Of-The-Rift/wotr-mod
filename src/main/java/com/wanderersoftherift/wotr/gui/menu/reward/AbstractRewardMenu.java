package com.wanderersoftherift.wotr.gui.menu.reward;

import com.wanderersoftherift.wotr.network.reward.ClaimRewardPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base implementation for menus that offer non-item rewards.
 */
public abstract class AbstractRewardMenu extends AbstractContainerMenu implements RewardProvidingMenu {

    private boolean dirty = true;
    private List<RewardSlot> nonItemRewards;
    private ContainerLevelAccess access;

    protected AbstractRewardMenu(@Nullable MenuType<?> menuType, int containerId, List<RewardSlot> nonItemRewards,
            ContainerLevelAccess access) {
        super(menuType, containerId);
        this.nonItemRewards = new ArrayList<>(nonItemRewards);
        this.access = access;
    }

    public List<RewardSlot> getNonItemRewards() {
        return nonItemRewards;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            for (RewardSlot rewardSlot : nonItemRewards) {
                rewardSlot.reward().apply(serverPlayer);
            }
        }
    }

    /**
     * Client-side request to claim a non-item reward
     *
     * @param reward
     */
    public void clientClaimReward(RewardSlot reward) {
        if (nonItemRewards.removeIf(x -> x.id() == reward.id())) {
            PacketDistributor.sendToServer(new ClaimRewardPayload(reward.id()));
            dirty = true;
        }
    }

    /**
     * Server-side handling of claiming a non-item reward
     *
     * @param player
     * @param rewardId
     */
    @Override
    public void claimReward(Player player, int rewardId) {
        access.execute((level, pos) -> {
            Optional<RewardSlot> rewardSlot = nonItemRewards.stream().filter(x -> x.id() == rewardId).findFirst();
            if (rewardSlot.isPresent()) {
                rewardSlot.get().reward().apply(player);
                nonItemRewards.remove(rewardSlot.get());
            }
        });
    }

    /**
     * @return Whether the non-item rewards list has changed
     */
    public boolean isRewardsChanged() {
        return dirty;
    }

    public void clearRewardsChangedFlag() {
        dirty = false;
    }
}
