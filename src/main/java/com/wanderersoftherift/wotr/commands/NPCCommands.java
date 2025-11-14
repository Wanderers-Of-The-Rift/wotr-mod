package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;

import java.util.Optional;

public class NPCCommands extends BaseCommand {

    private static final String NPC_ARG = "npc";
    private static final String LOCATION_ARG = "location";
    private static final String MOB_ARG = "mob";

    public NPCCommands() {
        super("npc", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {

        builder.then(
                Commands.literal("makeInto")
                        .then(Commands.argument(MOB_ARG, EntityArgument.entity())
                                .then(Commands
                                        .argument(NPC_ARG, ResourceArgument.resource(context, WotrRegistries.Keys.NPCS))
                                        .executes(ctx -> makeNpc(ctx, EntityArgument.getEntity(ctx, MOB_ARG),
                                                ResourceArgument.getResource(ctx, NPC_ARG, WotrRegistries.Keys.NPCS)))
                                )
                        )
        );

        builder.then(
                Commands.literal("create")
                        .then(Commands.argument(NPC_ARG, ResourceArgument.resource(context, WotrRegistries.Keys.NPCS))
                                .executes(ctx -> createNpc(ctx,
                                        ResourceArgument.getResource(ctx, NPC_ARG, WotrRegistries.Keys.NPCS),
                                        BlockPos.containing(ctx.getSource().getPosition())))
                                .then(Commands.argument(LOCATION_ARG, BlockPosArgument.blockPos())
                                        .executes(ctx -> createNpc(ctx,
                                                ResourceArgument.getResource(ctx, NPC_ARG, WotrRegistries.Keys.NPCS),
                                                BlockPosArgument.getBlockPos(ctx, LOCATION_ARG)))
                                )));
    }

    private int makeNpc(CommandContext<CommandSourceStack> ctx, Entity mob, Holder<NpcIdentity> npcIdentity) {
        mob.setData(WotrAttachments.NPC_IDENTITY, new NpcIdentity.Attachment(Optional.of(npcIdentity)));
        mob.setData(WotrAttachments.NPC_INTERACT, npcIdentity.value().npcInteraction());
        mob.setCustomName(NpcIdentity.getDisplayName(npcIdentity));
        return Command.SINGLE_SUCCESS;
    }

    private int createNpc(
            CommandContext<CommandSourceStack> ctx,
            Holder.Reference<NpcIdentity> npcIdentityReference,
            BlockPos location) {
        Entity result = NpcIdentity.spawn(npcIdentityReference, ctx.getSource().getLevel(), location.above(),
                EntitySpawnReason.COMMAND);
        if (result != null) {
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}
