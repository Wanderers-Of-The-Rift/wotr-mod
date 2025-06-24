package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.config.ClientConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public class MixinJigsawTemplatePoolPlacer {
    @Redirect(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;get(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;"))
    private Optional<? extends Holder<StructureTemplatePool>> redirectGetPool(
            Registry<StructureTemplatePool> registry,
            ResourceKey<StructureTemplatePool> key) {
        String loc = key.location().toString();
        // If the location is "wotr:rift/poi/free/3", we will randomly select between the objective and shrine pools.
        // Only free/3 is used for shrines and objectives (now).
        if ("wotr:rift/poi/free/3".equals(loc)) {
            double rand = Math.random();
            double objectiveWeight = ClientConfig.POOL_OBJECTIVE_WEIGHT.get();
            double shrineWeight = ClientConfig.POOL_SHRINE_WEIGHT.get();
            if (rand < objectiveWeight) { // If the random number is less than the objective weight, use the objective
                                          // pool
                key = ResourceKey.create(registry.key(), ResourceLocation.parse("wotr:rift/poi/free/objective"));
            } else if (rand < objectiveWeight + shrineWeight) { // If the random number is less than the sum of
                                                                // objective and shrine weights, use the shrine pool
                key = ResourceKey.create(registry.key(), ResourceLocation.parse("wotr:rift/poi/free/shrine"));
            }
        }
        return registry.get(key);
    }
}