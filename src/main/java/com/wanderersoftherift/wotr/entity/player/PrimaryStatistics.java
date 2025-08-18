package com.wanderersoftherift.wotr.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.Map;

/**
 * Holds a player's base primary statistics, as selected based on level up - to carry across death
 */
public class PrimaryStatistics {

    private final IAttachmentHolder holder;

    private final Object2IntMap<Holder<PrimaryStatistic>> statistics = new Object2IntOpenHashMap<>();

    public PrimaryStatistics(IAttachmentHolder holder) {
        this(holder, null);
    }

    private PrimaryStatistics(IAttachmentHolder holder, Data data) {
        this.holder = holder;
        if (data != null) {
            statistics.putAll(data.statistics);
        }
    }

    public static IAttachmentSerializer<Tag, PrimaryStatistics> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, PrimaryStatistics::new, x -> new Data(x.statistics));
    }

    public int getStatistic(Holder<PrimaryStatistic> stat) {
        return statistics.getOrDefault(stat, 0);
    }

    public void setStatistic(Holder<PrimaryStatistic> stat, int value) {
        statistics.put(stat, value);
        if (holder instanceof Player player) {
            applyStatistic(player, stat, value);
        }
    }

    public void applyStatistics() {
        if (holder instanceof Player player) {
            statistics.forEach((stat, value) -> applyStatistic(player, stat, value));
        }
    }

    private void applyStatistic(Player player, Holder<PrimaryStatistic> stat, int value) {
        AttributeInstance attribute = player.getAttribute(stat.value().attribute());
        if (attribute != null) {
            attribute.addOrReplacePermanentModifier(new AttributeModifier(WanderersOfTheRift.id("base_stat"), value,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private record Data(Map<Holder<PrimaryStatistic>, Integer> statistics) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(PrimaryStatistic.CODEC, Codec.INT).fieldOf("statistics").forGetter(Data::statistics)
        ).apply(instance, Data::new));
    }
}
