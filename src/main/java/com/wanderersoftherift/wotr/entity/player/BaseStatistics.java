package com.wanderersoftherift.wotr.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.Map;

/**
 * Holds a player's base statistics, as selected based on level up - to carry across death
 */
public class BaseStatistics {

    private final IAttachmentHolder holder;

    private final Object2IntMap<Holder<Attribute>> statistics = new Object2IntOpenHashMap<>();

    public BaseStatistics(IAttachmentHolder holder) {
        this(holder, null);
    }

    private BaseStatistics(IAttachmentHolder holder, Data data) {
        this.holder = holder;
        if (data != null) {
            statistics.putAll(data.statistics);
        }
    }

    public static IAttachmentSerializer<Tag, BaseStatistics> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, BaseStatistics::new, x -> new Data(x.statistics));
    }

    public int getStatistic(Holder<Attribute> stat) {
        return statistics.getOrDefault(stat, 0);
    }

    public void setStatistic(Holder<Attribute> stat, int value) {
        statistics.put(stat, value);
        if (holder instanceof Player player) {
            AttributeMap attributes = player.getAttributes();
            attributes.getInstance(stat)
                    .addOrReplacePermanentModifier(new AttributeModifier(WanderersOfTheRift.id("base_stat"), value,
                            AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public void applyStatistics() {
        if (holder instanceof Player player) {
            AttributeMap attributes = player.getAttributes();
            statistics.forEach((attribute, value) -> {
                attributes.getInstance(attribute)
                        .addOrReplacePermanentModifier(new AttributeModifier(WanderersOfTheRift.id("base_stat"), value,
                                AttributeModifier.Operation.ADD_VALUE));
            });
        }
    }

    private record Data(Map<Holder<Attribute>, Integer> statistics) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Attribute.CODEC, Codec.INT).fieldOf("statistics").forGetter(Data::statistics)
        ).apply(instance, Data::new));
    }
}
