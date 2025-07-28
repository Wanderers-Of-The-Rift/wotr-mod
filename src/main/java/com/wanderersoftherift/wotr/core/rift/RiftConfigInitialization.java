package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.item.riftkey.RiftGenerationConfig;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.FilterJigsaws;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ShuffleJigsaws;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredFiniteRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.ChaosLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.PredefinedRoomLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.RingLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.CoarseDiamondRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.CachedRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.CoreRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.LayerGeneratableRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PerimeterGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RiftConfigInitialization {

    public static final int DEFAULT_RIFT_HEIGHT_IN_CHUNKS = 24;

    static RiftConfig initializeConfig(RiftConfig baseConfig, MinecraftServer server, ServerPlayer firstPlayer) {
        var random = RandomSource.create();
        int seed = baseConfig.riftGen().seed().orElseGet(random::nextInt);
        var riftTheme = baseConfig.theme().orElse(getRandomTheme(server, seed));

        return baseConfig.withRiftGenerationConfig(
                new RiftGenerationConfig(
                        Optional.of(baseConfig.riftGen().layout().orElse(defaultLayout(baseConfig.tier(), seed))),
                        Optional.of(defaultRoomGenerator()), true, initializeJigsawProcessors(), Optional.of(seed)
                )
        ).withThemeIfAbsent(riftTheme);
    }

    static Holder<RiftTheme> getRandomTheme(MinecraftServer server, int seed) {

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

    private static RiftLayout.Factory defaultLayout(int tier, int seed) {
        var layerCount = DEFAULT_RIFT_HEIGHT_IN_CHUNKS - FastRiftGenerator.MARGIN_LAYERS;
        var factory = new LayeredFiniteRiftLayout.Factory(
                new BoxedRiftShape(new CoarseDiamondRiftShape(2 + tier * 3, 3.0, layerCount),
                        new Vec3i(-1 - 3 * tier, -layerCount / 2, -1 - 3 * tier),
                        new Vec3i(3 + 6 * tier, layerCount, 3 + 6 * tier)),
                Optional.of(seed), defaultLayers());
        return factory;
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
