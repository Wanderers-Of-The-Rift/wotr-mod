package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModAttributes;
import com.wanderersoftherift.wotr.network.ManaChangePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Data tracking the state of a player's mana pool
 */
public class ManaData {
    public static final Codec<ManaData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.INT.fieldOf("amount").forGetter(ManaData::getAmount),
                    Codec.DOUBLE.fieldOf("fractionalRegen").forGetter(x -> x.fractionalRegen),
                    Codec.DOUBLE.fieldOf("fractionalDegen").forGetter(x -> x.fractionalDegen))
            .apply(instance, ManaData::new));

    private int amount = 0;
    private double fractionalRegen = 0;
    private double fractionalDegen = 0;

    public ManaData() {
    }

    private ManaData(int amount, double fractionalRegen, double fractionalDegen) {
        this.amount = amount;
        this.fractionalRegen = fractionalRegen;
        this.fractionalDegen = fractionalDegen;
    }

    /**
     *
     * @return The amount of mana available
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Consumes an amount of mana (will not consume past 0). This will be replicated to the player if on the server.
     * 
     * @param owner    The owner of the mana pool
     * @param quantity The quantity of mana to consume
     */
    public void useAmount(Player owner, int quantity) {
        setAmount(owner, amount - quantity);
    }

    /**
     * Sets the amount of mana in the pool. This will be replicated to the player if on the server.
     * 
     * @param owner The owner of the mana pool
     * @param value The new value of mana
     */
    public void setAmount(Player owner, int value) {
        if (value == amount) {
            return;
        }
        this.amount = Math.max(0, value);
        if (owner.level().isClientSide) {
            return;
        }
        PacketDistributor.sendToPlayer((ServerPlayer) owner, new ManaChangePayload(amount));
    }

    // Regenerates and/or degenerates the pool.
    public void tick(LivingEntity entity) {
        int maxMana = (int) entity.getAttributeValue(ModAttributes.MAX_MANA);
        if (amount > maxMana) {
            amount = maxMana;
        }
        if (amount < maxMana) {
            tickRegen(entity, maxMana);
        }
        if (amount > 0) {
            tickDegen(entity);
        }
    }

    private void tickRegen(LivingEntity entity, int maxMana) {
        AttributeInstance manaRegenAttribute = entity.getAttribute(ModAttributes.MANA_REGEN_RATE);
        if (manaRegenAttribute == null) {
            return;
        }
        fractionalRegen += manaRegenAttribute.getValue();
        amount = Math.min(maxMana, amount + (int) fractionalRegen);
        fractionalRegen = fractionalRegen % 1.0;
    }

    private void tickDegen(LivingEntity entity) {
        AttributeInstance manaDegenAttribute = entity.getAttribute(ModAttributes.MANA_DEGEN_RATE);
        if (manaDegenAttribute == null) {
            return;
        }
        fractionalDegen += manaDegenAttribute.getValue();
        amount = Math.max(0, amount - (int) fractionalDegen);
        fractionalDegen = fractionalDegen % 1.0;
    }

}
