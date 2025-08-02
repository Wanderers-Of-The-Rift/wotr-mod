package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.network.ability.ManaChangePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Data tracking the state of a player's mana pool
 */
public class ManaData {

    private static final AttachmentSerializerFromDataCodec<Float, ManaData> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            Codec.FLOAT, ManaData::new, ManaData::getAmount);

    private final IAttachmentHolder holder;
    private float amount;

    public ManaData(IAttachmentHolder holder) {
        this(holder, (holder instanceof LivingEntity entity) ? (float) entity.getAttributeValue(WotrAttributes.MAX_MANA)
                : 0);
    }

    public ManaData(IAttachmentHolder holder, float amount) {
        this.holder = holder;
        this.amount = amount;
    }

    public static IAttachmentSerializer<Tag, ManaData> getSerializer() {
        return SERIALIZER;
    }

    /**
     * @return The amount of mana available
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Consumes an amount of mana (will not consume past 0). This will be replicated to the player if on the server.
     * 
     * @param quantity The quantity of mana to consume
     */
    public void useAmount(float quantity) {
        setAmount(amount - quantity);
    }

    /**
     * Sets the amount of mana in the pool. This will be replicated to the player if on the server.
     * 
     * @param value The new value of mana
     */
    public void setAmount(float value) {
        this.amount = Math.clamp(value, 0, maxAmount());
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new ManaChangePayload(amount));
        }

    }

    public float maxAmount() {
        if (!(holder instanceof LivingEntity entity)) {
            return 0;
        }
        return (float) entity.getAttributeValue(WotrAttributes.MAX_MANA);
    }

    // Regenerates and/or degenerates the pool.
    public void tick() {
        if (!(holder instanceof LivingEntity entity)) {
            return;
        }
        float maxMana = maxAmount();
        if (amount < maxMana) {
            tickRegen(entity, maxMana);
        }
        if (amount > 0) {
            tickDegen(entity);
        }
    }

    private void tickRegen(LivingEntity entity, float maxMana) {
        AttributeInstance manaRegenAttribute = entity.getAttribute(WotrAttributes.MANA_REGEN_RATE);
        if (manaRegenAttribute == null) {
            return;
        }
        amount = Math.min((float) (amount + manaRegenAttribute.getValue()), maxMana);
    }

    private void tickDegen(LivingEntity entity) {
        AttributeInstance manaDegenAttribute = entity.getAttribute(WotrAttributes.MANA_DEGEN_RATE);
        if (manaDegenAttribute == null) {
            return;
        }
        amount = Math.max((float) (amount - manaDegenAttribute.getValue()), 0);
    }

}
