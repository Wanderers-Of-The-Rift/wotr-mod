package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.block.blockentity.AbilityBenchBlockEntity;
import com.wanderersoftherift.wotr.gui.menu.slot.AbilitySlot;
import com.wanderersoftherift.wotr.gui.menu.slot.LargeSlotItemHandler;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.item.handler.LargeCountItemHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The ability bench allows viewing and managing an ability and its upgrades
 */
public class AbilityBenchMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOTS = 2;
    private static final int PLAYER_INVENTORY_SLOTS = 3 * 9;
    private static final int PLAYER_SLOTS = PLAYER_INVENTORY_SLOTS + 9;

    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(INPUT_SLOTS)
            .tryMoveTo(0, INPUT_SLOTS)
            .tryMoveTo(INPUT_SLOTS + QuickMover.PLAYER_SLOTS, AbilitySlots.ABILITY_BAR_SIZE)
            .forSlot(0)
            .tryMoveTo(INPUT_SLOTS + QuickMover.PLAYER_SLOTS, AbilitySlots.ABILITY_BAR_SIZE)
            .tryMoveToPlayer()
            .forSlot(1)
            .tryMoveToPlayer()
            .forSlots(INPUT_SLOTS + QuickMover.PLAYER_SLOTS, AbilitySlots.ABILITY_BAR_SIZE)
            .tryMoveTo(0)
            .tryMoveToPlayer()
            .build();;

    private final ContainerLevelAccess access;
    private final SimpleContainer inputContainer;
    private final IItemHandler upgradeMatStorage;
    private final DataSlot canLevel;

    public AbilityBenchMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory,
                new LargeCountItemHandler(WotrItems.SKILL_THREAD.toStack(1), AbilityBenchBlockEntity.THREAD_STORAGE),
                ContainerLevelAccess.NULL, new ItemStackHandler(AbilitySlots.ABILITY_BAR_SIZE));
    }

    public AbilityBenchMenu(int containerId, Inventory playerInventory, IItemHandler persistentStore,
            ContainerLevelAccess access, IItemHandler abilities) {
        super(WotrMenuTypes.ABILITY_BENCH_MENU.get(), containerId);
        this.access = access;
        this.inputContainer = new SimpleContainer(1);
        this.upgradeMatStorage = persistentStore;
        inputContainer.addListener(this::onAbilitySlotChanged);
        addSlot(new AbilitySlot(inputContainer, 0, 32, 17));
        addSlot(new LargeSlotItemHandler(persistentStore, 0, 297, 7));

        addStandardInventorySlots(playerInventory, 32, 154);
        addPlayerAbilitySlots(abilities, 4, 46);

        canLevel = DataSlot.standalone();
        addDataSlot(canLevel);
    }

    protected void addPlayerAbilitySlots(IItemHandler abilitySlots, int x, int y) {
        for (int i = 0; i < abilitySlots.getSlots(); i++) {
            addSlot(new SlotItemHandler(abilitySlots, i, x, y + i * 18));
        }
    }

    /**
     * Adds a base upgrade pool to any inserted ability item
     *
     * @param container
     */
    private void onAbilitySlotChanged(Container container) {
        access.execute((level, blockPos) -> {
            ItemStack item = container.getItem(0);

            if (!item.isEmpty() && !item.has(WotrDataComponentType.ABILITY_UPGRADE_POOL)) {
                Holder<Ability> ability = item.get(WotrDataComponentType.ABILITY);
                RegistryAccess registryAccess = level.registryAccess();

                AbilityUpgradePool upgradePool = new AbilityUpgradePool.Mutable()
                        .generateChoices(registryAccess, ability.value(), 3, level.random,
                                AbilityUpgradePool.SELECTION_PER_LEVEL)
                        .toImmutable();
                item.set(WotrDataComponentType.ABILITY_UPGRADE_POOL.get(), upgradePool);
            }

            if (item.isEmpty()) {
                canLevel.set(0);
            } else {
                AbilityUpgradePool upgradePool = getUpgradePool();
                Holder<Ability> ability = getAbility();
                if (upgradePool.canLevelUp(level.registryAccess(), ability.value())) {
                    canLevel.set(1);
                } else {
                    canLevel.set(0);
                }
            }
        });
    }

    /**
     * @return Whether an ability item is in the ability input slot
     */
    public boolean isAbilityItemPresent() {
        ItemStack item = inputContainer.getItem(0);
        return !item.isEmpty() && item.has(WotrDataComponentType.ABILITY);
    }

    /**
     * @return The ability item (or Item.EMPTY)
     */
    public ItemStack getAbilityItem() {
        return inputContainer.getItem(0);
    }

    /**
     * @return The ability on the ability item, or null
     */
    public @Nullable Holder<Ability> getAbility() {
        ItemStack item = getAbilityItem();
        return item.get(WotrDataComponentType.ABILITY);
    }

    /**
     * @return The upgrade pool on the ability item, or null
     */
    public @Nullable AbilityUpgradePool getUpgradePool() {
        ItemStack item = getAbilityItem();
        if (!item.isEmpty() && item.has(WotrDataComponentType.ABILITY_UPGRADE_POOL)) {
            return item.get(WotrDataComponentType.ABILITY_UPGRADE_POOL);
        }
        return null;
    }

    /**
     * @return How much upgrade currency is available
     */
    public int availableUpgradeCurrency() {
        return upgradeMatStorage.getStackInSlot(0).getCount();
    }

    /**
     * @return Whether the ability could be leveled up (ignoring currency availability)
     */
    public boolean canLevelUp() {
        return canLevel.get() == 1;
    }

    /**
     * @return How much currency is required for the next level
     */
    public int costForNextLevel() {
        if (!canLevelUp()) {
            return 0;
        }
        AbilityUpgradePool pool = getUpgradePool();
        if (pool != null && pool.getChoiceCount() < AbilityUpgradePool.COST_PER_LEVEL.size()) {
            return AbilityUpgradePool.COST_PER_LEVEL.getInt(pool.getChoiceCount());
        }
        return 65;
    }

    /**
     * Unlocks a new level on the ability, spending currency and adding a new choice. For server-side use only.
     *
     * @param level The level to unlock
     */
    public void levelUp(int level) {
        access.execute((serverLevel, pos) -> {
            AbilityUpgradePool pool = getUpgradePool();
            if (pool == null || level != pool.getChoiceCount() + 1 || !canLevelUp()) {
                return;
            }
            int available = availableUpgradeCurrency();
            int cost = costForNextLevel();
            if (available < cost) {
                return;
            }
            upgradeMatStorage.extractItem(0, cost, false);
            AbilityUpgradePool updatedPool = pool.getMutable()
                    .generateChoice(serverLevel.registryAccess(), getAbility().value(), serverLevel.getRandom(),
                            AbilityUpgradePool.SELECTION_PER_LEVEL)
                    .toImmutable();
            getAbilityItem().set(WotrDataComponentType.ABILITY_UPGRADE_POOL, updatedPool);

            if (updatedPool.canLevelUp(serverLevel.registryAccess(), getAbility().value())) {
                canLevel.set(1);
            } else {
                canLevel.set(0);
            }
        });
    }

    /**
     * @return The current level of the ability
     */
    public int getUnlockLevel() {
        AbilityUpgradePool upgradePool = getUpgradePool();
        if (upgradePool != null) {
            return upgradePool.getChoiceCount();
        }
        return 0;
    }

    /**
     * Sets the selection for the given choice. For server-side use.
     *
     * @param choice
     * @param selection
     */
    public void selectAbility(int choice, int selection) {
        access.execute((serverLevel, pos) -> {
            if (isAbilityItemPresent()) {
                AbilityUpgradePool pool = getUpgradePool();
                if (choice < 0 || choice >= pool.getChoiceCount()) {
                    return;
                }
                List<Holder<AbilityUpgrade>> options = pool.getChoiceOptions(choice);
                if (selection < 0 || selection >= options.size()) {
                    return;
                }

                AbilityUpgradePool.Mutable mutable = pool.getMutable();
                mutable.selectChoice(choice, selection);
                DataComponentPatch patch = DataComponentPatch.builder()
                        .set(WotrDataComponentType.ABILITY_UPGRADE_POOL.get(), mutable.toImmutable())
                        .build();
                getAbilityItem().applyComponents(patch);
            }
        });
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, inputContainer));
    }

    @Override
    protected void clearContainer(@NotNull Player player, @NotNull Container container) {
        if (player instanceof ServerPlayer) {
            quickMoveStack(player, 0);
        }
        super.clearContainer(player, container);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, WotrBlocks.ABILITY_BENCH.get());
    }
}
