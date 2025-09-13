package com.wanderersoftherift.wotr.commands;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.config.preset.HudPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Developer commands for HUD
 */
public class HudCommands extends BaseCommand {

    public HudCommands() {
        super("hud", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(Commands.literal("exportPreset").executes(this::exportPrefab));
    }

    private int exportPrefab(CommandContext<CommandSourceStack> ctx) {
        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        var hudPreset = HudPreset.fromConfig(registryAccess);

        DataResult<JsonElement> json = HudPreset.MAP_CODEC.encodeStart(JsonOps.INSTANCE, hudPreset);
        json.ifSuccess(x -> {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("hud_preset.json"))) {
                writer.write(x.toString());
            } catch (IOException e) {
                WanderersOfTheRift.LOGGER.error("Failed to write out hud preset", e);
            }
        });

        return 0;
    }
}
