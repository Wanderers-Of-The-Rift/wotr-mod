package com.wanderersoftherift.wotr.client.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class BannedRiftList {

    private static final Set<ResourceLocation> bannedRifts = new LinkedHashSet<>();

    private BannedRiftList() {
    }

    @SubscribeEvent
    public static void onStart(ClientPlayerNetworkEvent.LoggingIn event) {
        clear();
    }

    public static void clear() {
        bannedRifts.clear();
    }

    public static boolean isBannedFrom(ResourceLocation location) {
        return bannedRifts.contains(location);
    }

    public static void addBannedRifts(List<ResourceLocation> locations) {
        bannedRifts.addAll(locations);
    }
}
