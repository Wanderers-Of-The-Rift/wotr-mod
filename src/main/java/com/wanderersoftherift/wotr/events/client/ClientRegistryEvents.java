package com.wanderersoftherift.wotr.events.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.render.blockentity.JigsawBlockEntityRenderer;
import com.wanderersoftherift.wotr.client.render.item.properties.select.SelectRuneGemShape;
import com.wanderersoftherift.wotr.client.tooltip.ImageTooltipRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistryEvents {

    @SubscribeEvent
    public static void registerSelectItemModelProperties(RegisterSelectItemModelPropertyEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "runegem_shape"), SelectRuneGemShape.TYPE);
    }

    @SubscribeEvent
    public static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ImageTooltipRenderer.ImageComponent.class, ImageTooltipRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                BlockEntityType.JIGSAW,
                JigsawBlockEntityRenderer::new
        );
    }
}
