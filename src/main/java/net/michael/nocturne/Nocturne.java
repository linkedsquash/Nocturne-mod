package net.michael.nocturne;

import net.michael.nocturne.block.ModBlocks;
import net.michael.nocturne.item.ModCreativeModeTabs;
import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.network.ModPacketHandler;
import net.michael.nocturne.world.entity.ModEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Nocturne.MOD_ID)
public class Nocturne {
    // Minecraft uses this ID to find this mod's registrations and resources.
    public static final String MOD_ID = "nocturne";

    // Forge calls this constructor when it loads the mod.
    public Nocturne(FMLJavaModLoadingContext context) {
        ModPacketHandler.register();

        // Get the event bus used to register this mod's content during startup.
        IEventBus modEventBus = context.getModEventBus();
        // Register each group so Minecraft knows about the mod's entities, items, blocks, and creative tab.
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModPacketHandler.register();

    }
}
