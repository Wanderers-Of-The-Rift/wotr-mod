package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftLevel;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Shadow public abstract Map<ResourceKey<Level>, ServerLevel> forgeGetWorldMap();


    // This will be injected when overworld is already loaded, but nothing else is
    // It will split the dimension set into 2 - rifts and nonRifts
    // It will then create RiftLevel for each rift and load it
    // nonRifts will be passed back for normal loading
    @WrapOperation(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;entrySet()Ljava/util/Set;"))
    private Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>> createRift(Registry<LevelStem> instance, Operation<Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>>> original) {
        var originalSet = original.call(instance);
        var rifts = originalSet.stream().filter(e -> e.getKey().location().getNamespace().equals("wotr")).toList();
        var nonRifts = originalSet.stream().filter(e -> !e.getKey().location().getNamespace().equals("wotr")).collect(Collectors.toSet());
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> rift : rifts) {
            var level = RiftLevel.create(rift.getKey().location(), rift.getValue(), null, null);
            this.forgeGetWorldMap().put(ResourceKey.create(Registries.DIMENSION, rift.getKey().location()), level);
            RiftLevelManager.addActiveRift(level);
            NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
            DimensionDelvers.LOGGER.debug("Loaded rift level {}", rift.getKey().location());
        }
        return nonRifts;
    }

}
