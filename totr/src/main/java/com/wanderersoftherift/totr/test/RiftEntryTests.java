package com.wanderersoftherift.totr.test;

import com.mojang.authlib.GameProfile;
import com.wanderersoftherift.totr.TestersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import javax.annotation.Nullable;
import java.util.UUID;

@GameTestHolder(TestersOfTheRift.MOD_ID)
public class RiftEntryTests {

    @PrefixGameTestTemplate(false)
    @GameTest(template = "rift_portal_test")
    public static void riftTest(GameTestHelper helper) {
        BlockPos portalBlockPos = new BlockPos(5, 2, 2);
        MockPlayer player = makeEnhancedMockPlayer(helper, GameType.SURVIVAL);
        player.setPos(helper.absoluteVec(new Vec3(5, 2, 2.5)));
        helper.getLevel().addFreshEntity(player);

        ItemStack key = WotrItems.RIFT_KEY.toStack(1);
        key.set(WotrDataComponentType.RiftKeyData.RIFT_TIER, 3);
        player.getInventory().setItem(player.getInventory().selected, key);
        helper.useBlock(portalBlockPos, player);

        helper.assertTrue(player.getInventory().getSelected().isEmpty(), "Key not consumed");

        helper.runAfterDelay(2, () -> {
            helper.assertTrue(player.teleportedPlayer != null, "Player not teleported to rift");
            MockPlayer riftPlayer = player.teleportedPlayer;
            ServerLevel riftLevel = (ServerLevel) riftPlayer.level();
            helper.assertTrue(RiftLevelManager.isRift(riftLevel), "Level is not rift");
            RiftData riftData = RiftData.get(riftLevel);
            helper.assertTrue(riftData.getConfig().tier() == 3, "Rift is incorrect tier");
            ResourceKey<Level> dimension = riftLevel.dimension();
            riftPlayer.setPos(PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION.getCenter());
            riftLevel.setChunkForced(0, 0, true);

            helper.succeedWhen(() -> {
                helper.assertTrue(riftPlayer.teleportedPlayer != null, "Player did not leave rift");
                helper.assertFalse(RiftLevelManager.levelExists(dimension),
                        "Rift level not destroyed after empty of players");
                riftPlayer.teleportedPlayer.remove(Entity.RemovalReason.DISCARDED);
                helper.succeed();
            });
        });
    }

    private static MockPlayer makeEnhancedMockPlayer(GameTestHelper helper, final GameType gameType) {
        return new MockPlayer(helper.getLevel(), BlockPos.ZERO, 0.0F,
                new GameProfile(UUID.randomUUID(), "test-mock-player"), gameType);
    }

    private static final class MockPlayer extends Player {
        private MockPlayer teleportedPlayer = null;
        private final GameType gameType;

        public MockPlayer(Level level, BlockPos pos, float yRot, GameProfile gameProfile, GameType gameType) {
            super(level, pos, yRot, gameProfile);
            this.gameType = gameType;
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

        @Nullable public Player teleport(TeleportTransition transition) {
            if (!net.neoforged.neoforge.common.CommonHooks.onTravelToDimension(this,
                    transition.newLevel().dimension())) {
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
                    MockPlayer newPlayer = new MockPlayer(serverlevel, BlockPos.ZERO, transition.yRot(),
                            getGameProfile(), gameType);
                    newPlayer.setPos(transition.position());
                    newPlayer.setXRot(transition.xRot());
                    newPlayer.setYRot(transition.yRot());
                    newPlayer.setData(WotrAttachments.RIFT_ENTRY_STATES,
                            this.getData(WotrAttachments.RIFT_ENTRY_STATES));
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
}
