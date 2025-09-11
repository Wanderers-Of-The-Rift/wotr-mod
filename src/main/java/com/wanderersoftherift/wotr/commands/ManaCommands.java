package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityResourceData;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class ManaCommands extends BaseCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_ABILITY_RESOURCE = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("command." + WanderersOfTheRift.MODID + ".invalid_ability_resource",
                    id)); // todo translation

    public ManaCommands() {
        super("mana", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        var resource = "resource";
        builder.then(Commands.literal("refill")
                .executes(ctx -> refillMana(ctx, null))
                .then(Commands.argument(resource, ResourceKeyArgument.key(WotrRegistries.Keys.ABILITY_RESOURCES)))
                .executes(ctx -> refillMana(ctx, ResourceKeyArgument.resolveKey(ctx, resource,
                        WotrRegistries.Keys.ABILITY_RESOURCES, ERROR_INVALID_ABILITY_RESOURCE))));
    }

    private int refillMana(CommandContext<CommandSourceStack> stack, Holder<AbilityResource> resource) {
        AbilityResourceData data = stack.getSource().getEntity().getData(WotrAttachments.MANA);
        if (resource == null) {
            data.getAmounts()
                    .keySet()
                    .forEach(
                            it -> {
                                data.setAmount(it, it.value().maxForEntity(stack.getSource().getEntity()));
                            }
                    );
        } else {
            data.setAmount(resource, resource.value().maxForEntity(stack.getSource().getEntity()));
        }
        return Command.SINGLE_SUCCESS;
    }
}
