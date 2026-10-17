package net.michael.nocturne;

import net.michael.nocturne.block.ModBlocks;
import net.michael.nocturne.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Nocturne.MOD_ID);

    public static final RegistryObject<CreativeModeTab> NOCTURNE_TAB = CREATIVE_MODE_TABS.register("nocturne_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BLOODSTONE.get()))
                    .title(Component.translatable("creativetab.nocturne_tab"))
                    .displayItems((pParameter, pOutput) -> {
                        pOutput.accept(ModItems.WHIP.get());
                        pOutput.accept(ModItems.BLOODSTONE.get());

                        pOutput.accept(ModBlocks.BLOOD_BLOCK.get());
                        pOutput.accept(ModBlocks.BLOODSTONE_ORE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
