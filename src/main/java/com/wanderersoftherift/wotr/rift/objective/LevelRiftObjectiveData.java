package com.wanderersoftherift.wotr.rift.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.LOGGER;


public class LevelRiftObjectiveData extends SavedData {

    public static Codec<LevelRiftObjectiveData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            AbstractObjective.DIRECT_CODEC.fieldOf("objective").forGetter(LevelRiftObjectiveData::getObjective)
    ).apply(inst, LevelRiftObjectiveData::new));

    private AbstractObjective objective;

    public LevelRiftObjectiveData(AbstractObjective objective) {
        this.objective = objective;
    }

    public AbstractObjective getObjective() {
        return objective;
    }

    public void setObjective(AbstractObjective objective) {
        this.objective = objective;
    }

    public static LevelRiftObjectiveData getFromLevel(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(LevelRiftObjectiveData::create, LevelRiftObjectiveData::load), "rift_objective");
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        if (objective != null) {
            AbstractObjective.DIRECT_CODEC
                    .encodeStart(NbtOps.INSTANCE, this.getObjective())
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(compound -> compoundTag.put("objective", compound));
        }
        return compoundTag;
    }

    public static LevelRiftObjectiveData create() {
        return new LevelRiftObjectiveData(null);
    }

    public static LevelRiftObjectiveData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        AbstractObjective objective = AbstractObjective.DIRECT_CODEC
                .parse(NbtOps.INSTANCE, tag.get("objective"))
                .resultOrPartial(LOGGER::error)
                .orElse(null);
        return new LevelRiftObjectiveData(objective);
    }
}
