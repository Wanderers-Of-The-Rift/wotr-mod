package com.wanderersoftherift.wotr.abilities.attachment;

import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.abilities.effects.attachment.ClientAttachEffect;
import com.wanderersoftherift.wotr.abilities.effects.attachment.MarkerDisplayInfo;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.source.AttachEffectModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.UUID;

/**
 * Data tracking attach effects on the client side
 */
public class ClientAttachEffects {

    private final IAttachmentHolder holder;
    private final Map<UUID, ClientAttachEffect> attached = new LinkedHashMap<>();
    private final List<MarkerDisplayInfo> markers = new ArrayList<>();

    public ClientAttachEffects(IAttachmentHolder holder) {
        this.holder = holder;
    }

    public List<MarkerDisplayInfo> getMarkers() {
        return Collections.unmodifiableList(markers);
    }

    public void add(ClientAttachEffect effect) {
        attached.put(effect.id(), effect);
        effect.marker().ifPresent(this::updateMarker);
        if (holder instanceof Entity entity) {
            int index = 0;
            for (ModifierInstance modifier : effect.modifiers()) {
                ModifierSource source = new AttachEffectModifierSource(effect.id(), index++);
                modifier.modifier().value().enableModifier(modifier.roll(), entity, source, modifier.tier());
            }
        }
    }

    public void remove(UUID id) {
        ClientAttachEffect removed = attached.remove(id);
        if (removed == null) {
            return;
        }
        removed.marker().ifPresent(this::updateMarker);
        if (holder instanceof Entity entity) {
            int index = 0;
            for (ModifierInstance modifier : removed.modifiers()) {
                ModifierSource source = new AttachEffectModifierSource(id, index++);
                modifier.modifier().value().disableModifier(modifier.roll(), entity, source, modifier.tier());
            }
        }
    }

    private void updateMarker(Holder<EffectMarker> marker) {
        markers.removeIf(x -> x.marker().equals(marker));
        OptionalLong maxUntil = attached.values()
                .stream()
                .filter(x -> x.marker().isPresent() && x.marker().get().equals(marker))
                .mapToLong(x -> x.until().orElse(Long.MAX_VALUE))
                .max();
        if (maxUntil.isPresent()) {
            markers.add(new MarkerDisplayInfo(marker, maxUntil.getAsLong()));
        }
    }
}
