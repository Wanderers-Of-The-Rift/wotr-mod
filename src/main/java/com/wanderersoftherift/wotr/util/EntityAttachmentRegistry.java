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

public class EntityAttachmentRegistry {

    private final SetMultimap<AttachmentType<?>, Entity> entities = Multimaps.newSetMultimap(new ConcurrentHashMap<>(),
            () -> Collections.newSetFromMap(new ConcurrentHashMap<>()));

    public <T> void add(Supplier<AttachmentType<T>> type, Entity entity) {
        add(type.get(), entity);
    }

    public void add(AttachmentType<?> type, Entity entity) {
        if (!entity.isRemoved()) {
            entities.put(type, entity);
        }
    }

    public <T> void forEach(Supplier<AttachmentType<T>> type, BiConsumer<Entity, T> consumer) {
        forEach(type.get(), consumer);
    }

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
