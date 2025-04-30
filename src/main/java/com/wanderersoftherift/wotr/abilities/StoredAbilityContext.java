package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * A serializable form of an {@link AbilityContext} - this largely deals with the caster being stored as a UUID and
 * needing to be looked up in the level
 */
public class StoredAbilityContext {
    public static final Codec<StoredAbilityContext> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(UUIDUtil.CODEC.fieldOf("caster").forGetter(x -> x.casterId),
                    ItemStack.OPTIONAL_CODEC.fieldOf("abilityItem").forGetter(x -> x.abilityItem)

            ).apply(instance, StoredAbilityContext::new));

    private final UUID casterId;
    private final ItemStack abilityItem;

    public StoredAbilityContext(AbilityContext context) {
        this.casterId = context.caster().getUUID();
        this.abilityItem = context.abilityItem();
    }

    public StoredAbilityContext(UUID casterId, ItemStack abilityItem) {
        this.casterId = casterId;
        this.abilityItem = abilityItem;
    }

    public AbilityContext toContext(LivingEntity caster) {
        return new AbilityContext(caster, abilityItem);
    }

    public UUID getCasterId() {
        return casterId;
    }
}
