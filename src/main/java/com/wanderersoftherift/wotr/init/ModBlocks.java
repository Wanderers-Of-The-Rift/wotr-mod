package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.BlockFamilyHelper;
import com.wanderersoftherift.wotr.block.RiftChestEntityBlock;
import com.wanderersoftherift.wotr.block.RuneAnvilEntityBlock;
import com.wanderersoftherift.wotr.block.RiftSpawnerBlock;
import com.wanderersoftherift.wotr.item.RiftChestType;
import com.wanderersoftherift.wotr.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.wanderersoftherift.wotr.block.BlockFamilyHelper.*;
import static net.minecraft.world.level.block.state.properties.WoodType.OAK;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WanderersOfTheRift.MODID);
    public static final Map<RiftChestType, DeferredBlock<Block>> CHEST_TYPES = new HashMap<>();
    public static final List<BlockFamilyHelper> BLOCK_FAMILY_HELPERS = new ArrayList<>();

    public static final DeferredBlock<Block> DEV_BLOCK = registerBlock("dev_block", () -> new Block(BlockBehaviour.Properties.of()
            .setId(blockId("dev_block"))
            .destroyTime(-1F)
            .explosionResistance(3600000F)
            .sound(SoundType.STONE)
            .lightLevel(state -> 7)
    ));

    public static final DeferredBlock<RuneAnvilEntityBlock> RUNE_ANVIL_ENTITY_BLOCK = registerBlock(
            "rune_anvil",
            () -> new RuneAnvilEntityBlock(BlockBehaviour.Properties.of()
                    .setId(blockId("rune_anvil"))
                    .strength(2.5F)
                    .sound(SoundType.METAL)
            )
    );

    public static final DeferredBlock<RiftChestEntityBlock> RIFT_CHEST = registerChestBlock(
            "rift_chest",
            () -> new RiftChestEntityBlock(ModBlockEntities.RIFT_CHEST::get, BlockBehaviour.Properties.of()
                    .setId(blockId("rift_chest"))
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
            ),
            RiftChestType.WOODEN
    );

    public static final DeferredBlock<RiftSpawnerBlock> RIFT_SPAWNER = registerBlock(
            "rift_spawner",
            () -> new RiftSpawnerBlock(BlockBehaviour.Properties.of()
                    .setId(blockId("rift_spawner"))
                    .strength(2.0f)
                    .sound(SoundType.STONE)
            )
    );

    public static final DeferredBlock<KeyForgeBlock> KEY_FORGE = registerBlock(
      "key_forge",
            () -> new KeyForgeBlock(BlockBehaviour.Properties.of()
                    .setId(blockId("key_forge"))
                    .strength(2.0f)
                    .sound(SoundType.STONE)
            )
    );

    public static final BlockFamilyHelper PROCESSOR_BLOCK_1 = registerBuildingBlock("processor_block_1", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_1"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_2 = registerBuildingBlock("processor_block_2", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_2"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_3 = registerBuildingBlock("processor_block_3", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_3"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_4 = registerBuildingBlock("processor_block_4", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_4"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_5 = registerBuildingBlock("processor_block_5", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_5"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_6 = registerBuildingBlock("processor_block_6", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_6"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_7 = registerBuildingBlock("processor_block_7", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_7"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_8 = registerBuildingBlock("processor_block_8", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_8"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_9 = registerBuildingBlock("processor_block_9", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_9"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_10 = registerBuildingBlock("processor_block_10", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_10"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_11 = registerBuildingBlock("processor_block_11", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_11"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_12 = registerBuildingBlock("processor_block_12", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_12"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_13 = registerBuildingBlock("processor_block_13", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_13"))));
    public static final BlockFamilyHelper PROCESSOR_BLOCK_14 = registerBuildingBlock("processor_block_14", () -> new Block(BlockBehaviour.Properties.of().setId(blockId("processor_block_14"))));

    private static BlockFamilyHelper registerBuildingBlock(String id, Supplier<Block> sup) {
        DeferredBlock<Block> block = registerBlock(id, sup);
        BlockFamilyHelper buildingBlockHelper = new BlockFamilyHelper.Builder()
                .withBlockId(id).withBlock(block)
                .withSlab(registerBlock(id + SLAB_SUFFIX, () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(block.get()).setId(blockId(id + SLAB_SUFFIX)))))
                .withStairs(registerBlock(id + STAIRS_SUFFIX, () -> new StairBlock(block.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(block.get()).setId(blockId(id + STAIRS_SUFFIX)))))
                .withButton(registerBlock(id + BUTTON_SUFFIX, () -> new ButtonBlock(BlockSetType.STONE, 30, BlockBehaviour.Properties.ofFullCopy(block.get()).noCollission().strength(0.5F).setId(blockId(id + BUTTON_SUFFIX)))))
                .withPressurePlate(registerBlock(id + PLATE_SUFFIX, () -> new PressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(block.get()).noCollission().strength(0.5F).setId(blockId(id + PLATE_SUFFIX)))))
                .withWall(registerBlock(id + WALL_SUFFIX, () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(block.get()).setId(blockId(id + WALL_SUFFIX)))))
                .withFence(registerBlock(id + FENCE_SUFFIX, () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(block.get()).setId(blockId(id + FENCE_SUFFIX)))))
                .withFenceGate(registerBlock(id + FENCE_GATE_SUFFIX, () -> new FenceGateBlock(OAK, BlockBehaviour.Properties.ofFullCopy(block.get()).setId(blockId(id + FENCE_GATE_SUFFIX)))))
                .withTrapdoor(registerBlock(id + TRAPDOOR_SUFFIX, () -> new TrapDoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(block.get()).setId(blockId(id + TRAPDOOR_SUFFIX)))))
                .createBuildBlockHelper();
        BLOCK_FAMILY_HELPERS.add(buildingBlockHelper);
        return buildingBlockHelper;
    }

    private static <T extends Block> DeferredBlock<T> registerChestBlock(String riftChest, Supplier<T> supplier, RiftChestType riftChestType) {
        DeferredBlock<T> register = registerBlock(riftChest, supplier);
        CHEST_TYPES.put(riftChestType, (DeferredBlock<Block>) register);
        return register;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String key, Supplier<T> sup) {
        DeferredBlock<T> register = BLOCKS.register(key, sup);
        ModItems.registerSimpleBlockItem(key, register);
        return register;
    }

    private static ResourceKey<Block> blockId(String name) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, name));
    }
}
