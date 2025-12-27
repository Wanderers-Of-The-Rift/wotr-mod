package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlotFromMC;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import com.wanderersoftherift.wotr.item.ability.TriggerableAbilityModifier;
import com.wanderersoftherift.wotr.modifier.ModifierProvider;
import com.wanderersoftherift.wotr.network.ability.MainAttackPayload;
import com.wanderersoftherift.wotr.util.Ref;
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
        if (shouldUseOverrideAttackNew(player)) {
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

    private static boolean shouldUseOverrideAttackNew(Player player) {
        var result = new Ref<>(false);
        Arrays.stream(EquipmentSlot.values()).forEach(slot -> {
            var stack = player.getItemBySlot(slot);
            var modifierProviders = stack.getAllOfType(ModifierProvider.class);
            modifierProviders.forEach(it -> it.forEachModifier(stack, WotrEquipmentSlotFromMC.fromVanillaSlot(slot),
                    player, (modifierHolder, tier, roll, item) -> {
                        if (modifierHolder.value()
                                .getModifierTier(tier)
                                .stream()
                                .anyMatch(effect -> effect instanceof TriggerableAbilityModifier tam
                                        && tam.trigger().type() == WotrTrackedAbilityTriggers.MAIN_ATTACK)) {
                            result.setValue(true);
                        }
                    }));
        });
        return result.getValue();
    }

    private static void handleEndAttack() {
        Minecraft.getInstance().player.connection.send(new MainAttackPayload(false));
    }

    private static void handleStartAttack() {
        Minecraft.getInstance().player.connection.send(new MainAttackPayload(true));
    }
}
