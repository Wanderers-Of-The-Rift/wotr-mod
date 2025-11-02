package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.StoredAbilityContext;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.sources.MainAbilitySource;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OngoingAbilities {

    private static final AttachmentSerializerFromDataCodec<Data, OngoingAbilities> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            Data.CODEC, OngoingAbilities::new, OngoingAbilities::getData);

    private final IAttachmentHolder holder;
    private final List<ActiveAbility> activeAbilities;

    private AbilityContext.Activation currentContextActivation;

    public OngoingAbilities(IAttachmentHolder holder) {
        this(holder, new Data(List.of()));
    }

    private OngoingAbilities(IAttachmentHolder holder, Data data) {
        this.holder = holder;
        activeAbilities = new ArrayList<>(data.activeAbilities);
        if (holder instanceof Entity entity) {
            var registry = entity.level().getData(WotrAttachments.ONGOING_ABILITY_ENTITY_REGISTRY);
            registry.add(entity);
        }
    }

    public static IAttachmentSerializer<Tag, OngoingAbilities> getSerializer() {
        return SERIALIZER;
    }

    public boolean activate(MainAbilitySource source) {
        if (!(holder instanceof LivingEntity entity)) {
            return false;
        }
        Holder<Ability> ability = source.getAbility(entity);
        if (ability == null) {
            return false;
        }
        return activate(source, ability);
    }

    public boolean activate(AbilitySource source, Holder<Ability> ability) {
        return activate(source, ability, (context) -> {
        });
    }

    public boolean activate(AbilitySource source, Holder<Ability> ability, Consumer<AbilityContext> componentProvider) {
        if (!(holder instanceof LivingEntity entity)) {
            return false;
        }
        if (entity.level().isClientSide()) {
            return clientsideActivate(entity, ability, source);
        } else {
            return serversideActivate(entity, ability, source, componentProvider);
        }
    }

    private boolean clientsideActivate(LivingEntity entity, Holder<Ability> ability, AbilitySource source) {
        AbilityContext context = new AbilityContext(ability, entity, source);
        return activateContextFor(context, () -> {
            if (ability.value().canActivate(context)) {
                ability.value().clientActivate(context);
                return true;
            }
            return false;
        });
    }

    private boolean serversideActivate(
            LivingEntity entity,
            Holder<Ability> ability,
            AbilitySource source,
            Consumer<AbilityContext> componentProvider) {
        interruptChannelledAbilities();
        Optional<ActiveAbility> activeAbility = activeAbilities.stream()
                .filter(x -> x.matches(ability, source))
                .findFirst();
        boolean existing = activeAbility.isPresent();
        AbilityContext context;
        if (existing) {
            context = activeAbility.get().createContext(entity);
        } else {
            context = new AbilityContext(ability, entity, source);
        }
        componentProvider.accept(context);
        return activateContextFor(context, () -> {
            context.set(WotrDataComponentType.AbilityContextData.CONDITIONS,
                    AbilityConditions.forEntity(entity).condition(ability));
            if (!ability.value().canActivate(context)) {
                return false;
            }
            boolean finished = ability.value().activate(context);
            if (finished && existing) {
                activeAbilities.removeIf(x -> x.id().equals(context.instanceId()));
            } else if (!finished && !existing) {
                activeAbilities.add(new ActiveAbility(context));
            }
            return true;
        });
    }

    public void tick() {
        if (!(holder instanceof LivingEntity attachedTo)) {
            return;
        }
        for (ActiveAbility instance : ImmutableList.copyOf(activeAbilities)) {
            instance.age++;
            AbilityContext context = instance.createContext(attachedTo);
            activateContextFor(context, () -> {
                if (instance.ability().tick(context)) {
                    activeAbilities.remove(instance);
                }
                return null;
            });
        }
    }

    private Data getData() {
        return new Data(activeAbilities);
    }

    public boolean isEmpty() {
        return activeAbilities.isEmpty();
    }

    public void slotChanged(@NotNull WotrEquipmentSlot slot, ItemStack from, ItemStack to) {
        deactivateIf(x -> slot.equals(x.source().getLinkedSlot()));
    }

    public void interruptChannelledAbilities() {
        deactivateIf(x -> x.ability().isChannelled());
    }

    private void deactivateIf(Predicate<ActiveAbility> predicate) {
        if (!(holder instanceof LivingEntity attachedTo)) {
            return;
        }
        List<ActiveAbility> channelled = activeAbilities.stream().filter(predicate).toList();
        channelled.forEach(instance -> {
            AbilityContext abilityContext = instance.createContext(attachedTo);
            try (var ignored = abilityContext.activate()) {
                instance.ability().deactivate(abilityContext);
            }
        });
        activeAbilities.removeAll(channelled);
    }

    // TODO: Get rid of this after ability context stores attribute state.
    private <T> T activateContextFor(AbilityContext context, Callable<T> action) {
        AbilityContext.Activation previousContext = currentContextActivation;
        if (previousContext != null) {
            previousContext.pause();
        }
        try (var activation = context.activate()) {
            currentContextActivation = activation;
            return action.call();
        } catch (Exception e) {
            WanderersOfTheRift.LOGGER.error("Error occurred processing ability", e);
            return null;
        } finally {
            if (previousContext != null) {
                previousContext.resume();
            }
            currentContextActivation = previousContext;
        }
    }

    private static final class ActiveAbility {

        private static final Codec<ActiveAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                StoredAbilityContext.CODEC.fieldOf("stored_context").forGetter(x -> x.storedContext),
                Codec.LONG.fieldOf("age").forGetter(ActiveAbility::age)
        ).apply(instance, ActiveAbility::new));

        private final StoredAbilityContext storedContext;
        private long age;

        ActiveAbility(AbilityContext context) {
            this.storedContext = new StoredAbilityContext(context);
            this.age = context.age();
        }

        ActiveAbility(StoredAbilityContext context, long age) {
            this.storedContext = context;
            this.age = age;
        }

        boolean matches(Holder<Ability> ability, AbilitySource source) {
            return this.storedContext.ability().equals(ability) && Objects.equals(source, this.storedContext.source());
        }

        AbilityContext createContext(LivingEntity owner) {
            return storedContext.toContext(owner, owner.level(), age);
        }

        long age() {
            return age;
        }

        UUID id() {
            return storedContext.instanceId();
        }

        Ability ability() {
            return storedContext.ability().value();
        }

        AbilitySource source() {
            return storedContext.source();
        }
    }

    private record Data(List<ActiveAbility> activeAbilities) {
        static final Codec<Data> CODEC = ActiveAbility.CODEC.listOf().xmap(Data::new, Data::activeAbilities);
    }
}
