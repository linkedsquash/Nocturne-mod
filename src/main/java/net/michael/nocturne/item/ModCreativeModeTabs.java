package net.michael.nocturne.item;

import net.michael.nocturne.Nocturne;
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

    public static final RegistryObject<CreativeModeTab> CASTLEVANIA_TAB = CREATIVE_MODE_TABS.register("castlevania_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WHIP.get()))
                    .title(Component.translatable("creativetab.castlevania_tab"))
                    .displayItems((pParameter, pOutput) -> {
                        pOutput.accept(ModItems.WHIP.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
