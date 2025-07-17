package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrDataMaps;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeTypes;
import com.wanderersoftherift.wotr.item.crafting.EssenceRecipeInput;
import com.wanderersoftherift.wotr.item.crafting.KeyForgeRecipe;
import com.wanderersoftherift.wotr.item.handler.ChangeAwareItemHandler;
import com.wanderersoftherift.wotr.item.handler.TakeOnlyItemHandler;
import com.wanderersoftherift.wotr.util.ItemStackHandlerUtil;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
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
    public static final int INPUT_SLOTS = 5;
    private static final int OUTPUT_SLOTS = 1;
    private static final int PLAYER_INVENTORY_SLOTS = 3 * 9;
    private static final int PLAYER_SLOTS = PLAYER_INVENTORY_SLOTS + 9;
    private static final int INPUT_SLOTS_X = 8;
    private static final int INPUT_SLOTS_Y = 18;
    private static final int INPUT_SLOT_X_OFFSET = 54;
    private static final int INPUT_SLOT_Y_OFFSET = 54;
    private static final List<Integer> TIER_COSTS = IntStream.iterate(10, n -> n + 8).limit(20).boxed().toList();
    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(INPUT_SLOTS + OUTPUT_SLOTS)
            .tryMoveTo(0, INPUT_SLOTS)
            .forSlots(0, INPUT_SLOTS)
            .tryMoveToPlayer()
            .forSlot(INPUT_SLOTS)
            .tryMoveToPlayer()
            .build();

    private final ContainerLevelAccess access;
    private final IItemHandlerModifiable inputItems;
    private final ItemStackHandler resultItems;
    private final DataSlot tierPercent;

    public KeyForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public KeyForgeMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.KEY_FORGE_MENU.get(), containerId);
        this.access = access;
        this.tierPercent = DataSlot.standalone();
        this.inputItems = new ItemStackHandler(INPUT_SLOTS) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (slot == 0) {
                    if (stack.has(WotrDataComponentType.INFUSED)) {
                        return false;
                    }
                    return access.evaluate(
                            (level, blockPos) -> level.getServer()
                                    .getRecipeManager()
                                    .recipeMap()
                                    .byType(WotrRecipeTypes.KEY_FORGE_RECIPE.get())
                                    .stream()
                                    .map(x -> x.value().getInput())
                                    .anyMatch(x -> x.test(stack))
                    ).orElse(false);
                } else {
                    return stack.getItemHolder().getData(WotrDataMaps.ESSENCE_VALUE_DATA) != null;
                }
            }
        };
        IItemHandlerModifiable inputSlotHandler = new ChangeAwareItemHandler(inputItems) {
            @Override
            public void onSlotChanged(int slot, ItemStack oldStack, ItemStack newStack) {
                access.execute((level, pos) -> {
                    if (level instanceof ServerLevel) {
                        update();
                    }
                });
            }
        };

        resultItems = new ItemStackHandler(1);
        IItemHandlerModifiable resultSlotHandler = new ChangeAwareItemHandler(new TakeOnlyItemHandler(resultItems)) {
            @Override
            public void onSlotChanged(int slot, ItemStack oldStack, ItemStack newStack) {
                access.execute((level, pos) -> {
                    if (!oldStack.isEmpty() && newStack.isEmpty()) {
                        for (int i = 0; i < inputItems.getSlots(); i++) {
                            ItemStack stackInSlot = inputItems.getStackInSlot(i);
                            if (!stackInSlot.isEmpty()) {
                                inputItems.extractItem(i, stackInSlot.getCount(), false);
                            }
                        }
                        update();
                    }
                });
            }
        };

        addSlot(new SlotItemHandler(inputSlotHandler, 0, 35, 45));
        for (int slotY = 0; slotY < 2; slotY++) {
            for (int slotX = 0; slotX < 2; slotX++) {
                addSlot(new SlotItemHandler(inputSlotHandler, slotY * 2 + slotX + 1,
                        INPUT_SLOTS_X + INPUT_SLOT_X_OFFSET * slotX, INPUT_SLOTS_Y + INPUT_SLOT_Y_OFFSET * slotY));
            }
        }
        addSlot(new SlotItemHandler(resultSlotHandler, 0, 148, 78));

        addStandardInventorySlots(playerInventory, 8, 114);

        addDataSlot(tierPercent);
    }

    public int getTierPercent() {
        return tierPercent.get();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, WotrBlocks.KEY_FORGE.get());
    }

    @Override
    public void removed(@NotNull Player player) {
        this.access.execute((world, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStackHandlerUtil.placeInPlayerInventoryOrDrop(serverPlayer, inputItems);
            }
        });

    }

    private void update() {
        access.execute((level, pos) -> {
            ItemStack inputItem = inputItems.getStackInSlot(0);
            List<ItemStack> essenceSources = new ArrayList<>();
            for (int slot = 1; slot < inputItems.getSlots(); slot++) {
                if (!inputItems.getStackInSlot(slot).isEmpty()) {
                    essenceSources.add(inputItems.getStackInSlot(slot));
                }
            }
            EssenceRecipeInput recipeInput = new EssenceRecipeInput(inputItem, essenceSources);
            updateTier(recipeInput.getTotalEssence());
            updateOutput(recipeInput);
        });
    }

    private void applyKeyforgeRecipes(ItemStack riftKey, EssenceRecipeInput input) {
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
        if (tier == 0 && !resultItems.getStackInSlot(0).isEmpty()) {
            resultItems.setStackInSlot(0, ItemStack.EMPTY);
            return;
        }

        if (tier > 0 && !input.getInputItem().isEmpty()) {
            ItemStack output = input.getInputItem().copyWithCount(1);
            applyKeyforgeRecipes(output, input);
            if (ItemStack.isSameItemSameComponents(output, input.getInputItem())) {
                resultItems.setStackInSlot(0, ItemStack.EMPTY);
            } else {
                output.set(WotrDataComponentType.ITEM_RIFT_TIER, tier);
                output.set(WotrDataComponentType.INFUSED, true);
                resultItems.setStackInSlot(0, output);
            }
        } else {
            resultItems.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

}
