package com.wanderersoftherift.wotr.abilities.effects.marker;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.Holder;

import java.util.Map;

/**
 * Data tracking the display markers for effects. Used as an Attachment
 */
public class EffectDisplayData {
    private final Object2IntArrayMap<Holder<EffectMarker>> markers = new Object2IntArrayMap<>();

    public EffectDisplayData() {
    }

    public EffectDisplayData(Map<Holder<EffectMarker>, Integer> effects) {
        markers.putAll(effects);
    }

    public void setMarker(Holder<EffectMarker> marker, int duration) {
        markers.put(marker, duration);
    }

    public void removeMarker(Holder<EffectMarker> marker) {
        markers.removeInt(marker);
    }

    public ObjectIterator<Object2IntMap.Entry<Holder<EffectMarker>>> iterate() {
        return markers.object2IntEntrySet().fastIterator();
    }

    public int size() {
        return markers.size();
    }

    public void tick() {
        var iterator = markers.object2IntEntrySet().fastIterator();
        while (iterator.hasNext()) {
            Object2IntMap.Entry<Holder<EffectMarker>> entry = iterator.next();
            if (entry.getIntValue() != Integer.MAX_VALUE) {
                int newValue = entry.getIntValue() - 1;
                if (newValue > 0) {
                    entry.setValue(newValue);
                } else {
                    iterator.remove();
                }
            }
        }
    }

}
