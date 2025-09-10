package com.wanderersoftherift.wotr.util;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A level attachment for tracking entities with an attachment types, to enable performant iteration.
 * <p>
 * An instance of this should be registered as an attachment type for each attachment type that should have entities
 * tracked. Attachments that wish to be tracked should use the IAttachmentHolder pattern and add their entities in their
 * constructor:
 * 
 * <pre>
 * {@code
 * if (holder instanceof Entity entity) {
 *     entity.level().getData(ATTACHMENT_REGISTRY).add(entity);
 * }
 * }
 * </pre>
 * </p>
 */
public class EntityAttachmentRegistry<T> {

    private final Supplier<AttachmentType<T>> type;
    private final Set<Entity> entities = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public EntityAttachmentRegistry(Supplier<AttachmentType<T>> type) {
        this.type = type;
    }

    /**
     * Adds an entity to the registry for the given attachment type
     *
     * @param entity The entity to add
     */
    public void add(Entity entity) {
        if (!entity.isRemoved()) {
            entities.add(entity);
        }
    }

    /**
     * Iterates over all entities with the given attachment type
     * 
     * @param consumer
     */
    public void forEach(BiConsumer<Entity, T> consumer) {
        Iterator<Entity> iterator = entities.iterator();
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
