package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerDurationData {

    public static final Codec<PlayerDurationData> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
            .xmap(PlayerDurationData::new, x -> x.map);

    private final Map<ResourceLocation, Integer> map = new LinkedHashMap<>();

    public PlayerDurationData() {
    }

    public PlayerDurationData(Map<ResourceLocation, Integer> data) {
        map.putAll(data);
    }

    public void beginDuration(ResourceLocation loc, int amount) {
        this.insert(loc, amount);
    }

    public int getDuration(ResourceLocation loc) {
        return this.get(loc);
    }

    public boolean isDurationRunning(ResourceLocation loc) {
        return this.containsKey(loc) && this.get(loc) > 0;
    }

    public void reduceDurations() {
        reduceAll(1);
    }

    public List<AbstractAbility> getRunningDurations(Registry<AbstractAbility> abilityRegistry) {
        List<AbstractAbility> durationActive = new ArrayList<>();
        for (ResourceLocation loc : this.getKeys()) {
            if (abilityRegistry.get(loc).isPresent()) {
                durationActive.add(abilityRegistry.get(loc).get().value());
            }
        }

        return durationActive;
    }

    public void insert(ResourceLocation loc, int value) {
        map.put(loc, value);
    }

    public void set(ResourceLocation loc, int value) {
        map.replace(loc, value);
    }

    public int get(ResourceLocation loc) {
        return map.getOrDefault(loc, 0);
    }

    public void remove(ResourceLocation loc) {
        map.remove(loc);
    }

    public Set<ResourceLocation> getKeys() {
        return map.keySet();
    }

    public Collection<Integer> getValues() {
        return map.values();
    }

    public boolean containsKey(ResourceLocation loc) {
        return map.containsKey(loc);
    }

    public void reduceAll(int amount) {
        List<ResourceLocation> toRemove = new ArrayList<>();
        for (ResourceLocation loc : this.getKeys()) {
            this.set(loc, Math.max(this.get(loc) - amount, 0));
            if (this.get(loc) == 0) {
                toRemove.add(loc);
            }
        }

        toRemove.forEach(this::remove);
    }
}
