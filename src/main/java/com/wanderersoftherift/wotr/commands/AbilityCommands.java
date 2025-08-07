package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class AbilityCommands {

    private static final DynamicCommandExceptionType ERROR_INVALID_ABILITY = new DynamicCommandExceptionType(
            templateId -> Component.translatableEscape(WanderersOfTheRift.translationId("commands", "ability.invalid"),
                    templateId));

    private AbilityCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(WanderersOfTheRift.MODID + ":makeAbilityItem")
                .requires(sender -> sender.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("ability", ResourceKeyArgument.key(WotrRegistries.Keys.ABILITIES))
                        .then(Commands.argument("choices", IntegerArgumentType.integer(1))
                                .executes((ctx) -> addAbilityToCurrentItem(ctx.getSource(),
                                        ResourceKeyArgument.resolveKey(ctx, "ability", WotrRegistries.Keys.ABILITIES,
                                                ERROR_INVALID_ABILITY),
                                        IntegerArgumentType.getInteger(ctx, "choices"))))));
    }

    private static int addAbilityToCurrentItem(CommandSourceStack source, Holder<Ability> ability, int choices) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            ItemStack item = player.getInventory().getSelected();
            ItemStack newItem = generateAbilityItem(item, ability, choices, source.getLevel());
            if (!newItem.isEmpty()) {
                source.sendSuccess(
                        () -> Component
                                .translatable(WanderersOfTheRift.translationId("command", "make_ability_item.success")),
                        true);

            }
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    public static ItemStack generateAbilityItem(
            ItemStack item,
            Holder<Ability> ability,
            int choices,
            ServerLevel serverLevel) {
        if (item.isEmpty()) {
            return item;
        }
        AbilityUpgradePool.Mutable upgradePool = new AbilityUpgradePool.Mutable();
        upgradePool.generateChoices(serverLevel.registryAccess(), ability.value(), choices, serverLevel.random, 3);
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(WotrDataComponentType.ABILITY.get(), ability)
                .set(WotrDataComponentType.ABILITY_UPGRADE_POOL.get(), upgradePool.toImmutable())
                .build();
        item.applyComponents(patch);
        return item;
    }
}
