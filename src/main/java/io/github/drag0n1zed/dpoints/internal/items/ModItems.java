package io.github.drag0n1zed.dpoints.internal.items;

import io.github.drag0n1zed.dpoints.internal.Points;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Points.MODID);

    public static final RegistryObject<Item> POINTS = ITEMS.register("points",
            () -> new Item(new Item.Properties()));
}
