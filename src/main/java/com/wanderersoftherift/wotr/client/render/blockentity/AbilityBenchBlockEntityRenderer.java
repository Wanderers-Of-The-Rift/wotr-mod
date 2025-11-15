package com.wanderersoftherift.wotr.client.render.blockentity;

import com.wanderersoftherift.wotr.block.blockentity.AbilityBenchBlockEntity;
import com.wanderersoftherift.wotr.client.model.geo.block.AbilityBenchBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AbilityBenchBlockEntityRenderer extends GeoBlockRenderer<AbilityBenchBlockEntity> {

    public AbilityBenchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new AbilityBenchBlockModel());
    }

    @Override
    public @Nullable RenderType getRenderType(
            AbilityBenchBlockEntity animatable,
            ResourceLocation texture,
            @Nullable MultiBufferSource bufferSource,
            float partialTick) {
        return RenderType.entityCutout(texture);
    }
}
