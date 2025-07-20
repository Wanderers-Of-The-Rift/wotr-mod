package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredFiniteRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.ChaosLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.PredefinedRoomLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.RingLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.CoarseDiamondRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.Optional;

@EventBusSubscriber
public class RiftConfigInitialization {

    public static final int DEFAULT_RIFT_HEIGHT_IN_CHUNKS = 24;

    static RiftConfig initializeConfig(RiftConfig baseConfig, MinecraftServer server) {
        var random = RandomSource.create();
        int seed = baseConfig.seed().orElseGet(random::nextInt);
        var riftTheme = baseConfig.theme().orElse(getRandomTheme(server, seed));
        var config = baseConfig.withSeedIfAbsent(seed)
                .withThemeIfAbsent(riftTheme)
                .withLayoutIfAbsent(baseConfig.layout().orElse(defaultLayout(baseConfig.tier(), seed)));
        return config;
    }

    @SubscribeEvent
    private static void appendObjectiveGenerationStuff(RiftEvent.Created.Pre event) {
        var config = event.getConfig();
        var objectiveOptional = config.objective();
        if (!objectiveOptional.isPresent()) {
            return;
        }
        var objective = objectiveOptional.get().value();
        var layout = config.layout().get();
        if (layout instanceof LayeredRiftLayout.Factory layeredLayout) {
            var layers = new ArrayList<>(layeredLayout.layers());
            /*
             * layers.add(0, new PredefinedRoomLayer.Factory( new
             * RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_portal"),
             * RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY), new Vec3i(-1, -1, 2)));
             */
            layout = layeredLayout.withLayers(layers);
        }
        event.setConfig(config.withLayout(layout));
    }

    @SubscribeEvent
    private static void appendQuestGenerationStuff(RiftEvent.Created.Pre event) {

    }

    public static Holder<RiftTheme> getRandomTheme(MinecraftServer server, int seed) {

        var themeRandom = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0),
                seed * 5624397638181617163L);

        Registry<RiftTheme> registry = server.registryAccess().lookupOrThrow(WotrRegistries.Keys.RIFT_THEMES);

        return registry.getRandomElementOf(WotrTags.RiftThemes.RANDOM_SELECTABLE, themeRandom)
                .orElseThrow(() -> new IllegalStateException("No rift themes available"));
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
        result.add(new RingLayer.Factory(
                new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_unstable"),
                        RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY),
                10));
        result.add(new ChaosLayer.Factory(new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_chaos"),
                RoomRandomizerImpl.MULTI_SIZE_SPACE_HOLDER_FACTORY)));
        return result;
    }
}
