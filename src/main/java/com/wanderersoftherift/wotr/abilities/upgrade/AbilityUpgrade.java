package com.wanderersoftherift.wotr.abilities.upgrade;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An upgrade for an ability.
 */
public final class AbilityUpgrade {

    public static final Codec<Holder<AbilityUpgrade>> REGISTRY_CODEC = RegistryFixedCodec
            .create(RegistryEvents.ABILITY_UPGRADE_REGISTRY);

    public static final Codec<AbilityUpgrade> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("id").forGetter(AbilityUpgrade::id),
                            ResourceLocation.CODEC.fieldOf("icon").forGetter(AbilityUpgrade::icon),
                            Codec.INT.fieldOf("maxCount").forGetter(AbilityUpgrade::maxCount),
                            AbstractModifierEffect.DIRECT_CODEC.listOf()
                                    .optionalFieldOf("effects", Collections.emptyList())
                                    .forGetter(AbilityUpgrade::modifierEffects))
                    .apply(instance, AbilityUpgrade::new));

    private final ResourceLocation id;
    private final ResourceLocation icon;
    private final int maxCount;
    private final List<AbstractModifierEffect> modifierEffects;
    private final Component name;
    private final Component description;

    /**
     * @param id              Id for the upgrade
     * @param icon            Resource location of the upgrade's icon
     * @param maxCount        Maximum instances of this upgrade an ability can have
     * @param modifierEffects A list of modifier effects this upgrade applies
     */
    public AbilityUpgrade(ResourceLocation id, ResourceLocation icon, int maxCount,
            List<AbstractModifierEffect> modifierEffects) {
        this.id = id;
        this.icon = icon;
        this.maxCount = maxCount;
        this.modifierEffects = ImmutableList.copyOf(modifierEffects);
        this.name = Component.translatable("ability_upgrade." + id.getNamespace() + "." + id.getPath() + ".name");
        this.description = Component
                .translatable("ability_upgrade." + id.getNamespace() + "." + id.getPath() + ".description");
    }

    /**
     * @return Display name for the upgrade
     */
    public Component name() {
        return name;
    }

    /**
     * @return Display description for the upgrade
     */
    public Component description() {
        return description;
    }

    /**
     * @return Identifier for the upgrade
     */
    public ResourceLocation id() {
        return id;
    }

    /**
     * @return an icon for the upgrade
     */
    public ResourceLocation icon() {
        return icon;
    }

    /**
     * @return maximum number of instances of this upgrade an ability can have
     */
    public int maxCount() {
        return maxCount;
    }

    /**
     * @return The ModifierEffects this upgrade grants
     */
    public List<AbstractModifierEffect> modifierEffects() {
        return modifierEffects;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AbilityUpgrade) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AbilityUpgrade[" + "id='" + id + "']";
    }

}
