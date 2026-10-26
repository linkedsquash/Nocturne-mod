package net.michael.nocturne.datagen;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Nocturne.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        simpleItem(ModItems.BLOOD_PEACH);
        simpleItem(ModItems.WHIP);
        simpleItem(ModItems.EXPLOSIVE_STONE);
        simpleItem(ModItems.TIN_INGOT);
        simpleItem(ModItems.RAW_TIN);
        simpleItem(ModItems.BRONZE_INGOT);


    }
    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Nocturne.MOD_ID,"item/" + item.getId().getPath()));
    }
}
