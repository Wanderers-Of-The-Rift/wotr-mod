package com.wanderersoftherift.wotr.util;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A level attachment for tracking entities with given attachment types, to enable performant iteration.
 * <p>
 * Attachments that wish to be tracked should use the IAttachmentHolder pattern and add their entities in their
 * constructor:
 * 
 * <pre>
 * {@code
 * if (holder instanceof Entity entity) {
 *     entity.level().getData(WotrAttachments.ENTITY_ATTACHMENT_REGISTRY).add(attachmentType, entity);
 * }
 * }
 * </pre>
 * </p>
 */
public class EntityAttachmentRegistry {

    private final SetMultimap<AttachmentType<?>, Entity> entities = Multimaps.newSetMultimap(new ConcurrentHashMap<>(),
            () -> Collections.newSetFromMap(new ConcurrentHashMap<>()));

    /**
     * Adds an entity to the registry for the given attachment type
     * 
     * @param type   The type of attachment
     * @param entity The entity to add
     * @param <T>    The attachment type
     */
    public <T> void add(Supplier<AttachmentType<T>> type, Entity entity) {
        add(type.get(), entity);
    }

    /**
     * Adds an entity to the registry for the given attachment type
     *
     * @param type   The type of attachment
     * @param entity The entity to add
     */
    public void add(AttachmentType<?> type, Entity entity) {
        if (!entity.isRemoved()) {
            entities.put(type, entity);
        }
    }

    /**
     * Iterates over all entities with the given attachment type
     * 
     * @param type     The attachment type
     * @param consumer
     * @param <T>
     */
    public <T> void forEach(Supplier<AttachmentType<T>> type, BiConsumer<Entity, T> consumer) {
        forEach(type.get(), consumer);
    }

    /**
     * Iterates over all entities with the given attachment type
     * 
     * @param type
     * @param consumer
     * @param <T>
     */
    public <T> void forEach(AttachmentType<T> type, BiConsumer<Entity, T> consumer) {
        Iterator<Entity> iterator = entities.get(type).iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (entity.isRemoved() || !entity.hasData(type)) {
                iterator.remove();
            } else {
                T data = entity.getData(type);
                consumer.accept(entity, data);
            }
        }
    }

}
