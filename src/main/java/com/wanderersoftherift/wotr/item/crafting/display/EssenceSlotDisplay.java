package com.wanderersoftherift.wotr.item.crafting.display;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.recipe.WotrSlotDisplayTypes;
import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Slot Display for EssencePredicates
 * 
 * @param predicate
 */
public record EssenceSlotDisplay(EssencePredicate predicate) implements SlotDisplay {

    public static final MapCodec<EssenceSlotDisplay> CODEC = EssencePredicate.CODEC.fieldOf("predicate")
            .xmap(EssenceSlotDisplay::new, EssenceSlotDisplay::predicate);
    public static final StreamCodec<RegistryFriendlyByteBuf, EssenceSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            EssencePredicate.STREAM_CODEC, EssenceSlotDisplay::predicate, EssenceSlotDisplay::new
    );

    @Override
    public <T> @NotNull Stream<T> resolve(@NotNull ContextMap context, @NotNull DisplayContentsFactory<T> output) {
        return switch (output) {
            case ForEssencePredicate<T> factory -> Stream.of(factory.forState(predicate));
            default -> Stream.empty();
        };
    }

    @Override
    public @NotNull Type<? extends SlotDisplay> type() {
        return WotrSlotDisplayTypes.ESSENCE_SLOT_DISPLAY.get();
    }
}
