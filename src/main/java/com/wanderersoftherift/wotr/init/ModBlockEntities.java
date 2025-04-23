package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.DittoBlockEntity;
import com.wanderersoftherift.wotr.block.blockentity.RiftChestBlockEntity;
import com.wanderersoftherift.wotr.block.blockentity.RuneAnvilBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, WanderersOfTheRift.MODID);
    public static final Supplier<BlockEntityType<RuneAnvilBlockEntity>> RUNE_ANVIL_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("rune_anvil",
                    () -> new BlockEntityType<>(RuneAnvilBlockEntity::new, ModBlocks.RUNE_ANVIL_ENTITY_BLOCK.get()));

    public static final Supplier<BlockEntityType<RiftChestBlockEntity>> RIFT_CHEST = BLOCK_ENTITIES
            .register("rift_chest", () -> new BlockEntityType<>(RiftChestBlockEntity::new, ModBlocks.RIFT_CHEST.get()));

    public static final Supplier<BlockEntityType<DittoBlockEntity>> DITTO_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("ditto_block_entity",
                    // The block entity type.
                    () -> new BlockEntityType<>(DittoBlockEntity::new,
                            Set.of(ModBlocks.DITTO_BLOCK.get(), ModBlocks.TRAP_BLOCK.get(),
                                    ModBlocks.MOB_TRAP_BLOCK.get(), ModBlocks.PLAYER_TRAP_BLOCK.get(),
                                    ModBlocks.SPRING_BLOCK.get())));
}
