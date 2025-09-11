package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.ability.ManaChangePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collections;
import java.util.Map;

/**
 * Data tracking the state of a player's mana pool
 */
public class AbilityResourceData {

    private static final Codec<Map<Holder<AbilityResource>, Float>> DATA_CODEC = Codec
            .unboundedMap(AbilityResource.HOLDER_CODEC, Codec.FLOAT);

    private static final AttachmentSerializerFromDataCodec<Map<Holder<AbilityResource>, Float>, AbilityResourceData> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            DATA_CODEC, AbilityResourceData::new, AbilityResourceData::getAmounts);

    private final IAttachmentHolder holder;
    private Object2FloatMap<Holder<AbilityResource>> amounts = new Object2FloatLinkedOpenHashMap<>();

    public AbilityResourceData(IAttachmentHolder holder) {
        this(holder, Collections.emptyMap());
    }

    public AbilityResourceData(IAttachmentHolder holder, Map<Holder<AbilityResource>, Float> amounts) {
        this.holder = holder;
        this.amounts.putAll(amounts);
        if (holder instanceof Entity entity) {
            entity.level().getData(WotrAttachments.MANA_ENTITY_REGISTRY).add(entity);
        }
    }

    public static IAttachmentSerializer<Tag, AbilityResourceData> getSerializer() {
        return SERIALIZER;
    }

    /**
     * @return The amount of mana available
     */
    public float getAmount(Holder<AbilityResource> resource) {
        return amounts.getFloat(resource);
    }

    public Map<Holder<AbilityResource>, Float> getAmounts() {
        return amounts;
    }

    /**
     * Consumes an amount of mana (will not consume past 0). This will be replicated to the player if on the server.
     * 
     * @param quantity The quantity of mana to consume
     */
    public void useAmount(Holder<AbilityResource> resource, float quantity) {
        if (quantity == 0) {
            return;
        }
        setAmount(resource, getAmount(resource) - quantity);
    }

    /**
     * Sets the amount of mana in the pool. This will be replicated to the player if on the server.
     * 
     * @param value The new value of mana
     */
    public void setAmount(Holder<AbilityResource> resource, float value) {
        var amount = Math.clamp(value, 0, resource.value().maxForEntity(holder));
        this.amounts.put(resource, amount);

        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new ManaChangePayload(resource, amount));
        }

    }

    // Regenerates and/or degenerates the pool.
    public void tick() {
        if (!(holder instanceof LivingEntity entity)) {
            return;
        }

        amounts.forEach((key, value) -> {
            amounts.put(key, key.value().tickForEntity(holder, value));
        });
        /*
         * float delta = 0; AttributeInstance manaRegenAttribute = entity.getAttribute(WotrAttributes.MANA_REGEN_RATE);
         * if (manaRegenAttribute != null) { delta += (float) manaRegenAttribute.getValue(); } AttributeInstance
         * manaDegenAttribute = entity.getAttribute(WotrAttributes.MANA_DEGEN_RATE); if (manaDegenAttribute != null) {
         * delta -= (float) manaDegenAttribute.getValue(); } amount = Math.clamp(amount + delta, 0, maxAmount());
         */
    }
}
