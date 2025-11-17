package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.block.blockentity.RiftChestBlockEntity;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.loot.WotrLootContextParams;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootCommands extends BaseCommand {

    private static final String LOOT_TABLE_ARG = "loot_table";
    private static final String RIFT_TIER_ARG = "rift_tier";
    private static final String LOCATION_ARG = "location";
    private static final String FROM_ARG = "from";
    private static final String TO_ARG = "to";

    public LootCommands() {
        super("loot", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {

        builder.then(Commands.literal("generateInChest")
                .then(Commands.argument(LOCATION_ARG, BlockPosArgument.blockPos())
                        .then(Commands.argument(LOOT_TABLE_ARG, ResourceOrIdArgument.lootTable(context))
                                .suggests(LootCommand.SUGGEST_LOOT_TABLE)
                                .executes(ctx -> createChest(ctx, BlockPosArgument.getBlockPos(ctx, LOCATION_ARG),
                                        ResourceOrIdArgument.getLootTable(ctx, LOOT_TABLE_ARG), 1))
                                .then(Commands.argument(RIFT_TIER_ARG, IntegerArgumentType.integer(1))
                                        .executes(
                                                ctx -> createChest(ctx, BlockPosArgument.getBlockPos(ctx, LOCATION_ARG),
                                                        ResourceOrIdArgument.getLootTable(ctx, LOOT_TABLE_ARG),
                                                        IntegerArgumentType.getInteger(ctx, RIFT_TIER_ARG)))))));
        builder.then(Commands.literal("generateInChestArea")
                .then(Commands.argument(FROM_ARG, BlockPosArgument.blockPos())
                        .then(Commands.argument(TO_ARG, BlockPosArgument.blockPos())
                                .then(Commands.argument(LOOT_TABLE_ARG, ResourceOrIdArgument.lootTable(context))
                                        .suggests(LootCommand.SUGGEST_LOOT_TABLE)
                                        .executes(ctx -> createChests(ctx, BlockPosArgument.getBlockPos(ctx, FROM_ARG),
                                                BlockPosArgument.getBlockPos(ctx, TO_ARG),
                                                ResourceOrIdArgument.getLootTable(ctx, LOOT_TABLE_ARG), 1))
                                        .then(Commands.argument(RIFT_TIER_ARG, IntegerArgumentType.integer(1))
                                                .executes(
                                                        ctx -> createChests(ctx,
                                                                BlockPosArgument.getBlockPos(ctx, FROM_ARG),
                                                                BlockPosArgument.getBlockPos(ctx, TO_ARG),
                                                                ResourceOrIdArgument.getLootTable(ctx, LOOT_TABLE_ARG),
                                                                IntegerArgumentType.getInteger(ctx,
                                                                        RIFT_TIER_ARG))))))));
    }

    private int createChests(
            CommandContext<CommandSourceStack> ctx,
            BlockPos from,
            BlockPos to,
            Holder<LootTable> lootTable,
            int riftTier) {
        BoundingBox area = BoundingBox.fromCorners(from, to);
        int result = 0;
        for (BlockPos pos : BlockPos.betweenClosed(area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(),
                area.maxZ())) {
            result += createChest(ctx, pos, lootTable, riftTier);
        }
        return result;
    }

    private int createChest(
            CommandContext<CommandSourceStack> ctx,
            BlockPos pos,
            Holder<LootTable> lootTable,
            int riftTier) {
        ServerLevel level = ctx.getSource().getLevel();
        level.setBlock(pos, WotrBlocks.RIFT_CHEST.get().defaultBlockState(), Block.UPDATE_ALL);
        if (level.getBlockEntity(pos) instanceof RiftChestBlockEntity chest) {
            chest.clearContent();
            LootParams.Builder builder = new LootParams.Builder(level).withParameter(WotrLootContextParams.RIFT_TIER,
                    riftTier);
            if (ctx.getSource().getPlayer() != null) {
                builder.withParameter(LootContextParams.THIS_ENTITY, ctx.getSource().getPlayer());
            }
            LootParams lootParams = builder.create(LootContextParamSets.EMPTY);
            ObjectArrayList<ItemStack> randomItems = lootTable.value().getRandomItems(lootParams, level.getRandom());
            for (int i = 0; i < randomItems.size(); i++) {
                chest.setItem(i, randomItems.get(i));
            }
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

}
