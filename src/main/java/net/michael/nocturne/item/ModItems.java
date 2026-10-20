package net.michael.nocturne.item;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.item.custom.WhipItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class   ModItems {
    // Hold this mod's item registrations until Forge processes them during startup.
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Nocturne.MOD_ID);

    //registers the whip item
    // Create a whip item with 500 durability points before it breaks.
    public static final RegistryObject<Item>  WHIP = ITEMS.register("whip", () -> new WhipItem(new Item.Properties().durability(500)));
    //registers BloodStone item
    // Create a basic item for the bloodstone resource.
    public static final RegistryObject<Item> BLOODSTONE = ITEMS.register("bloodstone", () -> new Item(new Item.Properties()));


    public static void register (IEventBus eventBus){
        // Attach item registration to Forge's startup event bus.
        ITEMS.register(eventBus);
    }
}
