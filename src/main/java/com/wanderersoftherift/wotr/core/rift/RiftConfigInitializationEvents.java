package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ReplaceJigsaws;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ReplaceJigsawsBulk;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.PredefinedRoomLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import net.minecraft.core.Vec3i;

import java.util.Map;

// @EventBusSubscriber
public class RiftConfigInitializationEvents {

    // @SubscribeEvent
    private static void example(RiftEvent.Created.Pre event) {
        var config = event.getConfig();
        var riftGenConfig = config.riftGen();

        // get objective:
        var objectiveOptional = config.objective();
        if (objectiveOptional.isEmpty()) {
            return;
        }
        var objective = objectiveOptional.get().value();

        // get openning player:
        var player = event.getFirstPlayer();

        // for replacing POIs:
        var newJigsawProcessors = ImmutableList.<JigsawListProcessor>builder()
                .add(new ReplaceJigsaws(WanderersOfTheRift.id("rift/poi/free/5"),
                        WanderersOfTheRift.id("rift/new_pool"), 1))
                .addAll(riftGenConfig.jigsawProcessors())
                .build();
        riftGenConfig = riftGenConfig.withJigsawProcessors(newJigsawProcessors);

        var layout = riftGenConfig.layout().get();
        if (layout instanceof LayeredRiftLayout.Factory layeredLayout) {
            riftGenConfig = riftGenConfig
                    .withLayout(layeredLayout.withLayers(ImmutableList.<LayeredRiftLayout.LayoutLayer.Factory>builder()
                            .add(
                                    // For placing special rooms:
                                    new PredefinedRoomLayer.Factory(
                                            new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_portal"),
                                                    RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY),
                                            new Vec3i(-1, -1, 2))
                            )
                            .addAll(layeredLayout.layers())
                            .build()));
        }
        event.setConfig(config.withRiftGenerationConfig(riftGenConfig));
    }

    // @SubscribeEvent(priority = EventPriority.LOW)
    private static void appendDefaultAnomalies(RiftEvent.Created.Pre event) {
        var config = event.getConfig();
        var riftGenConfig = config.riftGen();
        var chance = 0.1f;
        var newJigsawProcessors = ImmutableList.<JigsawListProcessor>builder()
                .add(new ReplaceJigsawsBulk(Map.of(
                        WanderersOfTheRift.id("rift/poi/free/3"),
                        new ReplaceJigsawsBulk.Replacement(WanderersOfTheRift.id("rift/anomaly/free/3"), chance),
                        WanderersOfTheRift.id("rift/poi/ceiling/3"),
                        new ReplaceJigsawsBulk.Replacement(WanderersOfTheRift.id("rift/anomaly/ceiling/3"), chance),
                        WanderersOfTheRift.id("rift/poi/halfway/3"),
                        new ReplaceJigsawsBulk.Replacement(WanderersOfTheRift.id("rift/anomaly/halfway/3"), chance),
                        WanderersOfTheRift.id("rift/poi/inwall/3"),
                        new ReplaceJigsawsBulk.Replacement(WanderersOfTheRift.id("rift/anomaly/inwall/3"), chance)
                )))
                .addAll(riftGenConfig.jigsawProcessors())
                .build();
        riftGenConfig = riftGenConfig.withJigsawProcessors(newJigsawProcessors);
        event.setConfig(config.withRiftGenerationConfig(riftGenConfig));
    }

    // @SubscribeEvent
    private static void appendRandomSizeReduction(RiftEvent.Created.Pre event) {
        var config = event.getConfig();
        var riftGenConfig = config.riftGen();
        var chance = 0.2f;
        var newJigsawProcessors = ImmutableList.<JigsawListProcessor>builder()
                .add(new ReplaceJigsawsBulk(Map.of(
                        WanderersOfTheRift.id("rift/poi/free/5"),
                        new ReplaceJigsawsBulk.Replacement(WanderersOfTheRift.id("rift/anomaly/free/5"), chance),
                        WanderersOfTheRift.id("rift/poi/ceiling/5"),
                        new ReplaceJigsawsBulk.Replacement(WanderersOfTheRift.id("rift/anomaly/ceiling/5"), chance),
                        WanderersOfTheRift.id("rift/poi/halfway/5"),
                        new ReplaceJigsawsBulk.Replacement(WanderersOfTheRift.id("rift/anomaly/halfway/5"), chance),
                        WanderersOfTheRift.id("rift/poi/inwall/5"),
                        new ReplaceJigsawsBulk.Replacement(WanderersOfTheRift.id("rift/anomaly/inwall/5"), chance)
                )))
                .addAll(riftGenConfig.jigsawProcessors())
                .build();
        riftGenConfig = riftGenConfig.withJigsawProcessors(newJigsawProcessors);
        event.setConfig(config.withRiftGenerationConfig(riftGenConfig));
    }
}
