package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.block.RiftChestEntityBlock;
import com.dimensiondelvers.dimensiondelvers.block.RuneAnvilBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DimensionDelvers.MODID);

    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock(
            "example_block", // Name
            BlockBehaviour.Properties.of() // Properties to use.
    );

    public static final DeferredBlock<Block> DEV_BLOCK = BLOCKS.register(
            "dev_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .setId(blockId("dev_block"))
                    .destroyTime(-1F)
                    .explosionResistance(3600000F)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 7)
            ));

    public static final DeferredBlock<RuneAnvilBlock> RUNE_ANVIL_BLOCK = BLOCKS.register(
            "rune_anvil",
            () -> new RuneAnvilBlock(BlockBehaviour.Properties.of()
                    .setId(blockId("rune_anvil"))
                    .strength(2.5F)
                    .sound(SoundType.METAL)
            )
    );

    public static final DeferredBlock<RiftChestEntityBlock> RIFT_CHEST = BLOCKS.register(
            "rift_chest",
            () -> new RiftChestEntityBlock(BlockBehaviour.Properties.of()
                    .setId(blockId("rift_chest"))
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
            )
    );

    private static ResourceKey<Block> blockId(String name) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DimensionDelvers.MODID, name));
    }
}
