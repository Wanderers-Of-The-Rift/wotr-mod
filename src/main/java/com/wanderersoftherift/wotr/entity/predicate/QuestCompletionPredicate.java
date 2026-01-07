package com.wanderersoftherift.wotr.entity.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Predicate requiring the entity is in a specific range of quest completions for a quest. By default, if min and max
 * are not specified, it requires the specified quest to have been completed at least once.
 * 
 * @param quest
 * @param min   The minimum count (inclusive)
 * @param max   The maximum count (inclusive)
 */
public record QuestCompletionPredicate(Holder<Quest> quest, int min, int max) implements EntitySubPredicate {

    public static final MapCodec<QuestCompletionPredicate> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Quest.CODEC.fieldOf("quest").forGetter(QuestCompletionPredicate::quest),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min", 1).forGetter(QuestCompletionPredicate::min),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max", Integer.MAX_VALUE)
                            .forGetter(QuestCompletionPredicate::max)
            ).apply(instance, QuestCompletionPredicate::new));

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(@NotNull Entity entity, @NotNull ServerLevel level, @Nullable Vec3 position) {
        int count = entity.getExistingData(WotrAttachments.QUEST_LOG)
                .map(status -> status.getCompletionCount(quest))
                .orElse(0);
        return count >= min && count <= max;
    }
}
