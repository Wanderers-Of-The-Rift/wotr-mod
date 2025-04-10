package com.wanderersoftherift.wotr.block;

import com.google.common.collect.Maps;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.data.BlockFamily.Variant.*;


public class BlockFamilyHelper {
    public static final String SLAB_SUFFIX = "_slab";
    public static final String STAIRS_SUFFIX = "_stairs";
    public static final String BUTTON_SUFFIX = "_button";
    public static final String PLATE_SUFFIX = "_pressure_plate";
    public static final String WALL_SUFFIX = "_wall";
    public static final String FENCE_SUFFIX = "_fence";
    public static final String FENCE_GATE_SUFFIX = "_fence_gate";
    public static final String TRAPDOOR_SUFFIX = "_trapdoor";
    public static final String GLASS_BLOCK_SUFFIX = "_glass";
    public static final String PANE_SUFFIX = "_glass_pane";
    public static final String DIRECTIONAL_PILLAR_SUFFIX = "_directional_pillar";

    private final String blockId;
    private final Supplier<Block> baseBlock;
    private final Map<BlockFamily.Variant, Supplier<Block>> variants = Maps.newHashMap();
    private final Map<ModBlockFamilyVariant, Supplier<Block>> modVariants = Maps.newHashMap();
    private BlockFamily blockFamily;

    //additional variants that Mojang does not support yet
    public enum ModBlockFamilyVariant {
        GLASS_BLOCK("glass_block"),
        PANE("pane"),
        DIRECTIONAL_PILLAR("directional_pillar")
        ;

        private final String variantName;

        ModBlockFamilyVariant(String variantName){
            this.variantName = variantName;
        }

        public String getVariantName(){
            return variantName;
        }
    }

    public BlockFamilyHelper(String blockId, Supplier<Block> baseBlock, Map<BlockFamily.Variant, Supplier<Block>> variants,
                             Map<ModBlockFamilyVariant, Supplier<Block>> modVariants) {
        this.blockId = blockId;
        this.baseBlock = baseBlock;
        this.variants.putAll(variants);
        this.modVariants.putAll(modVariants);
    }

    public Supplier<Block> getBlock() {
        return baseBlock;
    }

    public BlockFamily getFamily() {
        if (blockFamily == null) {
            blockFamily = generateBlockFamily();
        }
        return blockFamily;
    }

    private BlockFamily generateBlockFamily() {
        BlockFamily.Builder blockFamilyBuilder = new BlockFamily.Builder(baseBlock.get());
        if (variants.containsKey(SLAB))           blockFamilyBuilder.slab(variants.get(SLAB).get());
        if (variants.containsKey(STAIRS))         blockFamilyBuilder.stairs(variants.get(STAIRS).get());
        if (variants.containsKey(BUTTON))         blockFamilyBuilder.button(variants.get(BUTTON).get());
        if (variants.containsKey(PRESSURE_PLATE)) blockFamilyBuilder.pressurePlate(variants.get(PRESSURE_PLATE).get());
        if (variants.containsKey(WALL))           blockFamilyBuilder.wall(variants.get(WALL).get());
        if (variants.containsKey(FENCE))          blockFamilyBuilder.fence(variants.get(FENCE).get());
        if (variants.containsKey(FENCE_GATE))     blockFamilyBuilder.fenceGate(variants.get(FENCE_GATE).get());
        if (variants.containsKey(TRAPDOOR))       blockFamilyBuilder.trapdoor(variants.get(TRAPDOOR).get());
        return blockFamilyBuilder.getFamily();
    }

    public ResourceLocation getBaseResourceLocation() {
        return WanderersOfTheRift.id(blockId);
    }

    public Map<BlockFamily.Variant, Supplier<Block>> getVariants() {
        return new HashMap<>(variants);
    }

    public  Supplier<Block> getVariant(BlockFamily.Variant variant) {
        return getVariants().get(variant);
    }

    public Map<ModBlockFamilyVariant, Supplier<Block>> getModVariants() {
        return new HashMap<>(modVariants);
    }

    public  Supplier<Block> getModVariants(ModBlockFamilyVariant variant) {
        return getModVariants().get(variant);
    }

    public static class Builder {
        private String blockId;
        private Supplier<Block> baseBlock;
        private final Map<BlockFamily.Variant, Supplier<Block>> variants = Maps.newHashMap();
        private final Map<ModBlockFamilyVariant, Supplier<Block>> modVariants = Maps.newHashMap();

        public Builder withBlockId(String blockId) {
            this.blockId = blockId;
            return this;
        }

        public Builder withBlock(Supplier<Block> block) {
            this.baseBlock = block;
            return this;
        }

        public Builder withSlab(Supplier<Block> slab) {
            this.variants.put(SLAB, slab);
            return this;
        }

        public Builder withStairs(Supplier<Block> stairs) {
            this.variants.put(BlockFamily.Variant.STAIRS, stairs);
            return this;
        }

        public Builder withButton(Supplier<Block> button) {
            this.variants.put(BlockFamily.Variant.BUTTON, button);
            return this;
        }

        public Builder withPressurePlate(Supplier<Block> pressurePlate) {
            this.variants.put(BlockFamily.Variant.PRESSURE_PLATE, pressurePlate);
            return this;
        }

        public Builder withFence(Supplier<Block> fence) {
            this.variants.put(BlockFamily.Variant.FENCE, fence);
            return this;
        }

        public Builder withFenceGate(Supplier<Block> fenceGate) {
            this.variants.put(BlockFamily.Variant.FENCE_GATE, fenceGate);
            return this;
        }

        public Builder withWall(Supplier<Block> wall) {
            this.variants.put(BlockFamily.Variant.WALL, wall);
            return this;
        }

        public Builder withTrapdoor(Supplier<Block> trapdoor) {
            this.variants.put(BlockFamily.Variant.TRAPDOOR, trapdoor);
            return this;
        }

        public Builder withPane(Supplier<Block> glassBlock, Supplier<Block> pane) {
            this.modVariants.put(ModBlockFamilyVariant.GLASS_BLOCK, glassBlock);
            this.modVariants.put(ModBlockFamilyVariant.PANE, pane);
            return this;
        }

        public Builder withDirectionalPillar(Supplier<Block> directionalPillar) {
            this.modVariants.put(ModBlockFamilyVariant.DIRECTIONAL_PILLAR, directionalPillar);
            return this;
        }

        public BlockFamilyHelper createBuildBlockHelper() {
            return new BlockFamilyHelper(blockId, baseBlock, variants, modVariants);
        }
    }
}
