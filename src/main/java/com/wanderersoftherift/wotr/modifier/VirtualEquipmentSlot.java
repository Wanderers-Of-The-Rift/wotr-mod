package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.implicit.RolledGearImplicits;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.List;

public record VirtualEquipmentSlot(String serializedName, List<ModifierInstance> modifiers)
        implements WotrEquipmentSlot {

    public static final MapCodec<VirtualEquipmentSlot> CODEC = RecordCodecBuilder.mapCodec(it -> it
            .group(Codec.STRING.fieldOf("name").forGetter(VirtualEquipmentSlot::serializedName),
                    ModifierInstance.CODEC.listOf().fieldOf("modifiers").forGetter(VirtualEquipmentSlot::modifiers))
            .apply(it, VirtualEquipmentSlot::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, VirtualEquipmentSlot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, VirtualEquipmentSlot::serializedName,
            ModifierInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), VirtualEquipmentSlot::modifiers,
            VirtualEquipmentSlot::new
    );
    public static final DualCodec<VirtualEquipmentSlot> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<? extends WotrEquipmentSlot> type() {
        return TYPE;
    }

    @Override
    public boolean canAccept(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack getContent(IAttachmentHolder entity) {
        var patch = DataComponentPatch.builder();
        patch.set(WotrDataComponentType.GEAR_IMPLICITS.get(), new RolledGearImplicits(modifiers));
        var virtualStack = new ItemStack(Items.COMMAND_BLOCK /* todo create item used by virtual equipment slots */, 1);
        virtualStack.applyComponents(patch.build());
        return virtualStack;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
