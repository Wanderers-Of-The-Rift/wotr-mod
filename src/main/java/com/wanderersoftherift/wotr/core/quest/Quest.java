package com.wanderersoftherift.wotr.core.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;
import java.util.Optional;

/**
 * Defines a quest that a player can undertake.
 *
 * @param icon    An icon to display with the quest
 * @param goals   A list of goal providers that will generate the goals that must be completed to complete the quest
 * @param rewards A list of reward providers that will generate the rewards for completing the quest
 */
public record Quest(Optional<ResourceLocation> icon, List<GoalProvider> goals, List<RewardProvider> rewards,
        Optional<EntitySubPredicate> prerequisite) {

    public static final Codec<Quest> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(Quest::icon),
            GoalProvider.DIRECT_CODEC.listOf().optionalFieldOf("goals", List.of()).forGetter(Quest::goals),
            RewardProvider.DIRECT_CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(Quest::rewards),
            EntitySubPredicate.CODEC.optionalFieldOf("prerequisite").forGetter(Quest::prerequisite)
    ).apply(instance, Quest::new));

    public static final Codec<Holder<Quest>> CODEC = RegistryFixedCodec.create(WotrRegistries.Keys.QUESTS);
    public static final Codec<HolderSet<Quest>> SET_CODEC = HolderSetCodec.create(WotrRegistries.Keys.QUESTS, CODEC,
            false);

    public List<Goal> generateGoals(LootParams params) {
        return goals.stream().flatMap(x -> x.generateGoal(params).stream()).toList();
    }

    public List<Reward> generateRewards(LootParams params) {
        return rewards.stream().flatMap(x -> x.generateReward(params).stream()).toList();
    }

    /**
     * @param quest
     * @return The title for the quest
     */
    public static Component title(Holder<Quest> quest) {
        ResourceLocation loc = quest.getKey().location();
        return Component.translatable("quest." + loc.getNamespace() + "." + loc.getPath() + ".title");
    }

    /**
     * @param quest
     * @return The description of the quest
     */
    public static Component description(Holder<Quest> quest) {
        ResourceLocation loc = quest.getKey().location();
        return Component.translatable("quest." + loc.getNamespace() + "." + loc.getPath() + ".description");
    }

    /**
     * @param entity
     * @param level
     * @return Whether this quest should be available to the given entity
     */
    public boolean isAvailable(Entity entity, ServerLevel level) {
        return prerequisite.map(x -> x.matches(entity, level, entity.position())).orElse(true);
    }
}
