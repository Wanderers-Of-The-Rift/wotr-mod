package com.wanderersoftherift.wotr.client.render.blockentity;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.client.WotrShaders;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;

public class AnomalyBlockEntityRenderer implements BlockEntityRenderer<AnomalyBlockEntity> {

    public static final RenderStateShard.ShaderStateShard ANOMALY_SHADER_STATE = new RenderStateShard.ShaderStateShard(
            WotrShaders.RIFT_PORTAL);
    private static final ResourceLocation OUTER_ANOMALY_LOCATION = WanderersOfTheRift
            .id("textures/entity/outer_rift.png");
    private static final ResourceLocation INNER_ANOMALY_LOCATION = WanderersOfTheRift
            .id("textures/entity/inner_rift.png");
    private static final Function<ResourceLocation, RenderType> RENDER_TYPE = Util.memoize(inner -> RenderType.create(
            "anomaly", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false,
            RenderType.CompositeState.builder()
                    .setShaderState(ANOMALY_SHADER_STATE)
                    .setTextureState(new RenderStateShard.MultiTextureStateShard.Builder()
                            .add(OUTER_ANOMALY_LOCATION, false, false)
                            .add(inner, false, false)
                            .build())
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(RenderStateShard.NO_OVERLAY)
                    .createCompositeState(false)));

    public AnomalyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static RenderType getRenderType(AnomalyBlockEntity anomalyBlockEntity) {
        var panoramaTexture = anomalyBlockEntity.getPanorama();
        if (panoramaTexture == null) {
            panoramaTexture = INNER_ANOMALY_LOCATION;
        }
        return RENDER_TYPE.apply(panoramaTexture);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight,
            float x,
            float y,
            float z,
            int u,
            int v) {
        consumer.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 0.0F, -1.0F);
    }

    @Override
    public void render(
            AnomalyBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        float width = 0.6f * blockEntity.getScale();
        float height = 0.8f;
        poseStack.scale(width, height, width);

        Vector3f cameraPos = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition().toVector3f();
        Vector3f blockPos = new Vector3f((float) blockEntity.getBlockPos().getX() + 0.5f,
                (float) blockEntity.getBlockPos().getY() + 0.5f, (float) blockEntity.getBlockPos().getZ() + 0.5f);
        Vector3f dir = blockPos.sub(cameraPos);
        dir.y = 0;
        if (dir.lengthSquared() < 0.001f) {
            dir = new Vector3f(1, 0, 0);
        }

        CompiledShaderProgram shader = RenderSystem.setShader(WotrShaders.RIFT_PORTAL);
        if (shader != null) {
            Uniform screenSize = shader.getUniform("ScreenSize");
            if (screenSize != null) {
                screenSize.set((float) Minecraft.getInstance().getWindow().getWidth(),
                        (float) Minecraft.getInstance().getWindow().getHeight());
            }

            Uniform view = shader.getUniform("View");
            if (view != null) {
                float x = Minecraft.getInstance().getEntityRenderDispatcher().camera.getXRot();
                float y = Mth.wrapDegrees(Minecraft.getInstance().getEntityRenderDispatcher().camera.getYRot());
                view.set(x, y);
            }
            shader.apply();
        }

        poseStack.mulPose(new Quaternionf().lookAlong(dir, new Vector3f(0, -1, 0)));
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer vertexConsumer = buffer.getBuffer(getRenderType(blockEntity));

        vertex(vertexConsumer, pose, packedLight, -0.5f, -0.5f, 0.0f, 0, 1);
        vertex(vertexConsumer, pose, packedLight, 0.5f, -0.5f, 0.0f, 1, 1);
        vertex(vertexConsumer, pose, packedLight, 0.5f, 0.5f, 0.0f, 1, 0);
        vertex(vertexConsumer, pose, packedLight, -0.5f, 0.5f, 0.0f, 0, 0);

        poseStack.popPose();

        var anomalyState = blockEntity.getAnomalyState();
        if (anomalyState == null) {
            return;
        }
        var taskDisplay = anomalyState.display();
        if (taskDisplay == null) {
            return;
        }

        int itemCount = taskDisplay.getCount();
        if (itemCount != 0) {
            float radius = 0.35f;
            float yOffset = 0.3f;
            var blockRenderer = Minecraft.getInstance().getItemRenderer();
            long time = blockEntity.getLevel().getGameTime();
            float rotationSpeed = 0.05f;

            taskDisplay.forEachIndexed((i, item) -> {

                poseStack.pushPose();
                poseStack.translate(0.5, 0.4, 0.5);
                double angle = (2 * Math.PI / itemCount) * i + (time * rotationSpeed);
                float x = Mth.cos((float) angle) * radius;
                float z = Mth.sin((float) angle) * radius;
                poseStack.translate(x, yOffset, z);
                poseStack.scale(0.2f, 0.2f, 0.2f);

                blockRenderer.renderStatic(item, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack,
                        buffer, blockEntity.getLevel(), 0);
                poseStack.popPose();
            });
        }
    }
}
