package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot.InventorySnapshot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DimensionDelvers.MODID);

    public static final Supplier<AttachmentType<InventorySnapshot>> INVENTORY_SNAPSHOT = ATTACHMENT_TYPES.register("inventory_snapshot", () -> AttachmentType.builder(InventorySnapshot::new).serialize(InventorySnapshot.CODEC).build());
    public static final Supplier<AttachmentType<List<ItemStack>>> RESPAWN_ITEMS = ATTACHMENT_TYPES.register("respawn_items", () -> AttachmentType.builder(() -> (List<ItemStack>)new ArrayList<ItemStack>()).serialize(ItemStack.CODEC.listOf()).copyOnDeath().build());
}
