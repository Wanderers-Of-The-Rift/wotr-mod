package com.wanderersoftherift.wotr.core.quest.goal;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 * Defines the different levels of rift completion that may be required by the {@link CompleteRiftGoal}
 */
public enum RiftCompletionLevel implements StringRepresentable {
    /**
     * The rift was attempted - regardless of whether the player survived
     */
    ATTEMPT(0, "attempt", Component.translatable(WanderersOfTheRift.translationId("goal", "rift.attempt"))),
    /**
     * The player at least survived the rift
     */
    SURVIVE(1, "survive", Component.translatable(WanderersOfTheRift.translationId("goal", "rift.survive"))),
    /**
     * The player successfully completed the rift
     */
    COMPLETE(2, "complete", Component.translatable(WanderersOfTheRift.translationId("goal", "rift.complete")));

    public static final StringRepresentable.StringRepresentableCodec<RiftCompletionLevel> CODEC = StringRepresentable
            .fromEnum(RiftCompletionLevel::values);

    private static final IntFunction<RiftCompletionLevel> BY_ID = ByIdMap.continuous(RiftCompletionLevel::getId,
            values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StreamCodec<ByteBuf, RiftCompletionLevel> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
            RiftCompletionLevel::getId);

    private final int id;
    private final String name;
    private final Component display;

    RiftCompletionLevel(int id, String name, Component display) {
        this.id = id;
        this.name = name;
        this.display = display;
    }

    public int getId() {
        return id;
    }

    /**
     * @return A component for displaying the level of completion
     */
    public Component getDisplay() {
        return display;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
