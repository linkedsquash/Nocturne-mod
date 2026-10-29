package net.michael.nocturne.item;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.item.custom.BloodPeachItem;
import net.michael.nocturne.item.custom.FuelItem;
import net.michael.nocturne.item.custom.WhipItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class   ModItems {
    // Hold this mod's item registrations until Forge processes them during startup.
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Nocturne.MOD_ID);

    public static final RegistryObject<Item> BELMONT_WHIP = ITEMS.register("belmont_whip", () -> new WhipItem(new Item.Properties().durability(1000)));

    public static final RegistryObject<Item>  WHIP = ITEMS.register("whip", () -> new WhipItem(new Item.Properties().durability(200)));

    public static final RegistryObject<Item> BLOODSTONE = ITEMS.register("bloodstone", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BLOOD_PEACH = ITEMS.register("blood_peach", () -> new BloodPeachItem(new Item.Properties().food(ModFoods.BLOOD_PEACH)));

    public static final RegistryObject<Item> EXPLOSIVE_STONE = ITEMS.register("explosive_stone", () -> new FuelItem(new Item.Properties(), 3200));

    public static final RegistryObject<Item> RAW_TIN = ITEMS.register("raw_tin", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", () -> new Item(new Item.Properties()));






    public static void register (IEventBus eventBus){
        // Attach item registration to Forge's startup event bus.
        ITEMS.register(eventBus);
    }
}
