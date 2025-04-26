package com.wanderersoftherift.wotr.item.riftkey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.codec.DeferrableRegistryCodec;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe for imbuing a rift key with a theme. This specifies requirements for the ingredients to be valid for the
 * theme, and a priority for determining the theme to use if there are overlaps.
 */
public class ThemeRecipe {
    public static final Codec<ThemeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DeferrableRegistryCodec.create(ModRiftThemes.RIFT_THEME_KEY)
                    .fieldOf("theme")
                    .forGetter(ThemeRecipe::getTheme),
            Codec.INT.fieldOf("priority").forGetter(ThemeRecipe::getPriority),
            EssencePredicate.CODEC.listOf()
                    .optionalFieldOf("essence_reqs", List.of())
                    .forGetter(ThemeRecipe::getEssenceRequirements),
            ItemPredicate.CODEC.listOf()
                    .optionalFieldOf("item_reqs", List.of())
                    .forGetter(ThemeRecipe::getItemRequirements))
            .apply(instance, ThemeRecipe::new));

    private final Holder<RiftTheme> theme;
    private final int priority;

    private final List<EssencePredicate> essenceRequirements;
    private final List<ItemPredicate> itemRequirements;

    public ThemeRecipe(Holder<RiftTheme> theme, int priority, List<EssencePredicate> essenceRequirements,
            List<ItemPredicate> itemRequirements) {
        this.theme = theme;
        this.priority = priority;
        this.essenceRequirements = essenceRequirements;
        this.itemRequirements = itemRequirements;
    }

    public boolean matches(List<ItemStack> items, Object2IntMap<ResourceLocation> essences) {
        for (EssencePredicate essenceReq : essenceRequirements) {
            if (!essenceReq.match(essences)) {
                return false;
            }
        }
        for (ItemPredicate itemReq : itemRequirements) {
            boolean met = false;
            for (ItemStack item : items) {
                if (itemReq.test(item)) {
                    met = true;
                    break;
                }
            }
            if (!met) {
                return false;
            }
        }
        return true;
    }

    public Holder<RiftTheme> getTheme() {
        return theme;
    }

    public int getPriority() {
        return priority;
    }

    public List<EssencePredicate> getEssenceRequirements() {
        return essenceRequirements;
    }

    public List<ItemPredicate> getItemRequirements() {
        return itemRequirements;
    }

    public static final class Builder {
        private final Holder<RiftTheme> theme;
        private int priority = 0;

        private final List<EssencePredicate> essenceRequirements = new ArrayList<>();
        private final List<ItemPredicate> itemRequirements = new ArrayList<>();

        /**
         * @param theme The theme this recipe is for
         */
        public Builder(Holder<RiftTheme> theme) {
            this.theme = theme;
        }

        /**
         * @param priority The priority of this recipe against other recipes (higher overrides lower)
         * @return
         */
        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Can be specified multiple times, with all requirements needing to be met
         * 
         * @param essenceReq A predicate specifying a requirement on a type of essence that has to be met by the
         *                   ingredients.
         * @return
         */
        public Builder withEssenceReq(EssencePredicate essenceReq) {
            this.essenceRequirements.add(essenceReq);
            return this;
        }

        /**
         * Can be specified multiple times, with all requirements needing to be met
         * 
         * @param itemReq A predicate specifying a requirement on a type of item that has to be met by the ingredients
         * @return
         */
        public Builder withItemReq(ItemPredicate itemReq) {
            this.itemRequirements.add(itemReq);
            return this;
        }

        public ThemeRecipe build() {
            return new ThemeRecipe(theme, priority, essenceRequirements, itemRequirements);
        }
    }
}
