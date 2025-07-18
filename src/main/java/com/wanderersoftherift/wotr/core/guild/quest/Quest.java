package com.wanderersoftherift.wotr.core.guild.quest;

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
 * <p>
 * A quest is composed of
 * <ul>
 * <li>Display information</li>
 * <li>A list of goals that need to be completed before the quest is complete</li>
 * <li>A list of rewards that are provided upon completion</li>
 * </ul>
 * </p>
 */
public final class Quest {
    public static final Codec<Quest> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(Quest::icon),
            GoalProvider.DIRECT_CODEC.listOf().optionalFieldOf("goals", List.of()).forGetter(Quest::goals),
            RewardProvider.DIRECT_CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(Quest::rewards)
    ).apply(instance, Quest::new));

    public static final Codec<Holder<Quest>> CODEC = RegistryFixedCodec.create(WotrRegistries.Keys.QUESTS);

    private final Optional<ResourceLocation> icon;

    private final List<GoalProvider> goals;
    private final List<RewardProvider> rewards;

    public Quest(Optional<ResourceLocation> icon, List<GoalProvider> goals, List<RewardProvider> rewards) {
        this.icon = icon;
        this.goals = goals;
        this.rewards = rewards;
    }

    public List<Goal> generateGoals(LootParams params) {
        return goals.stream().flatMap(x -> x.generateGoal(params).stream()).toList();
    }

    public List<Reward> generateRewards(LootParams params) {
        return rewards.stream().flatMap(x -> x.generateReward(params).stream()).toList();
    }

    /**
     * @return Display icon for the quest
     */
    public Optional<ResourceLocation> icon() {
        return icon;
    }

    /**
     * @return The goals for the quest
     */
    public List<GoalProvider> goals() {
        return goals;
    }

    /**
     * @return The rewards for completing the quest
     */
    public List<RewardProvider> rewards() {
        return rewards;
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
