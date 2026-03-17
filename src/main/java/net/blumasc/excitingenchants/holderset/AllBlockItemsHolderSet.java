package net.blumasc.excitingenchants.holderset;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.holderset.ModHolderSetTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AllBlockItemsHolderSet implements ICustomHolderSet<Item> {
    public static final MapCodec<AllBlockItemsHolderSet> CODEC = MapCodec.unit(AllBlockItemsHolderSet::new);

    private final List<Holder<Item>> holders;

    public AllBlockItemsHolderSet() {
        this.holders = BuiltInRegistries.ITEM.holders()
                .filter(h -> h.value() instanceof BlockItem)
                .map(h -> (Holder<Item>) h)
                .toList();
    }

    @Override
    public HolderSetType type() {
        return ModHolderSetTypes.ALL_BLOCK_ITEMS.get();
    }

    @Override
    public Stream<Holder<Item>> stream() {
        return holders.stream();
    }

    @Override
    public int size() {
        return holders.size();
    }

    @Override
    public Either<TagKey<Item>, List<Holder<Item>>> unwrap() {
        return Either.right(holders);
    }

    @Override
    public Optional<Holder<Item>> getRandomElement(RandomSource randomSource) {
        if (holders.isEmpty()) return Optional.empty();
        return Optional.of(holders.get(randomSource.nextInt(holders.size())));
    }

    @Override
    public Holder<Item> get(int i) {
        return holders.get(i);
    }

    @Override
    public boolean contains(Holder<Item> holder) {
        return holder.value() instanceof BlockItem;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<Item> holderOwner) {
        return true;
    }

    @Override
    public Optional<TagKey<Item>> unwrapKey() {
        return Optional.empty();
    }

    @Override
    public @NotNull Iterator<Holder<Item>> iterator() {
        return holders.iterator();
    }
}