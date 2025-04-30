package com.wanderersoftherift.wotr.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import org.joml.Matrix4f;

import static com.wanderersoftherift.wotr.init.client.ClientRegistryEvents.JIGSAW_NAME_TOGGLE_KEY;

public class JigsawBlockEntityRenderer implements BlockEntityRenderer<JigsawBlockEntity> {

    public JigsawBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            JigsawBlockEntity jigsawBlockEntity,
            float v,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            int i1) {
        if (!JIGSAW_NAME_TOGGLE_KEY.isDown()) {
            return;
        }
        MutableComponent name = Component.literal(jigsawBlockEntity.getPool().location().toString());
        renderNameTag(name, poseStack, multiBufferSource, i);
    }

    private void renderNameTag(
            Component displayName,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.5, 2.5, 0.5);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        poseStack.scale(0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        Font font = Minecraft.getInstance().font;
        float f = (float) (-font.width(displayName)) / 2.0F;
        int j = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        font.drawInBatch(displayName, f, (float) 0, 0xFFFFFF, false, matrix4f, bufferSource,
                Font.DisplayMode.SEE_THROUGH, j, packedLight);

        poseStack.popPose();
    }
}
