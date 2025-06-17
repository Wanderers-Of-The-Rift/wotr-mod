package com.wanderersoftherift.wotr.rift.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.LOGGER;

public class LevelRiftObjectiveData extends SavedData {

    public static final Codec<LevelRiftObjectiveData> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(OngoingObjective.DIRECT_CODEC.fieldOf("objective").forGetter(LevelRiftObjectiveData::getObjective)
            ).apply(inst, LevelRiftObjectiveData::new));

    private OngoingObjective objective;

    public LevelRiftObjectiveData(OngoingObjective objective) {
        this.objective = objective;
    }

    public OngoingObjective getObjective() {
        return objective;
    }

    public void setObjective(OngoingObjective objective) {
        this.objective = objective;
    }

    public static LevelRiftObjectiveData getFromLevel(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new Factory<>(LevelRiftObjectiveData::create, LevelRiftObjectiveData::load),
                        "rift_objective");
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        if (objective != null) {
            OngoingObjective.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.getObjective())
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(compound -> compoundTag.put("objective", compound));
        }
        return compoundTag;
    }

    public static LevelRiftObjectiveData create() {
        return new LevelRiftObjectiveData(null);
    }

    public static LevelRiftObjectiveData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        if (!tag.contains("objective")) {
            return new LevelRiftObjectiveData(null);
        }
        OngoingObjective objective = OngoingObjective.DIRECT_CODEC.parse(NbtOps.INSTANCE, tag.get("objective"))
                .resultOrPartial(LOGGER::error)
                .orElse(null);
        return new LevelRiftObjectiveData(objective);
    }
}
