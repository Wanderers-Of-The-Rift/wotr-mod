package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
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
    private final Map<AbilitySource, LongRange> cooldowns;

    public AbilityCooldowns(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private AbilityCooldowns(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        cooldowns = new LinkedHashMap<>();
        if (data != null && holder instanceof Entity entity) {
            long gameTime = getGameTime() * 1000;
            for (var entry : data.cooldowns) {
                cooldowns.put(entry.source, entry.range.offset(gameTime));
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

        long gameTime = getGameTime() * 1000;
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
     * @return The cooldown range for the given slot, in ticks (for comparison with gameTime)
     */
    public LongRange getCooldown(AbilitySource slot) {
        return cooldowns.getOrDefault(slot, LongRange.EMPTY);
    }

    /**
     * @return A stream over all cooldowns
     */
    public Stream<CooldownInfo> getCooldowns() {
        return cooldowns.entrySet()
                .stream()
                .filter(x -> x.getValue().to() > getGameTime() * 1000)
                .map(x -> new CooldownInfo(x.getKey(), x.getValue()));
    }

    /**
     * @param source
     * @return Whether the given slot is on cooldown
     */
    public boolean isOnCooldown(AbilitySource source) {
        return isOnCooldown(source, 1000);
    }

    /**
     * @param source
     * @param margin how far in advance is the ability allowed to activate
     * @return Whether the given slot is on cooldown
     */
    public boolean isOnCooldown(AbilitySource source, int margin) {
        return remainingCooldown(source) > margin;
    }

    public long remainingCooldown(AbilitySource source) {
        var range = cooldowns.getOrDefault(source, LongRange.EMPTY);
        if (range.to() == range.from() || range.to() < 0) {
            return 0;
        }
        return Math.max(0, range.to() - getGameTime() * 1000);
    }

    /**
     * @param source
     * @param from   In milliticks, in terms of gameTime
     * @param until  In milliticks, in terms of gameTime
     */
    public void setCooldown(AbilitySource source, long from, long until) {
        cooldowns.put(source, new LongRange(from, until));
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new AbilityCooldownUpdatePayload(source, from, until));
        }
    }

    /**
     *
     * @param source
     * @param length In milliticks
     */
    public void setCooldown(AbilitySource source, int length) {
        long gameTime = getGameTime() * 1000;
        setCooldown(source, gameTime, gameTime + length);
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

    /**
     *
     * @param source
     * @param range  The range of the cooldown, in milliticks (comparable with gametime)
     */
    public record CooldownInfo(AbilitySource source, LongRange range) {
        private static final Codec<CooldownInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AbilitySource.DIRECT_CODEC.fieldOf("slot").forGetter(CooldownInfo::source),
                LongRange.CODEC.fieldOf("time_range").forGetter(CooldownInfo::range)
        ).apply(instance, CooldownInfo::new));
    }

    private record Data(List<CooldownInfo> cooldowns) {
        public static final Codec<Data> CODEC = CooldownInfo.CODEC.listOf().xmap(Data::new, Data::cooldowns);
    }
}
