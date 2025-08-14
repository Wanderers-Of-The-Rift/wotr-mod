package com.wanderersoftherift.wotr.abilities.attachment;

import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.abilities.effects.attachment.EffectMarkerInstance;
import com.wanderersoftherift.wotr.abilities.effects.attachment.MarkerDisplayInfo;
import net.minecraft.core.Holder;

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
public class EffectMarkers {

    private final Map<UUID, EffectMarkerInstance> attached = new LinkedHashMap<>();
    private final List<MarkerDisplayInfo> markers = new ArrayList<>();

    public List<MarkerDisplayInfo> getMarkers() {
        return Collections.unmodifiableList(markers);
    }

    public void add(EffectMarkerInstance effect) {
        attached.put(effect.id(), effect);
        effect.marker().ifPresent(this::updateMarker);
    }

    public void remove(UUID id) {
        EffectMarkerInstance removed = attached.remove(id);
        if (removed == null) {
            return;
        }
        removed.marker().ifPresent(this::updateMarker);
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
