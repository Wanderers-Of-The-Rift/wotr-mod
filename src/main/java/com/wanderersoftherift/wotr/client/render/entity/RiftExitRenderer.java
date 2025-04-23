package com.wanderersoftherift.wotr.client.render.entity;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.ModShaders;
import com.wanderersoftherift.wotr.entity.portal.RiftPortalExitEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class RiftExitRenderer extends EntityRenderer<RiftPortalExitEntity, RiftExitRenderer.RiftRendererEntityState> {

    private static final ResourceLocation OUTER_RIFT_LOCATION = WanderersOfTheRift.id("textures/entity/outer_rift.png");
    private static final ResourceLocation INNER_RIFT_LOCATION = WanderersOfTheRift.id("textures/entity/inner_rift.png");
    private static final RenderType RENDER_TYPE = RiftPortalRenderType.riftPortal(OUTER_RIFT_LOCATION,
            INNER_RIFT_LOCATION);

    public RiftExitRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RiftRendererEntityState state, PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
            int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0, 0.5f * state.boundingBoxHeight, 0);
        poseStack.scale(state.boundingBoxHeight, state.boundingBoxHeight, state.boundingBoxHeight);

        // Face camera while remaining vertical
        Vector3f dir;
        if (state.billboard) {
            Vector3f cameraPos = this.entityRenderDispatcher.camera.getPosition().toVector3f();
            dir = new Vector3f((float) state.x, (float) state.y, (float) state.z).sub(cameraPos);
            dir.y = 0;
            if (dir.lengthSquared() < 0.001f) {
                dir = new Vector3f(1, 0, 0);
            }
        } else {
            dir = state.facingDir.getUnitVec3().toVector3f();
        }

        CompiledShaderProgram shader = RenderSystem.setShader(ModShaders.RIFT_PORTAL);
        if (shader != null) {
            Uniform screenSize = shader.getUniform("ScreenSize");
            if (screenSize != null) {
                screenSize.set((float) Minecraft.getInstance().getWindow().getWidth(),
                        (float) Minecraft.getInstance().getWindow().getHeight());
            }

            Uniform view = shader.getUniform("View");
            if (view != null) {
                float x = this.entityRenderDispatcher.camera.getXRot();
                float y = Mth.wrapDegrees(this.entityRenderDispatcher.camera.getYRot());
                view.set(x, y);
            }

            shader.apply();
        }

        poseStack.mulPose(new Quaternionf().lookAlong(dir, new Vector3f(0, -1, 0)));

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        vertex(vertexconsumer, pose, packedLight, -0.5f, -0.5f, 0.0f, 0, 1);
        vertex(vertexconsumer, pose, packedLight, 0.5f, -0.5f, 0.0f, 1, 1);
        vertex(vertexconsumer, pose, packedLight, 0.5f, 0.5f, 0.0f, 1, 0);
        vertex(vertexconsumer, pose, packedLight, -0.5f, 0.5f, 0.0f, 0, 0);
        poseStack.popPose();
        super.render(state, poseStack, bufferSource, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float z,
            int u, int v) {
        consumer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public @NotNull RiftRendererEntityState createRenderState() {
        return new RiftRendererEntityState();
    }

    public void extractRenderState(@NotNull RiftPortalExitEntity entity, @NotNull RiftRendererEntityState state,
            float delta) {
        super.extractRenderState(entity, state, delta);
        state.facingDir = entity.getDirection();
        state.billboard = entity.isBillboard();
    }

    public static class RiftRendererEntityState extends EntityRenderState {
        public boolean billboard;
        public Direction facingDir;
    }
}
