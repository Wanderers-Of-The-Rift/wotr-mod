package com.wanderersoftherift.wotr.block.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.block.DittoBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DittoBlockEntityRenderer implements BlockEntityRenderer<DittoBlockEntity> {
    private final BlockRenderDispatcher dispatcher;

    public DittoBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(@NotNull DittoBlockEntity blockEntity, float partialTick, PoseStack stack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        DittoBlock dittoBlock = (DittoBlock) blockEntity.getBlockState().getBlock();
        if (!dittoBlock.shouldRender(blockEntity.getBlockState())) {
            return;
        }
        if ((blockEntity.getTheItem().getItem() instanceof BlockItem)
                && blockEntity.getTheItem().getItem() != dittoBlock.getBlock().asItem()) {
            BlockState blockstate = ((BlockItem) blockEntity.getTheItem().getItem()).getBlock().defaultBlockState();
            if (blockEntity.getLevel() == null) {
                return;
            }
            int packedUV = OverlayTexture.pack(OverlayTexture.u(0.15F), 10);
            this.dispatcher.renderSingleBlock(blockstate, stack, bufferSource, packedLight, packedUV);
        } else {
            this.dispatcher.getModelRenderer()
                    .tesselateBlock(blockEntity.getLevel(), dispatcher.getBlockModel(blockEntity.getBlockState()),
                            blockEntity.getBlockState(), blockEntity.getBlockPos(), stack,
                            bufferSource.getBuffer(RenderType.CUTOUT), false, RandomSource.create(),
                            blockEntity.getBlockState().getSeed(blockEntity.getBlockPos()), packedOverlay,
                            ModelData.EMPTY, RenderType.CUTOUT);
        }
    }
}
