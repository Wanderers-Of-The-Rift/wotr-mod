package com.wanderersoftherift.wotr.item.gear;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.item.gear.GearAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GearEvents {

    @SubscribeEvent
    public static void castPrimary(PlayerInteractEvent.LeftClickEmpty event){
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        ItemStack weapon = player.getItemHeldByArm(player.getMainArm());
        AbstractAbility ability = new AbilityContext(player, weapon).getBasic();

            if (ability != null){
                /*if (player.getAttackStrengthScale(0.0F) == 1.0f){
                    ability.onActivateGear(player, weapon, true);
                }*/
                ability.onActivateGear(player, weapon, true);
                player.displayClientMessage(Component.literal(String.valueOf(player.getCurrentItemAttackStrengthDelay())), true);
                //player.displayClientMessage(Component.literal(String.valueOf(player.getAttackStrengthScale(0.0F))), true);
            }


    }

    @SubscribeEvent
    public static void castSecondary(PlayerInteractEvent.RightClickEmpty event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        ItemStack weapon = player.getItemHeldByArm(player.getMainArm());
        AbstractAbility ability = new AbilityContext(player, weapon).getAbility();

        if (ability != null) {
            ability.onActivateGear(player, weapon, false);
            player.displayClientMessage(Component.literal(String.valueOf(player.getAttackStrengthScale(0.0F))), true);
        }
    }

    public static void startBasicCooldown(){
       return;
    }
    public static void startSecondaryCooldown(){
        return;
    }
}
