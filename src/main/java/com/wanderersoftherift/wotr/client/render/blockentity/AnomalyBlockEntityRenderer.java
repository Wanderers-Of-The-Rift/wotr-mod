package com.wanderersoftherift.wotr.client.render.blockentity;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.client.WotrShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AnomalyBlockEntityRenderer implements BlockEntityRenderer<AnomalyBlockEntity> {

    private static final ResourceLocation OUTER_ANOMALY_LOCATION = WanderersOfTheRift
            .id("textures/entity/outer_rift.png");
    private static final ResourceLocation INNER_ANOMALY_LOCATION = WanderersOfTheRift.id("textures/entity/anomaly.png");
    private static final RenderType RENDER_TYPE = AnomalyBlockEntityRenderType.anomaly(OUTER_ANOMALY_LOCATION,
            INNER_ANOMALY_LOCATION);

    public AnomalyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
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
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
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

        // Move to center of block
        poseStack.translate(0.5, 0.5, 0.5);

        // Scale to half the size of rift portal
        float scaleMultiplier = blockEntity.getScale();
        float width = 0.6f * scaleMultiplier;
        float height = 0.8f;
        poseStack.scale(width, height, width);

        // Face camera while remaining vertical (horizontal rotation only)
        Vector3f cameraPos = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition().toVector3f();
        Vector3f blockPos = new Vector3f((float) blockEntity.getBlockPos().getX() + 0.5f,
                (float) blockEntity.getBlockPos().getY() + 0.5f, (float) blockEntity.getBlockPos().getZ() + 0.5f);
        Vector3f dir = blockPos.sub(cameraPos); // This matches RiftPortalRenderer: entity position - camera position
        dir.y = 0; // Remove vertical component for horizontal-only rotation
        if (dir.lengthSquared() < 0.001f) {
            dir = new Vector3f(1, 0, 0);
        }

        // Set up shader uniforms like rift portal
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
        VertexConsumer vertexConsumer = buffer.getBuffer(RENDER_TYPE);

        vertex(vertexConsumer, pose, packedLight, -0.5f, -0.5f, 0.0f, 0, 1);
        vertex(vertexConsumer, pose, packedLight, 0.5f, -0.5f, 0.0f, 1, 1);
        vertex(vertexConsumer, pose, packedLight, 0.5f, 0.5f, 0.0f, 1, 0);
        vertex(vertexConsumer, pose, packedLight, -0.5f, 0.5f, 0.0f, 0, 0);

        poseStack.popPose();
    }
}
