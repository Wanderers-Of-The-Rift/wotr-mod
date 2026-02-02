package com.wanderersoftherift.wotr.core.goal.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalManager;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.Optional;

/**
 * A goal to kill mobs
 * 
 * @param mob      A predicate for the type of mobs that must be killed
 * @param rawLabel A translation string for displaying the type of mob
 * @param count    The number of mobs that need to be killed
 */
@EventBusSubscriber
public record KillMobGoal(Optional<EntityTypePredicate> mob, String rawLabel, int count) implements Goal {

    public static final MapCodec<KillMobGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    EntityTypePredicate.CODEC.optionalFieldOf("mob").forGetter(KillMobGoal::mob),
                    Codec.STRING.optionalFieldOf("mob_label", WanderersOfTheRift.translationId("goal", "mobs"))
                            .forGetter(KillMobGoal::rawLabel),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(KillMobGoal::count)
            ).apply(instance, KillMobGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KillMobGoal> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, KillMobGoal::rawLabel, ByteBufCodecs.INT, KillMobGoal::count,
            (label, quantity) -> new KillMobGoal(null, label, quantity)
    );

    public static final DualCodec<KillMobGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<KillMobGoal> getType() {
        return TYPE;
    }

    /**
     * @return A component for displaying the name of the mob classification
     */
    public Component mobLabel() {
        return Component.translatable(rawLabel);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        GoalManager.getGoalStates(player, KillMobGoal.class).forEach(state -> {
            if (state.getGoal().mob().map(predicate -> predicate.matches(event.getEntity().getType())).orElse(true)) {
                state.incrementProgress(player);
            }
        });
    }

}
