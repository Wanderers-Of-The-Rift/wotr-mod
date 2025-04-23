package com.wanderersoftherift.wotr.abilities.upgrade;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.util.FastUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A data component providing a pool of upgrades for an ability. This is composed of a list of choices, each of which
 * grants a number of options that can be switched between. Each choice is only composed of different upgrades. The
 * upgrade selection is selected fromm all upgrades that are relevant for the ability. As a data component the
 * AbilityUpgradePool is immutable, but can be converted to and from {@link AbilityUpgradePool.Mutable}
 */
public class AbilityUpgradePool {
    public static final int UNSELECTED = -1;
    public static final Codec<AbilityUpgradePool> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(AbilityUpgrade.REGISTRY_CODEC.listOf().listOf().fieldOf("choices").forGetter(x -> x.choices),
                            Codec.INT.listOf()
                                    .<IntList>xmap(IntArrayList::new, FastUtils::toList)
                                    .fieldOf("selected_upgrades")
                                    .forGetter(x -> x.selectedUpgrades))
                    .apply(instance, AbilityUpgradePool::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityUpgradePool> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(RegistryEvents.ABILITY_UPGRADE_REGISTRY)
                    .apply(ByteBufCodecs.list())
                    .apply(ByteBufCodecs.list()),
            x -> x.choices, ByteBufCodecs.INT.apply(ByteBufCodecs.list()).map(IntArrayList::new, FastUtils::toList),
            x -> x.selectedUpgrades, AbilityUpgradePool::new);
    /**
     * The number of selections available per choice
     */
    public static final int SELECTION_PER_LEVEL = 3;

    /**
     * The cost for unlocking each level
     */
    public static final IntList COST_PER_LEVEL = new IntArrayList(
            new int[] { 0, 1, 1, 1, 2, 2, 3, 4, 5, 7, 9, 12, 16, 21, 28 });

    protected final List<List<Holder<AbilityUpgrade>>> choices;
    protected final IntList selectedUpgrades;

    public AbilityUpgradePool(List<List<Holder<AbilityUpgrade>>> choices, IntList selectedUpgrades) {
        this.choices = new ArrayList<>();
        for (List<Holder<AbilityUpgrade>> choice : choices) {
            this.choices.add(new ArrayList<>(choice));
        }
        this.selectedUpgrades = new IntArrayList(selectedUpgrades);
    }

    public AbilityUpgradePool(AbilityUpgradePool other) {
        this(other.choices, other.selectedUpgrades);
    }

    /**
     * @return The number of choices available. Can be considered the level of the ability
     */
    public int getChoiceCount() {
        return choices.size();
    }

    /**
     * @param index The index of the choice. Must be between 0 and {@link #getChoiceCount()}
     * @return The options available for the choice
     */
    public List<Holder<AbilityUpgrade>> getChoiceOptions(int index) {
        return Collections.unmodifiableList(choices.get(index));
    }

    /**
     * @param choice The index of a choice.
     * @return The index of the selection for the given choice
     */
    public int getSelectedIndex(int choice) {
        if (choice >= selectedUpgrades.size()) {
            return UNSELECTED;
        }
        return selectedUpgrades.getInt(choice);
    }

    /**
     * @param choice
     * @return The selected upgrade for the given choice, if any
     */
    public Optional<AbilityUpgrade> getSelectedUpgrade(int choice) {
        if (choice >= selectedUpgrades.size()) {
            return Optional.empty();
        }
        int index = selectedUpgrades.getInt(choice);
        if (index >= 0 && index < choices.get(choice).size()) {
            return Optional.of(choices.get(choice).get(index).value());
        }
        return Optional.empty();
    }

    /**
     * @return A map of all selected ability upgrades, and the number of times they have been selected
     */
    public Object2IntMap<AbilityUpgrade> getAllSelected() {
        Object2IntArrayMap<AbilityUpgrade> result = new Object2IntArrayMap<>();
        for (int i = 0; i < getChoiceCount(); i++) {
            getSelectedUpgrade(i).ifPresent(upgrade -> result.merge(upgrade, 1, Integer::sum));
        }
        return result;
    }

    /**
     * For each choice with a selected ability, will supply that choice index and ability to a consumer
     *
     * @param consumer A consumer that will be supplied with the index of each choice, and the selected upgrade if.
     */
    public void forEachSelected(BiConsumer<Integer, AbilityUpgrade> consumer) {
        for (int i = 0; i < getChoiceCount(); i++) {
            final int choice = i;
            getSelectedUpgrade(i).ifPresent(x -> consumer.accept(choice, x));
        }
    }

