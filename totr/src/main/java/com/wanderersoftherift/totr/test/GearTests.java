package com.wanderersoftherift.totr.test;

import com.wanderersoftherift.totr.TestersOfTheRift;
import com.wanderersoftherift.totr.helper.MockPlayer;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Optional;

@GameTestHolder(TestersOfTheRift.MOD_ID)
public class GearTests {

    @PrefixGameTestTemplate(false)
    @GameTest(template = "rift_portal_test")
    public static void addRunegemWithAnvil(GameTestHelper helper) {
        BlockPos anvilBlockPos = new BlockPos(3, 1, 4);
        MockPlayer mockPlayer = MockPlayer.create(helper, GameType.SURVIVAL);
        helper.useBlock(anvilBlockPos, mockPlayer);
        if (!(mockPlayer.containerMenu instanceof RuneAnvilMenu menu)) {
            helper.fail("RuneAnvil menu did not open");
            return;
        }
        ItemStack inputSword = new ItemStack(Items.DIAMOND_SWORD);
        inputSword.set(WotrDataComponentType.GEAR_SOCKETS,
                new GearSockets(List.of(new GearSocket(RunegemShape.CIRCLE, Optional.empty(), Optional.empty()))));
        menu.getGearSlot().set(inputSword);
        ItemStack runegem = new ItemStack(WotrItems.RUNEGEM.get());
        runegem.set(WotrDataComponentType.RUNEGEM_DATA,
                helper.getLevel()
                        .registryAccess()
                        .lookupOrThrow(WotrRegistries.Keys.RUNEGEM_DATA)
                        .getValue(WanderersOfTheRift.id("defense_raw")));
        menu.getSocketSlots().getFirst().set(runegem);
        menu.apply();
        ItemStack outputSword = menu.getGearSlotItem();
        GearSockets gearSockets = outputSword.get(WotrDataComponentType.GEAR_SOCKETS);
        helper.assertTrue(gearSockets.sockets().get(0).runegem().isPresent(), "Runegem not on sword");
        helper.assertTrue(gearSockets.sockets().get(0).runegem().get().shape() == RunegemShape.CIRCLE,
                "Runegem wrong shape");
        helper.assertTrue(gearSockets.sockets().get(0).modifier().isPresent(), "Socket modifier missing");
        helper.succeed();
    }

}
