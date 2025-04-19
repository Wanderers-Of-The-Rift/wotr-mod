package com.wanderersoftherift.wotr;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs

/*@EventBusSubscriber(modid = Wotr.MODID, bus = EventBusSubscriber.Bus.MOD)*/
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ALLOW_PVP = BUILDER.comment("Whether player vs player is allowed")
            .define("allowPvP", false);

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean allowPvP;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    // This was being called upon Client Config load as well, causing it to query config values before the common-config
    // was loaded.
    /*
     * @SubscribeEvent static void onLoad(final ModConfigEvent event) { logDirtBlock = LOG_DIRT_BLOCK.get(); magicNumber
     * = MAGIC_NUMBER.get(); magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
     * 
     * //convert the list of strings into a set of items items = ITEM_STRINGS.get().stream().map(itemName ->
     * BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName))).collect(Collectors.toSet()); }
     */
}