    /**
     * @return A mutable AbilityUpgradePool for modification
     */
    public AbilityUpgradePool.Mutable getMutable() {
        return new Mutable(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbilityUpgradePool other) {
            return Objects.equals(choices, other.choices) && Objects.equals(selectedUpgrades, other.selectedUpgrades);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(choices, selectedUpgrades);
    }

    /**
     * A mutable AbilityUpgradePool, to allow modification before being converted back to an immutable pool for use as a
     * DataComponent
     */
    public static final class Mutable extends AbilityUpgradePool {

        public Mutable() {
            this(Collections.emptyList(), IntLists.emptyList());
        }

        public Mutable(List<List<Holder<AbilityUpgrade>>> choices, IntList selectedChoices) {
            super(choices, selectedChoices);
        }

        public Mutable(AbilityUpgradePool other) {
            super(other);
        }

        public AbilityUpgradePool toImmutable() {
            return new AbilityUpgradePool(this);
        }

        /**
         * @param choice    The choice to select for
         * @param selection The option to select
         * @return The mutable instance for chaining
         */
        public Mutable selectChoice(int choice, int selection) {
            Preconditions.checkArgument(choice >= 0 && choice < choices.size());
            Preconditions.checkArgument(selection >= 0 && selection < choices.get(choice).size());
            while (selectedUpgrades.size() <= choice) {
                selectedUpgrades.add(UNSELECTED);
            }
            selectedUpgrades.set(choice, selection);
            return this;
        }

        /**
         * Generates a single additional choice
         *
         * @param registryAccess Registry access for fetching possible upgrades
         * @param ability        The ability that the choice is being generated for
         * @param random         A random source
         * @param optionCount    How many options to include in the choice
         * @return This object for method chaining
         */
        public Mutable generateChoice(RegistryAccess registryAccess, AbstractAbility ability, RandomSource random,
                int optionCount) {
            generateChoices(registryAccess, ability, 1, random, optionCount);
            return this;
        }

        /**
         * Generates a number of choices
         *
         * @param registryAccess Registry access for fetching possible upgrades
         * @param ability        The ability that the choices are being generated for
         * @param count          How many choices to generate
         * @param random         A random source
         * @param optionCount    How many options to include in each choice
         * @return This object for method chaining
         */
        public Mutable generateChoices(RegistryAccess registryAccess, AbstractAbility ability, int count,
                RandomSource random, int optionCount) {
            Object2IntMap<Holder<AbilityUpgrade>> availableUpgrades = determineChoices(registryAccess, ability);

            for (int i = 0; i < count; i++) {
                ObjectList<Holder<AbilityUpgrade>> upgradeSet = new ObjectArrayList<>(availableUpgrades.keySet());
                List<Holder<AbilityUpgrade>> choice = new ArrayList<>();
                for (int j = 0; j < optionCount && !upgradeSet.isEmpty(); j++) {
                    choice.add(upgradeSet.remove(random.nextInt(upgradeSet.size())));
                }
                for (Holder<AbilityUpgrade> item : choice) {
                    int previousCount = availableUpgrades.getInt(item);
                    availableUpgrades.put(item, previousCount - 1);
                }
                choices.add(choice);
                selectedUpgrades.add(UNSELECTED);
            }
            return this;
        }

        private Object2IntMap<Holder<AbilityUpgrade>> determineChoices(RegistryAccess registryAccess,
                AbstractAbility ability) {
            Registry<AbilityUpgrade> upgrades = registryAccess.lookupOrThrow(RegistryEvents.ABILITY_UPGRADE_REGISTRY);
            Object2IntArrayMap<Holder<AbilityUpgrade>> availableUpgrades = upgrades.stream()
                    .filter(x -> isRelevant(x, ability))
                    .map(upgrades::wrapAsHolder)
                    .collect(
                            Collectors.toMap(x -> x, x -> x.value().maxCount(), Integer::sum, Object2IntArrayMap::new));
            choices.forEach(
                    options -> options.forEach((item) -> availableUpgrades.mergeInt(item, 0, (a, b) -> a + b - 1)));
            return availableUpgrades;
        }

        private boolean isRelevant(AbilityUpgrade upgrade, AbstractAbility ability) {
            for (AbstractModifierEffect modifierEffect : upgrade.modifierEffects()) {
                if (!ability.isRelevantModifier(modifierEffect)) {
                    return false;
                }
            }
            return true;
        }
    }
}
