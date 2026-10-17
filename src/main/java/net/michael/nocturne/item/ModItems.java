package net.michael.nocturne.item;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.item.custom.WhipItem;
import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class   ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Nocturne.MOD_ID);

    //registers the whip item
    public static final RegistryObject<Item>  WHIP = ITEMS.register("whip", () -> new WhipItem(new Item.Properties().durability(500)));
    //registers BloodStone item
    public static final RegistryObject<Item> BLOODSTONE = ITEMS.register("bloodstone", () -> new Item(new Item.Properties()));


    public static void register (IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
