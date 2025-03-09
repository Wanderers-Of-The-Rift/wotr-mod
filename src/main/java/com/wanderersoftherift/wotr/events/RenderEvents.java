package com.wanderersoftherift.wotr.events;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.render.ModelWireframeExtractor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class RenderEvents {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final Map<BlockState, List<ModelWireframeExtractor.RenderLine>> cachedWireFrames = new Reference2ObjectOpenHashMap<>();


    public static void resetCached() {
        cachedWireFrames.clear();
    }

    @SubscribeEvent
    public static void onBlockHover(RenderHighlightEvent.Block event) {
        if(true) return; // TODO: Temporary instant return until we have block models that should be accurate to their BakedModel

        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        BlockHitResult rayTraceResult = event.getTarget();
        if (rayTraceResult.getType() != HitResult.Type.MISS) {
            Level world = player.level();
            BlockPos pos = rayTraceResult.getBlockPos();
            MultiBufferSource renderer = event.getMultiBufferSource();
            Camera info = event.getCamera();
            PoseStack matrix = event.getPoseStack();

            BlockState blockState = world.getBlockState(pos);


            if (!blockState.isAir() && world.getWorldBorder().isWithinBounds(pos) /* && TODO: Each custom model that we want to accurately display needs to be checked here */) {
                matrix.pushPose();
                Vec3 viewPosition = info.getPosition();
                matrix.translate(pos.getX() - viewPosition.x, pos.getY() - viewPosition.y, pos.getZ() - viewPosition.z);
                renderBlockWireFrame(blockState, renderer.getBuffer(RenderType.lines()), matrix, world.random);
                matrix.popPose();

                event.setCanceled(true);
            }
        }
    }




    private static void renderBlockWireFrame(BlockState state, VertexConsumer buffer, PoseStack matrix, RandomSource rand) {
        List<ModelWireframeExtractor.RenderLine> lines = cachedWireFrames.computeIfAbsent(state, key -> {
            BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            return ModelWireframeExtractor.extract(bakedModel, state, rand, ModelData.EMPTY, null);
        });

        PoseStack.Pose pose = matrix.last();
        renderVertexWireFrame(lines, buffer, pose.pose(), pose.normal());
    }

    public static void renderVertexWireFrame(Collection<ModelWireframeExtractor.RenderLine> lines, VertexConsumer buffer, Matrix4f pose, Matrix3f poseNormal) {
        Vector4f pos = new Vector4f();
        Vector3f normal = new Vector3f();

        for (ModelWireframeExtractor.RenderLine line : lines) {
            poseNormal.transform(line.nX(), line.nY(), line.nZ(), normal);

            pose.transform(line.x1(), line.y1(), line.z1(), 1F, pos);
            buffer.addVertex(pos.x, pos.y, pos.z)
                    .setColor(0, 0, 0, 102)
                    .setNormal(normal.x, normal.y, normal.z);

            pose.transform(line.x2(), line.y2(), line.z2(), 1F, pos);
            buffer.addVertex(pos.x, pos.y, pos.z)
                    .setColor(0, 0, 0, 102)
                    .setNormal(normal.x, normal.y, normal.z);
        }
    }


}
