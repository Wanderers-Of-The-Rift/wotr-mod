package com.wanderersoftherift.wotr.core.guild;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.reward.RewardMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.guild.UnclaimedGuildRewardsReplicationPayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import com.wanderersoftherift.wotr.serialization.GuavaCodecs;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Attachment for tracking guild rank up rewards that are available to be claimed
 */
public class UnclaimedGuildRewards {
    private final IAttachmentHolder holder;
    private final ListMultimap<Holder<Guild>, Integer> unclaimedRewards = ArrayListMultimap.create();

    public UnclaimedGuildRewards(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private UnclaimedGuildRewards(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        if (data != null) {
            unclaimedRewards.putAll(data.unclaimedRewards);
        }
    }

    /**
     * @param guild
     * @return Whether there are rewards available for the given guild
     */
    public boolean hasRewards(Holder<Guild> guild) {
        return unclaimedRewards.containsKey(guild);
    }

    public void addReward(Holder<Guild> guild, int rank) {
        if (guild.value().getRank(rank).rewards().isEmpty()) {
            return;
        }
        unclaimedRewards.put(guild, rank);
        replicate();
    }

    /**
     * If the player has unclaimed rewards for the guild, generates them and opens up a reward menu for claiming them
     * 
     * @param guild
     */
    public void claimRewards(Holder<Guild> guild) {
        List<Integer> ranks = unclaimedRewards.removeAll(guild);
        if (ranks.isEmpty() || !(holder instanceof Player player) || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        LootContext context = new LootContext.Builder(params).create(Optional.empty());
        List<Reward> rewards = ranks.stream()
                .flatMap(rank -> guild.value().getRank(rank).rewards().stream())
                .flatMap(provider -> provider.generateReward(context).stream())
                .toList();
        if (!rewards.isEmpty()) {
            RewardMenu.openRewardMenu(player, rewards,
                    Component.translatable(WanderersOfTheRift.translationId("container", "guild_rank_up")));
        }
        replicate();
        if (unclaimedRewards.isEmpty()) {
            holder.removeData(WotrAttachments.UNCLAIMED_GUILD_REWARDS);
        }
    }

    /**
     * Replicates to the client player
     */
    public void replicate() {
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new UnclaimedGuildRewardsReplicationPayload(unclaimedRewards));
        }
    }

    /**
     * Sets all data (intended for replication updates)
     * 
     * @param newValues
     */
    public void setAll(ListMultimap<Holder<Guild>, Integer> newValues) {
        unclaimedRewards.clear();
        unclaimedRewards.putAll(newValues);
    }

    public static IAttachmentSerializer<Tag, UnclaimedGuildRewards> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, UnclaimedGuildRewards::new,
                UnclaimedGuildRewards::getData);
    }

    private Data getData() {
        return new Data(unclaimedRewards);
    }

    private record Data(ListMultimap<Holder<Guild>, Integer> unclaimedRewards) {
        private static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                GuavaCodecs.stringKeyListMultimap(Guild.CODEC, Codec.INT)
                        .fieldOf("unclaimed_rewards")
                        .forGetter(Data::unclaimedRewards)
        ).apply(instance, Data::new));
    }
}
