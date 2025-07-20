package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.DittoBlockEntityRenderer;
import com.wanderersoftherift.wotr.client.render.blockentity.AnomalyBlockEntityRenderer;
import com.wanderersoftherift.wotr.client.render.blockentity.JigsawBlockEntityRenderer;
import com.wanderersoftherift.wotr.client.render.blockentity.RiftMobSpawnerBlockEntityRenderer;
import com.wanderersoftherift.wotr.client.render.entity.AltSpiderRenderer;
import com.wanderersoftherift.wotr.client.render.entity.RiftPortalRenderer;
import com.wanderersoftherift.wotr.client.render.entity.SimpleEffectProjectileRenderer;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.init.WotrEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WotrEntities.RIFT_ENTRANCE.get(), RiftPortalRenderer::new);
        event.registerEntityRenderer(WotrEntities.RIFT_EXIT.get(), RiftPortalRenderer::new);
        event.registerEntityRenderer(EntityType.SPIDER, (context) -> new AltSpiderRenderer<>(context,
                WanderersOfTheRift.id("textures/entity/alt_spider.png"), 1.0f));
        event.registerEntityRenderer(EntityType.CAVE_SPIDER, (context) -> new AltSpiderRenderer<>(context,
                WanderersOfTheRift.id("textures/entity/alt_cave_spider.png"), 0.75f));
        event.registerBlockEntityRenderer(BlockEntityType.JIGSAW, JigsawBlockEntityRenderer::new);
        event.registerEntityRenderer(WotrEntities.SIMPLE_EFFECT_PROJECTILE.get(), SimpleEffectProjectileRenderer::new);
        event.registerBlockEntityRenderer(WotrBlockEntities.DITTO_BLOCK_ENTITY.get(), DittoBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(WotrBlockEntities.RIFT_MOB_SPAWNER.get(),
                RiftMobSpawnerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(WotrBlockEntities.ANOMALY_BLOCK_ENTITY.get(),
                AnomalyBlockEntityRenderer::new);
    }
}
