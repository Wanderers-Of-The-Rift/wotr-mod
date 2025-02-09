package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot.InventorySnapshot;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DimensionDelvers.MODID);

    public static final Supplier<AttachmentType<InventorySnapshot>> INVENTORY_SNAPSHOT = ATTACHMENT_TYPES.register("inventory_snapshot", () -> AttachmentType.builder(InventorySnapshot::new).serialize(InventorySnapshot.CODEC).copyOnDeath().build());
}
