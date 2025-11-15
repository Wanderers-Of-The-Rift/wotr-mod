package com.wanderersoftherift.wotr.item.block;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * Basic block item implementation with geckolib model rendering
 */
public abstract class DefaultGeoBlockItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final ResourceLocation model;

    public DefaultGeoBlockItem(Block block, Properties properties, ResourceLocation model) {
        super(block, properties);
        this.model = model;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<DefaultGeoBlockItem> renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new GeoItemRenderer<>(
                            new DefaultedBlockGeoModel<>(model)) {
                        @Override
                        public RenderType getRenderType(
                                DefaultGeoBlockItem animatable,
                                ResourceLocation texture,
                                @Nullable MultiBufferSource bufferSource,
                                float partialTick) {
                            return RenderType.entityCutout(texture);
                        }
                    };
                }
                return this.renderer;
            }
        });
    }
}
