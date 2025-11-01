package com.wanderersoftherift.totr.helper;

import com.mojang.authlib.GameProfile;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * Enhanced mock player with additional testing capabilities:
 * <ul>
 * <li>Handles dimension travel by linking to a newly created mock player</li>
 * <li>Handles opening menues</li>
 * </ul>
 */
public final class MockPlayer extends Player {
    public static final int CONTAINER_ID = 1;

    private MockPlayer teleportedPlayer = null;
    private final GameType gameType;

    public MockPlayer(Level level, BlockPos pos, float yRot, GameProfile gameProfile, GameType gameType) {
        super(level, pos, yRot, gameProfile);
        this.gameType = gameType;
    }

    public static MockPlayer create(GameTestHelper helper, GameType gameType) {
        return new MockPlayer(helper.getLevel(), BlockPos.ZERO, 0.0F,
                new GameProfile(UUID.randomUUID(), "test-mock-player"), gameType);
    }

    /**
     * @return The new mock player created as a result of teleportation to another dimension
     */
    public @Nullable MockPlayer getTeleportSuccessor() {
        return teleportedPlayer;
    }

    @Override
    public boolean isSpectator() {
        return gameType == GameType.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        return gameType.isCreative();
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }

    @Override
    public @NotNull OptionalInt openMenu(@Nullable MenuProvider menu) {
        if (menu == null) {
            return OptionalInt.empty();
        }
        containerMenu = menu.createMenu(CONTAINER_ID, new Inventory(this), this);
        return OptionalInt.of(CONTAINER_ID);
    }

    public @Nullable Player teleport(TeleportTransition transition) {
        if (!net.neoforged.neoforge.common.CommonHooks.onTravelToDimension(this, transition.newLevel().dimension())) {
            return null;
        }
        if (this.isRemoved()) {
            return null;
        } else {
            ServerLevel serverlevel = transition.newLevel();
            if (!(level() instanceof ServerLevel currentLevel)) {
                return null;
            }
            ResourceKey<Level> currentDimension = currentLevel.dimension();
            if (!transition.asPassenger()) {
                this.stopRiding();
            }

            if (serverlevel.dimension() == currentDimension) {
                transition.postTeleportTransition().onTransition(this);
                return this;
            } else {
                remove(RemovalReason.CHANGED_DIMENSION);
                MockPlayer newPlayer = new MockPlayer(serverlevel, BlockPos.ZERO, transition.yRot(), getGameProfile(),
                        gameType);
                newPlayer.setPos(transition.position());
                newPlayer.setXRot(transition.xRot());
                newPlayer.setYRot(transition.yRot());
                newPlayer.setData(WotrAttachments.RIFT_ENTRY_STATES, this.getData(WotrAttachments.RIFT_ENTRY_STATES));
                serverlevel.addDuringTeleport(newPlayer);
                transition.postTeleportTransition().onTransition(newPlayer);
                net.neoforged.neoforge.event.EventHooks.firePlayerChangedDimensionEvent(this, currentDimension,
                        transition.newLevel().dimension());
                teleportedPlayer = newPlayer;
                return newPlayer;
            }
        }
    }
}
