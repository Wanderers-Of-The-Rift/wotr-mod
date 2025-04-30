package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class AbilityCommands {

    private AbilityCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(WanderersOfTheRift.MODID + ":makeAbilityItem")
                .requires(sender -> sender.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("ability", AbilityArgument.ability())
                        .then(Commands.argument("choices", IntegerArgumentType.integer(1))
                                .executes((ctx) -> addAbilityToCurrentItem(ctx.getSource(),
                                        AbilityArgument.getAbility(ctx, "ability"),
                                        IntegerArgumentType.getInteger(ctx, "choices"))))));
    }

    private static int addAbilityToCurrentItem(CommandSourceStack source, AbstractAbility ability, int choices) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            ItemStack item = player.getInventory().getSelected();
            if (!item.isEmpty()) {

                AbilityUpgradePool.Mutable upgradePool = new AbilityUpgradePool.Mutable();
                upgradePool.generateChoices(source.getLevel().registryAccess(), ability, choices,
                        source.getLevel().random, 3);

                Registry<AbstractAbility> abilities = source.getLevel()
                        .registryAccess()
                        .lookupOrThrow(RegistryEvents.ABILITY_REGISTRY);

                DataComponentPatch patch = DataComponentPatch.builder()
                        .set(ModDataComponentType.ABILITY.get(), abilities.wrapAsHolder(ability))
                        .set(ModDataComponentType.ABILITY_UPGRADE_POOL.get(), upgradePool.toImmutable())
                        .build();
                item.applyComponents(patch);
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
}
