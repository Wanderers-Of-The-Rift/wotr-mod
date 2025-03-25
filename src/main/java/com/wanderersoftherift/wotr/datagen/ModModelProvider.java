package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.BlockFamilyHelper;
import com.wanderersoftherift.wotr.client.render.item.properties.select.SelectRuneGemShape;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, WanderersOfTheRift.MODID);
    }

    private static ResourceLocation createRuneGemShapeModel(ResourceLocation location, Item item, String suffix, ModelTemplate modelTemplate, ItemModelGenerators itemModels) {
        return modelTemplate.create(location, TextureMapping.layer0(location), itemModels.modelOutput);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.RUNE_ANVIL_ENTITY_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.DEV_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.KEY_FORGE.get());

        ResourceLocation baseChestModel = WanderersOfTheRift.id("block/rift_chest");
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                                ModBlocks.RIFT_CHEST.get(),
                                Variant.variant().with(VariantProperties.MODEL, baseChestModel))
                        .with(BlockModelGenerators.createHorizontalFacingDispatch())
        );

        ResourceLocation baseRiftSpawnerModel = WanderersOfTheRift.id("block/rift_spawner");
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.RIFT_SPAWNER.get(), Variant.variant().with(VariantProperties.MODEL, baseRiftSpawnerModel))
                        .with(createFacingDispatchFromUpModel())
        );



        itemModels.itemModelOutput.accept(ModItems.BUILDER_GLASSES.get(), ItemModelUtils.plainModel(WanderersOfTheRift.id("item/builder_glasses")));

        itemModels.generateFlatItem(ModItems.EXAMPLE_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RIFT_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RUNEGEM_GEODE.get(), ModelTemplates.FLAT_ITEM);

        this.generateRunegemItem(ModItems.RUNEGEM.get(), itemModels);

        ModBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> createModelsForBuildBlock(helper, blockModels, itemModels));
    }

    private void createModelsForBuildBlock(BlockFamilyHelper helper, BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.family(helper.getBlock().get()).generateFor(helper.getFamily());
    }

    public void generateRunegemItem(Item item, ItemModelGenerators itemModels) {
        ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
        ResourceLocation shapeLocation = ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "item/runegem/shape/");
        List<SelectItemModel.SwitchCase<RunegemShape>> list = new ArrayList<>(RunegemShape.values().length);
        for (RunegemShape shape : RunegemShape.values()) {
            ItemModel.Unbaked model = ItemModelUtils.plainModel(createRuneGemShapeModel(shapeLocation.withSuffix(shape.getName()), ModItems.RUNEGEM.get(), shape.getName(), ModelTemplates.FLAT_ITEM, itemModels));
            list.add(ItemModelUtils.when(shape, model));
        }
        itemModels.itemModelOutput.accept(item, ItemModelUtils.select(new SelectRuneGemShape(), ItemModelUtils.plainModel(modelLocation), list));
    }

    /**
     * @return A dispatch for facing a model away from the surface it is placed on, starting from an upward facing model
     */
    public static PropertyDispatch createFacingDispatchFromUpModel() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.UP, Variant.variant())
                .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

}
