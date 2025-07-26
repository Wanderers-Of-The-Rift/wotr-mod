package com.wanderersoftherift.wotr.core.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
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
public record Quest(Optional<ResourceLocation> icon, List<GoalProvider> goals, List<RewardProvider> rewards) {

    public static final Codec<Quest> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(Quest::icon),
            GoalProvider.DIRECT_CODEC.listOf().optionalFieldOf("goals", List.of()).forGetter(Quest::goals),
            RewardProvider.DIRECT_CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(Quest::rewards)
    ).apply(instance, Quest::new));

    public static final Codec<Holder<Quest>> CODEC = RegistryFixedCodec.create(WotrRegistries.Keys.QUESTS);

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
        ResourceLocation loc = ResourceLocation.parse(quest.getRegisteredName());
        return Component.translatable("quest." + loc.getNamespace() + "." + loc.getPath() + ".title");
    }

    /**
     * @param quest
     * @return The description of the quest
     */
    public static Component description(Holder<Quest> quest) {
        ResourceLocation loc = ResourceLocation.parse(quest.getRegisteredName());
        return Component.translatable("quest." + loc.getNamespace() + "." + loc.getPath() + ".description");
    }

}
