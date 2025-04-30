package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.StandardAbility;
import com.wanderersoftherift.wotr.abilities.effects.HealEffect;
import com.wanderersoftherift.wotr.abilities.effects.SoundEffect;
import com.wanderersoftherift.wotr.abilities.effects.predicate.EntitySentiment;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetPredicate;
import com.wanderersoftherift.wotr.abilities.targeting.CubeAreaTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.SelfTargeting;
import com.wanderersoftherift.wotr.datagen.provider.AbilityProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAbilityProvider extends AbilityProvider {
    public ModAbilityProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AbstractAbility> writer) {
        writer.accept(
                new StandardAbility(WanderersOfTheRift.id("heal"),
                        ResourceLocation.parse("minecraft:textures/mob_effect/regeneration.png"), 200, 10,
                        List.of(new HealEffect(new CubeAreaTargeting(
                                new TargetPredicate.Builder().withSentiment(EntitySentiment.NOT_FOE).build(), 2, true),
                                Collections.emptyList(), Optional.empty(), 2),
                                new SoundEffect(new SelfTargeting(new TargetPredicate.Builder().build()),
                                        Collections.emptyList(), Optional.empty(),
                                        Holder.direct(SoundEvents.SPLASH_POTION_BREAK)))));
    }
}
