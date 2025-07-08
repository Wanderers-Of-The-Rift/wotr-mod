package com.wanderersoftherift.wotr.serialization;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public record AttachmentSerializerFromDataCodec<D, A>(Codec<D> codec,
        BiFunction<IAttachmentHolder, D, A> attachmentConstructor, Function<A, D> dataGetter)
        implements IAttachmentSerializer<Tag, A> {

    @Override
    public A read(IAttachmentHolder iAttachmentHolder, Tag tag, HolderLookup.Provider provider) {
        return attachmentConstructor.apply(iAttachmentHolder,
                codec.decode(provider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow().getFirst());
    }

    @Override
    public @Nullable Tag write(A attachment, HolderLookup.Provider provider) {
        return codec.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), dataGetter().apply(attachment))
                .getOrThrow();
    }
}
