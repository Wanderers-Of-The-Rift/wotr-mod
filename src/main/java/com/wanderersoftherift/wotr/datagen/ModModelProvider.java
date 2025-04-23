package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.BlockFamilyHelper;
import com.wanderersoftherift.wotr.block.TrapBlock;
import com.wanderersoftherift.wotr.client.render.item.ability.AbilitySpecialRenderer;
import com.wanderersoftherift.wotr.client.render.item.properties.select.SelectRuneGemShape;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, WanderersOfTheRift.MODID);
    }

    private static ResourceLocation createRuneGemShapeModel(ResourceLocation location, ModelTemplate modelTemplate,
            ItemModelGenerators itemModels) {
        return modelTemplate.create(location, TextureMapping.layer0(location), itemModels.modelOutput);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.RUNE_ANVIL_ENTITY_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.DEV_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.KEY_FORGE.get());
        blockModels.createTrivialBlock(ModBlocks.DITTO_BLOCK.get(),
                TexturedModel.CUBE.updateTemplate(template -> template.extend().renderType("cutout").build()));
        blockModels.createTrivialCube(ModBlocks.SPRING_BLOCK.get());

        createBlockStatesForTrapBlock(ModBlocks.MOB_TRAP_BLOCK, blockModels);
        createBlockStatesForTrapBlock(ModBlocks.PLAYER_TRAP_BLOCK, blockModels);
        createBlockStatesForTrapBlock(ModBlocks.TRAP_BLOCK, blockModels);

        ResourceLocation abilityBenchModel = WanderersOfTheRift.id("block/ability_bench");
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(ModBlocks.ABILITY_BENCH.get(),
                        Variant.variant().with(VariantProperties.MODEL, abilityBenchModel))
                .with(BlockModelGenerators.createHorizontalFacingDispatch()));

        ResourceLocation baseChestModel = WanderersOfTheRift.id("block/rift_chest");
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(ModBlocks.RIFT_CHEST.get(),
                        Variant.variant().with(VariantProperties.MODEL, baseChestModel))
                .with(BlockModelGenerators.createHorizontalFacingDispatch()));

        ResourceLocation baseRiftSpawnerModel = WanderersOfTheRift.id("block/rift_spawner");
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(ModBlocks.RIFT_SPAWNER.get(),
                        Variant.variant().with(VariantProperties.MODEL, baseRiftSpawnerModel))
                .with(createFacingDispatchFromUpModel()));

        itemModels.itemModelOutput.accept(ModItems.BUILDER_GLASSES.get(),
                ItemModelUtils.plainModel(WanderersOfTheRift.id("item/builder_glasses")));

        itemModels.generateFlatItem(ModItems.EXAMPLE_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RIFT_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RUNEGEM_GEODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BASE_ABILITY_HOLDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SKILL_THREAD.get(), ModelTemplates.FLAT_ITEM);

        itemModels.itemModelOutput.accept(ModItems.ABILITY_HOLDER.get(),
                new SpecialModelWrapper.Unbaked(WanderersOfTheRift.id("item/base_ability_holder"),
                        new AbilitySpecialRenderer.Unbaked(ModItems.BASE_ABILITY_HOLDER)));

        this.generateRunegemItem(ModItems.RUNEGEM.get(), itemModels);

        ModBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> createModelsForBuildBlock(helper, blockModels));
    }

    private void createBlockStatesForTrapBlock(DeferredBlock<? extends Block> trapBlock,
            BlockModelGenerators generators) {
        ResourceLocation model0 = ModelLocationUtils.getModelLocation(trapBlock.get(), "/0");
        ResourceLocation model1 = ModelLocationUtils.getModelLocation(trapBlock.get(), "/1");
        ResourceLocation model2 = ModelLocationUtils.getModelLocation(trapBlock.get(), "/2");

        // Item Model (Pull from model0)
        generators.registerSimpleItemModel(trapBlock.get(), model0);

        // Block Models (one for each variant)
        ModelTemplates.CUBE_ALL.createWithSuffix(trapBlock.get(), "/0", TextureMapping.cube(model0),
                generators.modelOutput);
        ModelTemplates.CUBE_ALL.createWithSuffix(trapBlock.get(), "/1", TextureMapping.cube(model1),
                generators.modelOutput);
        ModelTemplates.CUBE_ALL.createWithSuffix(trapBlock.get(), "/2", TextureMapping.cube(model2),
                generators.modelOutput);

        // Blockstate (point to the three unique block models)
        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(trapBlock.get())
                .with(PropertyDispatch.property(TrapBlock.STAGE)
                        .select(0, Variant.variant().with(VariantProperties.MODEL, model0))
                        .select(1, Variant.variant().with(VariantProperties.MODEL, model1))
                        .select(2, Variant.variant().with(VariantProperties.MODEL, model2))));

    }

    private void createModelsForBuildBlock(BlockFamilyHelper helper, BlockModelGenerators blockModels) {
        if (helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.PANE) != null) {
            createGlassPane(blockModels,
                    helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.GLASS_BLOCK).get(),
                    helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.PANE).get());
        }

        if (helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.DIRECTIONAL_PILLAR) != null) {
            createDirectionalPillar(blockModels,
                    helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.DIRECTIONAL_PILLAR).get());
        }

        blockModels.family(helper.getBlock().get()).generateFor(helper.getFamily());
    }

    public void generateRunegemItem(Item item, ItemModelGenerators itemModels) {
        ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
        ResourceLocation shapeLocation = ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID,
                "item/runegem/shape/");
        List<SelectItemModel.SwitchCase<RunegemShape>> list = new ArrayList<>(RunegemShape.values().length);
        for (RunegemShape shape : RunegemShape.values()) {
            ItemModel.Unbaked model = ItemModelUtils.plainModel(createRuneGemShapeModel(
                    shapeLocation.withSuffix(shape.getName()), ModelTemplates.FLAT_ITEM, itemModels));
            list.add(ItemModelUtils.when(shape, model));
        }
        itemModels.itemModelOutput.accept(item,
                ItemModelUtils.select(new SelectRuneGemShape(), ItemModelUtils.plainModel(modelLocation), list));
    }

    /**
     * @return A dispatch for facing a model away from the surface it is placed on, starting from an upward facing model
     */
    public static PropertyDispatch createFacingDispatchFromUpModel() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.UP, Variant.variant())
                .select(Direction.DOWN,
                        Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.NORTH,
                        Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.EAST,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(Direction.SOUTH,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(Direction.WEST,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    private void createGlassPane(BlockModelGenerators blockModels, Block glassBlock, Block paneBlock) {
        blockModels.createTrivialBlock(glassBlock,
                TexturedModel.createDefault(
                        block -> new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(glassBlock)),
                        ExtendedModelTemplateBuilder.builder()
                                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/cube_all"))
                                .requiredTextureSlot(TextureSlot.ALL)
                                .renderType("translucent")
                                .build()));

        ExtendedModelTemplate panePostTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_post"))
                .suffix("_post")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        ExtendedModelTemplate paneSideTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_side"))
                .suffix("_side")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        ExtendedModelTemplate paneSideAltTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_side_alt"))
                .suffix("_side_alt")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        ExtendedModelTemplate paneNoSideTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_noside"))
                .suffix("_noside")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        ExtendedModelTemplate paneNoSideAltTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_noside_alt"))
                .suffix("_noside_alt")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        TextureMapping texturemapping = TextureMapping.pane(glassBlock, paneBlock);
        ResourceLocation resourceLocationPanePost = panePostTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        ResourceLocation resourceLocationPaneSide = paneSideTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        ResourceLocation resourceLocationPaneSideAlt = paneSideAltTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        ResourceLocation resourceLocationNoSide = paneNoSideTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        ResourceLocation resourceLocationNoSideAlt = paneNoSideAltTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        Item item = paneBlock.asItem();

        blockModels.registerSimpleItemModel(item, blockModels.createFlatItemModelWithBlockTexture(item, glassBlock));
        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(paneBlock)
                .with(Variant.variant().with(VariantProperties.MODEL, resourceLocationPanePost))
                .with(Condition.condition().term(BlockStateProperties.NORTH, true),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocationPaneSide))
                .with(Condition.condition().term(BlockStateProperties.EAST, true),
                        Variant.variant()
                                .with(VariantProperties.MODEL, resourceLocationPaneSide)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .with(Condition.condition().term(BlockStateProperties.SOUTH, true),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocationPaneSideAlt))
                .with(Condition.condition().term(BlockStateProperties.WEST, true),
                        Variant.variant()
                                .with(VariantProperties.MODEL, resourceLocationPaneSideAlt)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .with(Condition.condition().term(BlockStateProperties.NORTH, false),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocationNoSide))
                .with(Condition.condition().term(BlockStateProperties.EAST, false),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocationNoSideAlt))
                .with(Condition.condition().term(BlockStateProperties.SOUTH, false),
                        Variant.variant()
                                .with(VariantProperties.MODEL, resourceLocationNoSideAlt)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .with(Condition.condition().term(BlockStateProperties.WEST, false),
                        Variant.variant()
                                .with(VariantProperties.MODEL, resourceLocationNoSide)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
    }

    private void createDirectionalPillar(BlockModelGenerators blockModels, Block directionalPillarBlock) {
        blockModels.createRotatedPillarWithHorizontalVariant(directionalPillarBlock, TexturedModel.COLUMN_ALT,
                TexturedModel.COLUMN_HORIZONTAL_ALT);
    }
}
