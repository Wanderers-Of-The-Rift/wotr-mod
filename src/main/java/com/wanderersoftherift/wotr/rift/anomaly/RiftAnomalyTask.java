package com.wanderersoftherift.wotr.rift.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import oshi.util.tuples.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record RiftAnomalyTask(String type, // Type of the task, battle, bundle or needle. Only required component, rest
                                           // is optional
        int weight, // Weight of the task, used for random selection. Default is 1
        Optional<EffectData> effect, // Optional effect to apply
        float lootModifier, // Optional loot modifier, base loot is rift completion table, default is 0.2f
        int bundleMin, // Optional minimum number of items in a bundle, default is 3
        int bundleMax, // Optional maximum number of items in a bundle, default is 5
        float battleScale, // Optional scale for mobs spawned, default is 1.0f
        Optional<List<String>> selectableThemes // Optional list of themes that can be selected for the task, default is
                                                // empty = all themes available

) {

    public static final Codec<EffectData> EFFECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("id").forGetter(e -> e.id), Codec.INT.fieldOf("duration").forGetter(e -> e.duration),
            Codec.INT.fieldOf("amplifier").forGetter(e -> e.amplifier)
    ).apply(inst, EffectData::new));

    public static final Codec<RiftAnomalyTask> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("type").forGetter(RiftAnomalyTask::type),
            Codec.INT.optionalFieldOf("weight", 1).forGetter(RiftAnomalyTask::weight),
            EFFECT_CODEC.optionalFieldOf("effect").forGetter(RiftAnomalyTask::effect),
            Codec.FLOAT.optionalFieldOf("loot_modifier", 0.2f).forGetter(RiftAnomalyTask::lootModifier),
            Codec.INT.optionalFieldOf("bundle_min", 3).forGetter(RiftAnomalyTask::bundleMin),
            Codec.INT.optionalFieldOf("bundle_max", 5).forGetter(RiftAnomalyTask::bundleMax),
            Codec.FLOAT.optionalFieldOf("battle_scale", 1.0f).forGetter(RiftAnomalyTask::battleScale),
            Codec.STRING.listOf().optionalFieldOf("selectable_themes").forGetter(RiftAnomalyTask::selectableThemes)
    ).apply(inst, RiftAnomalyTask::new));

    public record EffectData(String id, int duration, int amplifier) {
    }

    public static FastWeightedList<RiftAnomalyTask> buildWeightedList(Collection<RiftAnomalyTask> tasks) {
        return FastWeightedList.of(
                tasks.stream().map(task -> new Pair<>((float) task.weight(), task)).toArray(Pair[]::new)
        );
    }
}
