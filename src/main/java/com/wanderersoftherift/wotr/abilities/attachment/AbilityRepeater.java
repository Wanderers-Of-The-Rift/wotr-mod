package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.triggers.TrackableTrigger;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbilityRepeater {

    private final IAttachmentHolder holder;

    private final Set<Holder<TrackableTrigger.TriggerType<?>>> triggersToRepeat = new HashSet<>();

    public AbilityRepeater(IAttachmentHolder holder) {
        this.holder = holder;
    }

    public AbilityRepeater(IAttachmentHolder holder, Data data) {
        this.holder = holder;
        triggersToRepeat.addAll(data.entries);
    }

    public static IAttachmentSerializer<Tag, AbilityRepeater> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, AbilityRepeater::new, AbilityRepeater::data);
    }

    private Data data() {
        return new Data(List.copyOf(triggersToRepeat));
    }

    public void addTrigger(Holder<TrackableTrigger.TriggerType<?>> trigger) {
        var wasEmpty = triggersToRepeat.isEmpty();
        triggersToRepeat.add(trigger);
        if (wasEmpty) {
            ((Player) holder).level().getData(WotrAttachments.ABILITY_REPEATER_REGISTRY).add((Player) holder);
        }
    }

    public void removeTrigger(Holder<TrackableTrigger.TriggerType<?>> trigger) {
        triggersToRepeat.remove(trigger);
        if (triggersToRepeat.isEmpty()) {
            holder.removeData(WotrAttachments.ABILITY_REPEATER);
        }
    }

    public void tick() {
        var entity = (Entity) holder;
        var tracker = TriggerTracker.forEntity(entity);
        triggersToRepeat.removeIf(triggerType -> {
            tracker.trigger(triggerType.value().clientTriggerInstance());
            return !tracker.hasListenersOnTrigger(triggerType);
        });
        triggersToRepeat.forEach(triggerType -> tracker.trigger(triggerType.value().clientTriggerInstance()));
    }

    public boolean isEmpty() {
        return triggersToRepeat.isEmpty();
    }

    private record Data(List<Holder<TrackableTrigger.TriggerType<?>>> entries) {
        private static final Codec<Data> CODEC = WotrRegistries.TRACKABLE_TRIGGERS.holderByNameCodec()
                .listOf()
                .xmap(Data::new, Data::entries);
    }

}
