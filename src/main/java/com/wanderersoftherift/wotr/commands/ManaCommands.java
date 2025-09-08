package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ManaCommands extends BaseCommand {

    public ManaCommands() {
        super("mana", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(Commands.literal("refill").executes(this::refillMana));
    }

    private int refillMana(CommandContext<CommandSourceStack> stack) {
        ManaData data = stack.getSource().getPlayer().getData(WotrAttachments.MANA);
        data.setAmount(data.maxAmount());
        return Command.SINGLE_SUCCESS;
    }
}
