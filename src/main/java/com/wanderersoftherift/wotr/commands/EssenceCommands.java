package com.wanderersoftherift.wotr.commands;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.init.ModDataMaps;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class EssenceCommands extends BaseCommand {
    public EssenceCommands() {
        super("essence", Commands.LEVEL_OWNERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(Commands.literal("dumpEssencelessItems").executes(this::dumpUnspecified))
                .then(Commands.literal("dumpEssenceValues").executes(this::dumpSpecified));
    }

    private int dumpUnspecified(CommandContext<CommandSourceStack> cmd) {
        Registry<Item> registry = cmd.getSource().getServer().registryAccess().lookupOrThrow(Registries.ITEM);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("missingessence.json"))) {
            Gson gson = new Gson();
            gson.toJson(registry.stream()
                    .map(Item::builtInRegistryHolder)
                    .filter(x -> x.getData(ModDataMaps.ESSENCE_VALUE_DATA) == null)
                    .map(Holder::getRegisteredName)
                    .toList(), writer);
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    private int dumpSpecified(CommandContext<CommandSourceStack> cmd) {
        Registry<Item> registry = cmd.getSource().getServer().registryAccess().lookupOrThrow(Registries.ITEM);
        List<ResourceLocation> types = registry.stream()
                .map(Item::builtInRegistryHolder)
                .map(x -> x.getData(ModDataMaps.ESSENCE_VALUE_DATA))
                .filter(Objects::nonNull)
                .flatMap(x -> x.values().keySet().stream())
                .distinct()
                .toList();

        Joiner itemJoiner = Joiner.on("\",\"");

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("essence.csv"))) {
            writer.write("\"item\",\"");
            writer.write(itemJoiner.join(types.stream().map(ResourceLocation::toString).toList()));
            writer.write("\"\n");

            List<Holder.Reference<Item>> items = registry.stream().map(Item::builtInRegistryHolder).toList();
            for (var item : items) {
                EssenceValue data = item.getData(ModDataMaps.ESSENCE_VALUE_DATA);
                if (data == null) {
                    continue;
                }
                writer.write("\"");
                writer.write(item.getRegisteredName());
                writer.write("\"");
                for (ResourceLocation type : types) {
                    writer.write(",");
                    writer.write(Integer.toString(data.values().getOrDefault(type, 0)));
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }
}
