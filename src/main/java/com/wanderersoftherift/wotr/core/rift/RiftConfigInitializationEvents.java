package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ReplaceJigsaws;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ReplaceJigsawsBulk;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.PredefinedRoomLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

// @EventBusSubscriber
public class RiftConfigInitializationEvents {
    private static final ImmutableList<String> POI_VARIANTS = ImmutableList.of("free", "ceiling", "halfway", "inwall");

    // @SubscribeEvent
    private static void example(RiftEvent.Created.Pre event) {
        var config = event.getConfig();
        var riftGenConfig = config.getCustomData(WotrRiftConfigDataTypes.RIFT_GENERATOR_CONFIG);

        // get objective:
        var objectiveHolder = config.objective();
        var objective = objectiveHolder.value();

        // get opening player:
        var player = event.getFirstPlayer();

        // for replacing POIs:
        var newJigsawProcessors = ImmutableList.<JigsawListProcessor>builder()
                .add(new ReplaceJigsaws(WanderersOfTheRift.id("rift/poi/free/5"),
                        WanderersOfTheRift.id("rift/new_pool"), 1))
                .addAll(riftGenConfig.jigsawProcessors())
                .build();
        riftGenConfig = riftGenConfig.withJigsawProcessors(newJigsawProcessors);

        var layout = riftGenConfig.layout();
        if (layout instanceof LayeredRiftLayout.Factory layeredLayout) {
            var access = event.getFirstPlayer().server.registryAccess();
            riftGenConfig = riftGenConfig
                    .withLayout(layeredLayout.withLayers(ImmutableList.<LayeredRiftLayout.LayoutLayer.Factory>builder()
                            .add(
                                    // For placing special rooms:
                                    new PredefinedRoomLayer.Factory(
                                            new RoomRandomizerImpl.Factory(
                                                    access.holderOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL,
                                                            WanderersOfTheRift.id("rift/room_portal"))),
                                                    RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY),
                                            new Vec3i(-1, -1, 2))
                            )
                            .addAll(layeredLayout.layers())
                            .build()));
        }
        event.setConfig(config.withCustomData(WotrRiftConfigDataTypes.RIFT_GENERATOR_CONFIG, riftGenConfig));
    }

    // @SubscribeEvent(priority = EventPriority.LOW)
    private static void appendDefaultAnomalies(RiftEvent.Created.Pre event) {
        var config = event.getConfig();
        var riftGenConfig = config.getCustomData(WotrRiftConfigDataTypes.RIFT_GENERATOR_CONFIG);
        var chance = 0.15f;
        var replacementMap = new HashMap<ResourceLocation, ReplaceJigsawsBulk.Replacement>();
        for (var variant : POI_VARIANTS) {
            for (int size = 3; size <= 3 /* todo increase once we have more anomaly POIs */; size += 2) {
                replacementMap.put(
                        WanderersOfTheRift.id("rift/poi/" + variant + "/" + size), new ReplaceJigsawsBulk.Replacement(
                                WanderersOfTheRift.id("rift/anomaly/" + variant + "/" + size), chance));
            }
        }
        var newJigsawProcessors = ImmutableList.<JigsawListProcessor>builder()
                .add(new ReplaceJigsawsBulk(replacementMap))
                .addAll(riftGenConfig.jigsawProcessors())
                .build();
        riftGenConfig = riftGenConfig.withJigsawProcessors(newJigsawProcessors);
        event.setConfig(config.withCustomData(WotrRiftConfigDataTypes.RIFT_GENERATOR_CONFIG, riftGenConfig));
    }

    // @SubscribeEvent
    private static void appendRandomSizeReduction(RiftEvent.Created.Pre event) {
        var config = event.getConfig();
        var riftGenConfig = config.getCustomData(WotrRiftConfigDataTypes.RIFT_GENERATOR_CONFIG);
        var chance = 0.25f;

        var replacementMap = new HashMap<ResourceLocation, ReplaceJigsawsBulk.Replacement>();
        for (var variant : POI_VARIANTS) {
            for (int size = 3; size < 11; size += 2) {
                replacementMap.put(
                        WanderersOfTheRift.id("rift/poi/" + variant + "/" + (size + 2)),
                        new ReplaceJigsawsBulk.Replacement(
                                WanderersOfTheRift.id("rift/poi/" + variant + "/" + size), chance));
            }
        }
        var newJigsawProcessors = ImmutableList.<JigsawListProcessor>builder()
                .add(new ReplaceJigsawsBulk(replacementMap))
                .addAll(riftGenConfig.jigsawProcessors())
                .build();
        riftGenConfig = riftGenConfig.withJigsawProcessors(newJigsawProcessors);
        event.setConfig(config.withCustomData(WotrRiftConfigDataTypes.RIFT_GENERATOR_CONFIG, riftGenConfig));
    }
}
