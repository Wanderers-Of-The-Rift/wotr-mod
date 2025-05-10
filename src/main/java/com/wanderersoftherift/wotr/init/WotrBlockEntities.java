package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.AbilityBenchBlockEntity;
import com.wanderersoftherift.wotr.block.blockentity.DittoBlockEntity;
import com.wanderersoftherift.wotr.block.blockentity.RiftChestBlockEntity;
import com.wanderersoftherift.wotr.block.blockentity.RiftMobSpawnerBlockEntity;
import com.wanderersoftherift.wotr.block.blockentity.RuneAnvilBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class WotrBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<BlockEntityType<RuneAnvilBlockEntity>> RUNE_ANVIL_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("rune_anvil",
                    () -> new BlockEntityType<>(RuneAnvilBlockEntity::new, WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK.get()));

    public static final Supplier<BlockEntityType<RiftChestBlockEntity>> RIFT_CHEST = BLOCK_ENTITIES.register(
            "rift_chest", () -> new BlockEntityType<>(RiftChestBlockEntity::new, WotrBlocks.RIFT_CHEST.get()));

    public static final Supplier<BlockEntityType<DittoBlockEntity>> DITTO_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("ditto_block_entity",
                    // The block entity type.
                    () -> new BlockEntityType<>(DittoBlockEntity::new,
                            Set.of(WotrBlocks.DITTO_BLOCK.get(), WotrBlocks.TRAP_BLOCK.get(),
                                    WotrBlocks.MOB_TRAP_BLOCK.get(), WotrBlocks.PLAYER_TRAP_BLOCK.get(),
                                    WotrBlocks.SPRING_BLOCK.get())));

    public static final Supplier<BlockEntityType<RiftMobSpawnerBlockEntity>> RIFT_MOB_SPAWNER = BLOCK_ENTITIES.register(
            "rift_mob_spawner",
            () -> new BlockEntityType<>(RiftMobSpawnerBlockEntity::new, WotrBlocks.RIFT_MOB_SPAWNER.get()));

    public static final Supplier<BlockEntityType<AbilityBenchBlockEntity>> ABILITY_BENCH_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("ability_bench_block_entity",
                    // The block entity type.
                    () -> new BlockEntityType<>(AbilityBenchBlockEntity::new, WotrBlocks.ABILITY_BENCH.get()));
}
