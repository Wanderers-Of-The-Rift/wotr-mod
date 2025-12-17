package com.wanderersoftherift.totr.test;

import com.wanderersoftherift.totr.TestersOfTheRift;
import com.wanderersoftherift.totr.helper.MockPlayer;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(TestersOfTheRift.MOD_ID)
public class RiftEntryTests {

    @PrefixGameTestTemplate(false)
    @GameTest(template = "rift_portal_test")
    public static void riftTest(GameTestHelper helper) {
        BlockPos portalBlockPos = new BlockPos(2, 2, 1);
        MockPlayer player = MockPlayer.create(helper, GameType.SURVIVAL);
        player.setPos(helper.absoluteVec(new BlockPos(1, 1, 1).getCenter().add(0.5, 0, 0)));
        helper.getLevel().addFreshEntity(player);

        ItemStack key = WotrItems.RIFT_KEY.toStack(1);
        key.set(WotrDataComponentType.RiftKeyData.RIFT_TIER, 3);
        player.getInventory().setItem(player.getInventory().selected, key);
        helper.useBlock(portalBlockPos, player);

        helper.assertTrue(player.getInventory().getSelected().isEmpty(), "Key not consumed");

        helper.runAfterDelay(2, () -> {
            helper.assertTrue(player.getTeleportSuccessor() != null, "Player not teleported to rift");
            MockPlayer riftPlayer = player.getTeleportSuccessor();
            ServerLevel riftLevel = (ServerLevel) riftPlayer.level();
            helper.assertTrue(RiftLevelManager.isRift(riftLevel), "Level is not rift");
            RiftData riftData = RiftData.get(riftLevel);
            helper.assertTrue(riftData.getConfig().tier() == 3, "Rift is incorrect tier");
            ResourceKey<Level> dimension = riftLevel.dimension();
            riftPlayer.setPos(PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION.getCenter());
            riftLevel.setChunkForced(0, 0, true);

            helper.succeedWhen(() -> {
                helper.assertTrue(riftPlayer.getTeleportSuccessor() != null, "Player did not leave rift");
                helper.assertFalse(RiftLevelManager.levelExists(dimension),
                        "Rift level not destroyed after empty of players");
                riftPlayer.getTeleportSuccessor().remove(Entity.RemovalReason.DISCARDED);
                helper.succeed();
            });
        });
    }

}
