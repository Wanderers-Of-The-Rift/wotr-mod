package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.guild.WalletUpdatePayload;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;

public class CurrencyCommands extends BaseCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_CURRENCY = new DynamicCommandExceptionType(
            templateId -> Component.translatableEscape(WanderersOfTheRift.translationId("commands", "currency.invalid"),
                    templateId));

    public CurrencyCommands() {
        super("currency", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String currencyArg = "currency";
        String amountArg = "amount";
        builder.then(Commands.literal("set")
                .then(Commands.argument(currencyArg, ResourceKeyArgument.key(WotrRegistries.Keys.CURRENCIES))
                        .then(Commands.argument(amountArg, IntegerArgumentType.integer(0))
                                .executes(
                                        ctx -> setCurrency(
                                                ctx,
                                                ResourceKeyArgument.resolveKey(ctx, currencyArg,
                                                        WotrRegistries.Keys.CURRENCIES, ERROR_INVALID_CURRENCY),
                                                IntegerArgumentType.getInteger(ctx, amountArg))
                                ))));
    }

    private int setCurrency(CommandContext<CommandSourceStack> ctx, Holder<Currency> currency, int amount) {
        Wallet wallet = ctx.getSource().getPlayer().getData(WotrAttachments.WALLET.get());
        wallet.set(currency, amount);
        PacketDistributor.sendToPlayer(ctx.getSource().getPlayer(), new WalletUpdatePayload(Map.of(currency, amount)));
        return 1;
    }
}
