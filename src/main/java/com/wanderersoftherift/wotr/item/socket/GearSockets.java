package com.wanderersoftherift.wotr.item.socket;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.client.tooltip.GearSocketTooltipRenderer;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.ModifierProvider;
import com.wanderersoftherift.wotr.modifier.source.GearSocketModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.wanderersoftherift.wotr.init.WotrTags.Items.SOCKETABLE;

public record GearSockets(List<GearSocket> sockets) implements ModifierProvider {
    public static final Codec<GearSockets> CODEC = RecordCodecBuilder
            .create(inst -> inst.group(GearSocket.CODEC.listOf().fieldOf("sockets").forGetter(GearSockets::sockets))
                    .apply(inst, GearSockets::new));

    public boolean isEmpty() {
        return sockets.isEmpty();
    }

    public static GearSockets generate(int socketCount, RandomSource random) {
        List<GearSocket> sockets = new ArrayList<>();
        for (int i = 0; i < socketCount; i++) {
            GearSocket socket = GearSocket.getRandomSocket(random);
            sockets.add(socket);
        }
        return new GearSockets(sockets);
    }

    public static GearSockets generateWithRange(int minSockets, int maxSockets, RandomSource random) {
        return generate(random.nextIntBetweenInclusive(minSockets, maxSockets), random);
    }

    public static GearSockets emptySockets() {
        return new GearSockets(new ArrayList<>());
    }

    public static void generateForItem(ItemStack itemStack, Level level, int minSockets, int maxSockets) {
        if (level.isClientSide() || !itemStack.is(SOCKETABLE)) {
            return;
        }
        // check if gear already has sockets
        if (itemStack.get(WotrDataComponentType.GEAR_SOCKETS) != null) {
            return;
        }
        GearSockets sockets = GearSockets.generateWithRange(minSockets, maxSockets, level.random);
        itemStack.set(WotrDataComponentType.GEAR_SOCKETS, sockets);
    }

    @Override
    public Stream<ModifierEntry> modifiers(ItemStack stack, WotrEquipmentSlot slot, LivingEntity entity) {
        List<GearSocket> sockets = sockets();
        return IntStream.range(0, sockets.size()).mapToObj(idx -> {
            GearSocket socket = sockets.get(idx);
            if (socket.isEmpty()) {
                return null;
            }
            ModifierInstance modifierInstance = socket.modifier().get();
            Holder<Modifier> modifier = modifierInstance.modifier();
            if (modifier == null) {
                return null;
            }
            ModifierSource source = new GearSocketModifierSource(slot, idx);
            return new ModifierEntry(modifierInstance, source);
        }).filter(Objects::nonNull);
    }

    @Override
    public List<Either<FormattedText, TooltipComponent>> tooltips(int maxWidth) {
        return List.of(Either.right(new GearSocketTooltipRenderer.GearSocketComponent(sockets())));
    }
}
