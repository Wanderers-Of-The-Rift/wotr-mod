package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public final class AbilityEquipmentSlot implements WotrEquipmentSlot {

    public static final MapCodec<AbilityEquipmentSlot> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("slot").forGetter(AbilityEquipmentSlot::slot)
    ).apply(instance, AbilityEquipmentSlot::forSlot));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityEquipmentSlot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AbilityEquipmentSlot::slot, AbilityEquipmentSlot::forSlot
    );

    public static final DualCodec<AbilityEquipmentSlot> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    public static final List<AbilityEquipmentSlot> SLOTS = IntStream.range(0, AbilitySlots.ABILITY_BAR_SIZE)
            .mapToObj(AbilityEquipmentSlot::new)
            .toList();

    private static final Int2ObjectArrayMap<AbilityEquipmentSlot> SLOT_INSTANCES = new Int2ObjectArrayMap<>();

    private final int slot;

    static {
        for (int i = 0; i < AbilitySlots.ABILITY_BAR_SIZE; i++) {
            SLOT_INSTANCES.put(i, new AbilityEquipmentSlot(i));
        }
    }

    private AbilityEquipmentSlot(int slot) {
        this.slot = slot;
    }

    public static AbilityEquipmentSlot forSlot(int slot) {
        return SLOT_INSTANCES.get(slot);
    }

    @Override
    public DualCodec<AbilityEquipmentSlot> type() {
        return TYPE;
    }

    @Override
    public boolean canAccept(ItemStack stack) {
        return stack.is(WotrItems.ABILITY_HOLDER);
    }

    @Override
    public ItemStack getContent(IAttachmentHolder entity) {
        return entity.getData(WotrAttachments.ABILITY_SLOTS.get()).getStackInSlot(slot);
    }

    @Override
    public @NotNull String getSerializedName() {
        return "ability_slot_" + slot;
    }

    public int slot() {
        return slot;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbilityEquipmentSlot other) {
            return other.slot == slot;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot);
    }

    @Override
    public String toString() {
        return getSerializedName();
    }

}
