package com.wanderersoftherift.wotr.abilities.effects.marker;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.Holder;

import java.util.Map;

/**
 * Data tracking the display markers for effects. Used as an Attachment
 */
public class EffectDisplayData {
    private final Object2FloatArrayMap<Holder<EffectMarker>> markers = new Object2FloatArrayMap<>();

    public EffectDisplayData() {
    }

    public EffectDisplayData(Map<Holder<EffectMarker>, Float> effects) {
        markers.putAll(effects);
    }

    public void setMarker(Holder<EffectMarker> marker, float duration) {
        markers.put(marker, duration);
    }

    public void removeMarker(Holder<EffectMarker> marker) {
        markers.removeFloat(marker);
    }

    public ObjectIterator<Object2FloatMap.Entry<Holder<EffectMarker>>> iterate() {
        return markers.object2FloatEntrySet().fastIterator();
    }

    public void tick(float delta) {
        var iterator = markers.object2FloatEntrySet().fastIterator();
        while (iterator.hasNext()) {
            Object2FloatMap.Entry<Holder<EffectMarker>> entry = iterator.next();
            if (Float.isFinite(entry.getFloatValue())) {
                float newDuration = entry.getFloatValue() - delta;
                if (newDuration > 0) {
                    entry.setValue(newDuration);
                } else {
                    iterator.remove();
                }
            }
        }
    }

}
