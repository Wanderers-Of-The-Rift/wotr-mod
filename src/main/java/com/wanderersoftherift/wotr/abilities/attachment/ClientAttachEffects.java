package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.source.AbilityModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Data tracking attach effects on the client side (display and modifiers only)
 */
public class ClientAttachEffects {

    private final IAttachmentHolder holder;
    private final Map<UUID, AttachedEffect> attached = new LinkedHashMap<>();
    private final Multiset<Holder<EffectMarker>> markers = LinkedHashMultiset.create();

    public ClientAttachEffects(IAttachmentHolder holder) {
        this.holder = holder;
    }

    public void add(UUID id, Holder<EffectMarker> marker, List<ModifierInstance> modifiers) {
        attached.put(id, new AttachedEffect(id, Optional.ofNullable(marker), modifiers));
        if (marker != null) {
            markers.add(marker);
        }
        if (holder instanceof Entity entity) {
            int index = 0;
            for (ModifierInstance modifier : modifiers) {
                ModifierSource source = new AbilityModifierSource(id, index++);
                modifier.modifier().value().enableModifier(modifier.roll(), entity, source, modifier.tier());
            }
        }
    }

    public void remove(UUID id) {
        AttachedEffect removed = attached.remove(id);
        if (removed == null) {
            return;
        }
        removed.marker.ifPresent(markers::remove);
        if (holder instanceof Entity entity) {
            int index = 0;
            for (ModifierInstance modifier : removed.modifiers) {
                ModifierSource source = new AbilityModifierSource(id, index++);
                modifier.modifier().value().disableModifier(modifier.roll(), entity, source, modifier.tier());
            }
        }
    }

    public List<Holder<EffectMarker>> getMarkers() {
        return List.copyOf(markers.elementSet());
    }

    private record AttachedEffect(UUID id, Optional<Holder<EffectMarker>> marker, List<ModifierInstance> modifiers) {
        private static final Codec<AttachedEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(AttachedEffect::id),
                EffectMarker.CODEC.optionalFieldOf("marker").forGetter(AttachedEffect::marker),
                ModifierInstance.CODEC.listOf().fieldOf("modifiers").forGetter(AttachedEffect::modifiers)
        ).apply(instance, AttachedEffect::new));
    }

}
