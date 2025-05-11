package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.network.S2CRiftObjectiveStatusPacket;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.ModOngoingObjectiveTypes.ONGOING_OBJECTIVE_TYPE_REGISTRY;

public class ObjectiveRenderers {

    public static final Map<ResourceLocation, Function<OngoingObjective, ObjectiveRenderer>> RENDERERS = new HashMap<>();

    public static final Function<OngoingObjective, ObjectiveRenderer> STEALTH = register(
            WanderersOfTheRift.id("stealth"), StealthObjectiveRenderer::create);

    public static final Function<OngoingObjective, ObjectiveRenderer> KILL = register(WanderersOfTheRift.id("kill"),
            GeneralObjectiveBarRenderer::create);

    public static Function<OngoingObjective, ObjectiveRenderer> register(
            ResourceLocation id,
            Function<OngoingObjective, ObjectiveRenderer> renderer) {
        RENDERERS.put(id, renderer);
        return renderer;
    }

    public static Function<OngoingObjective, ObjectiveRenderer> get(ResourceLocation id) {
        return RENDERERS.get(id);
    }

    public static void handleObjectiveStatus(S2CRiftObjectiveStatusPacket packet) {
        if (packet.objective().isPresent()) {
            OngoingObjective objective = packet.objective().get();
            ResourceLocation key = ONGOING_OBJECTIVE_TYPE_REGISTRY.getKey(objective.getCodec());
            Function<OngoingObjective, ObjectiveRenderer> renderer = get(key);
            if (renderer != null) {
                ObjectiveRenderer.current = renderer.apply(objective);
            } else {
                ObjectiveRenderer.current = null;
            }
        } else {
            ObjectiveRenderer.current = null;
        }
    }
}
