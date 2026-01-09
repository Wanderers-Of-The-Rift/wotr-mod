package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlotFromMC;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import com.wanderersoftherift.wotr.item.ability.TriggerableAbilityModifier;
import com.wanderersoftherift.wotr.modifier.ModifierProvider;
import com.wanderersoftherift.wotr.network.ability.MainAttackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Arrays;

@EventBusSubscriber
public class MainAttackHandler {

    private static boolean lastHasAttacked = false;
    private static boolean hasAttacked = false;

    public static boolean doAttack(Player player) {
        if (shouldUseOverrideAttack(player)) {
            hasAttacked = true;
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post tickEvent) {
        if (hasAttacked && !lastHasAttacked) {
            handleStartAttack();
        }
        if (!hasAttacked && lastHasAttacked) {
            handleEndAttack();
        }
        lastHasAttacked = hasAttacked;
        hasAttacked = false;
    }

    private static boolean shouldUseOverrideAttack(Player player) {
        // should other slots also be checked?
        return Arrays.stream(EquipmentSlot.values()).anyMatch(slot -> {
            var wotrSlot = WotrEquipmentSlotFromMC.fromVanillaSlot(slot);
            var stack = player.getItemBySlot(slot);
            return stack.getAllOfType(ModifierProvider.class)
                    .anyMatch(it -> it.modifiers(stack, wotrSlot, player)
                            .anyMatch(entry -> entry.instance()
                                    .modifier()
                                    .value()
                                    .getModifierTier(entry.instance().tier())
                                    .stream()
                                    .anyMatch(effect -> effect instanceof TriggerableAbilityModifier tam
                                            && tam.trigger().type() == WotrTrackedAbilityTriggers.MAIN_ATTACK)));
        });
    }

    private static void handleEndAttack() {
        Minecraft.getInstance().player.connection.send(new MainAttackPayload(false));
    }

    private static void handleStartAttack() {
        Minecraft.getInstance().player.connection.send(new MainAttackPayload(true));
    }
}
