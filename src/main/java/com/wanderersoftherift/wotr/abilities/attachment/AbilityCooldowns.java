package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.network.ability.AbilityCooldownUpdatePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import com.wanderersoftherift.wotr.util.LongRange;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Attachment for tracking ability cooldowns of an entity
 */
public class AbilityCooldowns {

    private static final AttachmentSerializerFromDataCodec<Data, AbilityCooldowns> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            Data.CODEC, AbilityCooldowns::new, AbilityCooldowns::data);

    private final IAttachmentHolder holder;
    private final Map<WotrEquipmentSlot, LongRange> cooldowns;

    public AbilityCooldowns(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private AbilityCooldowns(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        cooldowns = new LinkedHashMap<>();
        if (data != null && holder instanceof Entity entity) {
            long gameTime = entity.level().getGameTime();
            for (var entry : data.cooldowns) {
                cooldowns.put(entry.slot, entry.range.offset(gameTime));
            }
        }
    }

    /// Serialization

    public static IAttachmentSerializer<Tag, AbilityCooldowns> getSerializer() {
        return SERIALIZER;
    }

    private Data data() {
        if (!(holder instanceof Entity entity)) {
            return new Data(List.of());
        }

        long gameTime = entity.level().getGameTime();
        List<CooldownInfo> cooldownData = cooldowns.entrySet()
                .stream()
                .filter(x -> x.getValue().contains(gameTime))
                .map(x -> new CooldownInfo(x.getKey(), x.getValue().offset(-gameTime)))
                .toList();
        return new Data(cooldownData);
    }

    /// Access

    /**
     * @param slot
     * @return The cooldown range for the given slot
     */
    public LongRange getCooldown(WotrEquipmentSlot slot) {
        return cooldowns.getOrDefault(slot, LongRange.EMPTY);
    }

    /**
     * @return A stream over all cooldowns
     */
    public Stream<CooldownInfo> getCooldowns() {
        return cooldowns.entrySet()
                .stream()
                .filter(x -> x.getValue().to() > getGameTime())
                .map(x -> new CooldownInfo(x.getKey(), x.getValue()));
    }

    /**
     * @param slot
     * @return Whether the given slot is on cooldown
     */
    public boolean isOnCooldown(WotrEquipmentSlot slot) {
        return cooldowns.getOrDefault(slot, LongRange.EMPTY).to() > getGameTime();
    }

    public void setCooldown(WotrEquipmentSlot slot, long from, long until) {
        cooldowns.put(slot, new LongRange(from, until));
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new AbilityCooldownUpdatePayload(slot, from, until));
        }
    }

    public void setCooldown(WotrEquipmentSlot slot, int length) {
        long gameTime = getGameTime();
        setCooldown(slot, gameTime, gameTime + length);
    }

    /**
     * Removes all cooldowns
     */
    public void clear() {
        cooldowns.clear();
    }

    private long getGameTime() {
        if (!(holder instanceof Entity entity)) {
            return 0;
        }
        return entity.level().getGameTime();
    }

    public record CooldownInfo(WotrEquipmentSlot slot, LongRange range) {
        private static final Codec<CooldownInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                WotrEquipmentSlot.DIRECT_CODEC.fieldOf("slot").forGetter(CooldownInfo::slot),
                LongRange.CODEC.fieldOf("time_range").forGetter(CooldownInfo::range)
        ).apply(instance, CooldownInfo::new));
    }

    private record Data(List<CooldownInfo> cooldowns) {
        public static final Codec<Data> CODEC = CooldownInfo.CODEC.listOf().xmap(Data::new, Data::cooldowns);
    }
}
