package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.gui.menu.slot.EssenceInputSlot;
import com.wanderersoftherift.wotr.gui.menu.slot.KeyOutputSlot;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeTypes;
import com.wanderersoftherift.wotr.item.crafting.EssenceRecipeInput;
import com.wanderersoftherift.wotr.item.crafting.KeyForgeRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Menu for the Key Forge. This menu totals the essence value of inputs and uses it to produce a key. The total essence
 * determines the tier, the essence type distribution determines the theme.
 */
public class KeyForgeMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOTS = 4;
    private static final int OUTPUT_SLOTS = 1;
    private static final int PLAYER_INVENTORY_SLOTS = 3 * 9;
    private static final int PLAYER_SLOTS = PLAYER_INVENTORY_SLOTS + 9;
    private static final int INPUT_SLOTS_X = 31;
    private static final int INPUT_SLOTS_Y = 33;
    private static final int INPUT_SLOT_X_OFFSET = 25;
    private static final int INPUT_SLOT_Y_OFFSET = 25;
    private static final List<Integer> TIER_COSTS = IntStream.iterate(10, n -> n + 8).limit(20).boxed().toList();

    private final ContainerLevelAccess access;
    private final Container inputContainer;
    private final ResultContainer resultContainer;
    private final DataSlot tierPercent;
    private final QuickMover mover;

    public KeyForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public KeyForgeMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.KEY_FORGE_MENU.get(), containerId);
        this.access = access;
        this.tierPercent = DataSlot.standalone();
        this.inputContainer = new SimpleContainer(5) {
            @Override
            public void setChanged() {
                super.setChanged();
                KeyForgeMenu.this.slotsChanged(this);
            }
        };
        this.resultContainer = new ResultContainer();
        for (int slotY = 0; slotY < 2; slotY++) {
            for (int slotX = 0; slotX < 2; slotX++) {
                addSlot(new EssenceInputSlot(inputContainer, slotY * 2 + slotX,
                        INPUT_SLOTS_X + INPUT_SLOT_X_OFFSET * slotX, INPUT_SLOTS_Y + INPUT_SLOT_Y_OFFSET * slotY));
            }
        }
        addSlot(new KeyOutputSlot(resultContainer, 4, 148, 78, inputContainer));

        addStandardInventorySlots(playerInventory, 8, 114);

        addDataSlot(tierPercent);

        mover = QuickMover.create(this)
                .forPlayerSlots(INPUT_SLOTS + OUTPUT_SLOTS)
                .tryMoveTo(0, INPUT_SLOTS)
                .forSlots(0, INPUT_SLOTS)
                .tryMoveToPlayer()
                .forSlot(INPUT_SLOTS)
                .tryMoveToPlayer()
                .build();
    }

    public int getTierPercent() {
        return tierPercent.get();
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        this.access.execute((level, pos) -> {
            if (level instanceof ServerLevel) {
                update();
            }
        });
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return mover.quickMove(player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, WotrBlocks.KEY_FORGE.get());
    }

    @Override
    public void removed(@NotNull Player player) {
        this.access.execute((world, pos) -> this.clearContainer(player, inputContainer));
    }

    private void update() {
        access.execute((level, pos) -> {
            List<ItemStack> inputs = new ArrayList<>();
            for (int slot = 0; slot < inputContainer.getContainerSize(); slot++) {
                if (!inputContainer.getItem(slot).isEmpty()) {
                    inputs.add(inputContainer.getItem(slot));
                }
            }
            EssenceRecipeInput recipeInput = new EssenceRecipeInput(inputs);
            updateTier(recipeInput.getTotalEssence());
            updateOutput(recipeInput);
        });
    }

    private ItemStack applyKeyforgeRecipes(ItemStack riftKey, EssenceRecipeInput input) {
        access.execute((level, pos) -> {
            Object2IntMap<DataComponentType<?>> priorities = new Object2IntArrayMap<>();
            Map<DataComponentType<?>, KeyForgeRecipe> recipes = new LinkedHashMap<>();

            for (RecipeHolder<KeyForgeRecipe> recipeHolder : level.getServer()
                    .getRecipeManager()
                    .recipeMap()
                    .byType(WotrRecipeTypes.KEY_FORGE_RECIPE.get())) {
                KeyForgeRecipe recipe = recipeHolder.value();
                if (priorities.getOrDefault(recipe.getOutputType(), Integer.MIN_VALUE) < recipe.getPriority()
                        && recipe.matches(input, level)) {
                    recipes.put(recipe.getOutputType(), recipe);
                    priorities.put(recipe.getOutputType(), recipe.getPriority());
                }
            }

            for (KeyForgeRecipe value : recipes.values()) {
                value.apply(riftKey);
            }
        });
        return riftKey;
    }

    private void updateTier(int totalEssence) {
        int remainingEssence = totalEssence;
        int result = 0;
        for (int i = 0; i < TIER_COSTS.size() && remainingEssence > 0; i++) {
            if (remainingEssence >= TIER_COSTS.get(i)) {
                result += 100;
                remainingEssence -= TIER_COSTS.get(i);
            } else {
                result += 100 * remainingEssence / TIER_COSTS.get(i);
                remainingEssence = 0;
            }
        }
        tierPercent.set(result);
    }

    private void updateOutput(EssenceRecipeInput input) {
        int tier = tierPercent.get() / 100;
        if (tier == 0 && !resultContainer.isEmpty()) {
            resultContainer.clearContent();
            return;
        }

        if (tier > 0) {
            ItemStack output = WotrItems.RIFT_KEY.toStack();
            output.set(WotrDataComponentType.ITEM_RIFT_TIER, tier);
            output = applyKeyforgeRecipes(output, input);
            resultContainer.setItem(0, output);
        }
    }

}
