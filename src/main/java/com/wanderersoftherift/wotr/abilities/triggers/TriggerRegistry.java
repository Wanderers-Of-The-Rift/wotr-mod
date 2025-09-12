package com.wanderersoftherift.wotr.abilities.triggers;

import com.wanderersoftherift.wotr.abilities.TrackableTrigger;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Just a copy of {@link com.wanderersoftherift.wotr.util.EntityAttachmentRegistry}
 *
 */
public class TriggerRegistry<T extends TrackableTrigger> {

    private final DeferredHolder<TrackableTrigger.TriggerType<?>, TrackableTrigger.TriggerType<T>> type;
    private final Set<Entity> entities = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public TriggerRegistry(DeferredHolder<TrackableTrigger.TriggerType<?>, TrackableTrigger.TriggerType<T>> type) {
        this.type = type;
    }

    /**
     * Adds an entity to the registry for abilities for the given trigger type
     *
     * @param entity The entity to add
     */
    public void add(Entity entity) {
        if (!entity.isRemoved()) {
            entities.add(entity);
        }
    }

    /**
     * Iterates over all entities with abilities for the given trigger type
     *
     * @param consumer
     */
    public void forEach(BiConsumer<Entity, TriggerTracker> consumer) {
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            var tracker = TriggerTracker.forEntityNullable(entity);
            if (entity.isRemoved() || tracker.isEmpty()) {
                iterator.remove();
                continue;
            }
            var value = tracker.get();
            if (!value.hasAbilitiesOnTrigger(type.getDelegate())) {
                iterator.remove();
            } else {
                consumer.accept(entity, value);
            }

        }
    }

}
