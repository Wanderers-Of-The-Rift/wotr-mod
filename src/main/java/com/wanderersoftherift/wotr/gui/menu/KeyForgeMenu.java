package com.wanderersoftherift.wotr.gui.menu;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModDataMaps;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.ModMenuTypes;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import com.wanderersoftherift.wotr.util.FastCollectionUtils;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Menu for the Key Forge.
 * This menu totals the essence value of inputs and uses it to produce a key. The total essence determines the tier,
 * the essence type distribution determines the theme.
 */
public class KeyForgeMenu extends AbstractContainerMenu {
    public final static int INPUT_SLOTS = 4;
    private final static int OUTPUT_SLOTS = 1;
    private final static int PLAYER_INVENTORY_SLOTS = 3 * 9;
    private final static int PLAYER_SLOTS = PLAYER_INVENTORY_SLOTS + 9;
    private final static int INPUT_SLOTS_X = 31;
    private final static int INPUT_SLOTS_Y = 33;
    private final static int INPUT_SLOT_X_OFFSET = 25;
    private final static int INPUT_SLOT_Y_OFFSET = 25;
    private static final List<Integer> TIER_COSTS = IntStream.iterate(10, n -> n + 8)
        .limit(20)
        .boxed()
        .toList();

    private final ContainerLevelAccess access;
    private final Container inputContainer;
    private final ResultContainer resultContainer;
    private final DataSlot tierPercent;

    public KeyForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public KeyForgeMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenuTypes.KEY_FORGE_MENU.get(), containerId);
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
                addSlot(new EssenceInputSlot(inputContainer, slotY * 2 + slotX, INPUT_SLOTS_X + INPUT_SLOT_X_OFFSET * slotX, INPUT_SLOTS_Y + INPUT_SLOT_Y_OFFSET * slotY));
            }
        }
        addSlot(new KeyOutputSlot(resultContainer, 4, 148, 78, inputContainer));

        addStandardInventorySlots(playerInventory, 8, 114);

        addDataSlot(tierPercent);
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
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            ItemStack originalStack = slotStack.copy();
            if (slot instanceof KeyOutputSlot) {
                if (!this.moveItemStackTo(slotStack, INPUT_SLOTS + OUTPUT_SLOTS, INPUT_SLOTS + OUTPUT_SLOTS + PLAYER_SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, originalStack);
            } else if (slot instanceof EssenceInputSlot) {
                if (!this.moveItemStackTo(slotStack, INPUT_SLOTS + OUTPUT_SLOTS, INPUT_SLOTS + OUTPUT_SLOTS + PLAYER_SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
                update();
            } else {
                if (!this.moveItemStackTo(slotStack, 0, INPUT_SLOTS, false)) {
                    // Move from player inventory to hotbar
                    if (index < INPUT_SLOTS + OUTPUT_SLOTS + PLAYER_INVENTORY_SLOTS) {
                        if (!this.moveItemStackTo(slotStack, INPUT_SLOTS + OUTPUT_SLOTS + PLAYER_INVENTORY_SLOTS, INPUT_SLOTS + OUTPUT_SLOTS + PLAYER_SLOTS, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                    // Move from hotbar to player inventory
                    else if (!this.moveItemStackTo(slotStack, INPUT_SLOTS + OUTPUT_SLOTS, INPUT_SLOTS + OUTPUT_SLOTS + PLAYER_INVENTORY_SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, ModBlocks.KEY_FORGE.get());
    }

    @Override
    public void removed(@NotNull Player player) {
        this.access.execute((world, pos) -> this.clearContainer(player, inputContainer));
    }

    private void update() {
        int totalEssence = 0;
        Object2IntMap<ResourceLocation> essenceMap = new Object2IntArrayMap<>();
        for (int i = 0; i < inputContainer.getContainerSize(); i++) {
            ItemStack input = inputContainer.getItem(i);
            EssenceValue valueMap = input.getItemHolder().getData(ModDataMaps.ESSENCE_VALUE_DATA);
            if (valueMap == null) {
                continue;
            }
            for (Object2IntMap.Entry<ResourceLocation> entry : valueMap.values().object2IntEntrySet()) {
                essenceMap.mergeInt(entry.getKey(), entry.getIntValue() * input.getCount(), Integer::sum);
                totalEssence += entry.getIntValue() * input.getCount();
            }
        }

        updateTier(totalEssence);
        updateOutput(FastCollectionUtils.max(essenceMap));
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

    private void updateOutput(ResourceLocation type) {
        int tier = tierPercent.get() / 100;
        if (tier == 0 && !resultContainer.isEmpty()) {
            resultContainer.clearContent();
        } else if (tier > 0 && resultContainer.isEmpty()) {
            DataComponentPatch patch = buildKeyComponentPatch(tier, type);
            ItemStack output = ModItems.RIFT_KEY.toStack();
            output.applyComponents(patch);

            resultContainer.setItem(0, output);
        } else if (tier > 0 && !resultContainer.isEmpty()) {
            resultContainer.getItem(0).applyComponents(buildKeyComponentPatch(tier, type));
        }
    }

    private DataComponentPatch buildKeyComponentPatch(int tier, ResourceLocation theme) {
        return DataComponentPatch.builder()
                .set(ModDataComponentType.RIFT_TIER.get(), tier)
                .set(ModDataComponentType.RIFT_THEME.get(), theme)
                .build();
    }

}
