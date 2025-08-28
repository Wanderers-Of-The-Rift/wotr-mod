package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.levelgen.CorridorBlender;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftPostProcessingStep;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.FilterJigsaws;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ShuffleJigsaws;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.DefaultLayoutFactory;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.ChaosLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.PredefinedRoomLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.RingLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.CachedRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.CoreRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.LayerGeneratableRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.GeneratorLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Opposite;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Or;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PerimeterGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

final class RiftConfigInitialization {

    static RiftConfig initializeConfig(ItemStack item, MinecraftServer server) {
        var random = RandomSource.create();
        var seedOptional = item.get(WotrDataComponentType.RiftConfig.RIFT_SEED);
        var themeOptional = item.get(WotrDataComponentType.RiftConfig.RIFT_THEME);
        var tierOptional = item.get(WotrDataComponentType.RiftConfig.ITEM_RIFT_TIER);
        var objectiveOptional = item.get(WotrDataComponentType.RiftConfig.RIFT_OBJECTIVE);
        long seed = Objects.requireNonNullElseGet(seedOptional, random::nextLong);
        var objective = Objects.requireNonNullElseGet(objectiveOptional, () -> defaultObjective(server, seed));
        var theme = Objects.requireNonNullElseGet(themeOptional, () -> getRandomTheme(server, seed));
        var tier = Objects.requireNonNullElse(tierOptional, 0);
        return new RiftConfig(
                tier, theme, objective, riftGen(seed, item, server), seed, new HashMap<>()
        );
    }

    private static RiftGenerationConfig riftGen(long seed, ItemStack item, MinecraftServer server) {
        /* todo rift-gen (presets?), remove optionals */
        return new RiftGenerationConfig(
                defaultLayout(), defaultRoomGenerator(), defaultPostProcessingSteps(), initializeJigsawProcessors()
        );
    }

    private static Holder<ObjectiveType> defaultObjective(MinecraftServer server, long seed) {
        return server.registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.OBJECTIVES)
                .getRandomElementOf(WotrTags.Objectives.RANDOM_SELECTABLE, RandomSource.create(seed + 668_453_148))
                .orElseThrow(() -> new IllegalStateException("No objectives available"));
    }

    private static List<RiftPostProcessingStep> defaultPostProcessingSteps() {
        return List.of(new CorridorBlender(new Or(new GeneratorLayout(), new Opposite(new GeneratorLayout()))));
    }

    private static Holder<RiftTheme> getRandomTheme(MinecraftServer server, long seed) {

        var themeRandom = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0),
                seed * 5624397638181617163L);

        Registry<RiftTheme> registry = server.registryAccess().lookupOrThrow(WotrRegistries.Keys.RIFT_THEMES);

        return registry.getRandomElementOf(WotrTags.RiftThemes.RANDOM_SELECTABLE, themeRandom)
                .orElseThrow(() -> new IllegalStateException("No rift themes available"));
    }

    private static List<JigsawListProcessor> initializeJigsawProcessors() {
        return ImmutableList.of(new FilterJigsaws(WanderersOfTheRift.MODID, "rift/ring_"), new ShuffleJigsaws());

    }

    private static RiftRoomGenerator.Factory defaultRoomGenerator() {
        return new CachedRiftRoomGenerator.Factory(
                new LayerGeneratableRiftRoomGenerator.Factory(
                        new PerimeterGeneratable(Blocks.BEDROCK.defaultBlockState()),
                        CoreRiftRoomGenerator.Factory.INSTANCE
                )
        );
    }

    private static RiftLayout.Factory defaultLayout() {
        return new DefaultLayoutFactory(defaultLayers());
    }

    private static ArrayList<LayeredRiftLayout.LayoutLayer.Factory> defaultLayers() {
        var result = new ArrayList<LayeredRiftLayout.LayoutLayer.Factory>();

        result.add(new PredefinedRoomLayer.Factory(
                new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_portal"),
                        RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY),
                new Vec3i(-1, -1, -1)));
        result.add(new RingLayer.Factory(new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_stable"),
                RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY), 5));
        result.add(new RingLayer.Factory(new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_unstable"),
                RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY), 10));
        result.add(new ChaosLayer.Factory(new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_chaos"),
                RoomRandomizerImpl.MULTI_SIZE_SPACE_HOLDER_FACTORY)));
        return result;
    }
}
