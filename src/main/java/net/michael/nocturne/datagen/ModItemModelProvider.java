package net.michael.nocturne.datagen;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.block.ModBlocks;
import net.michael.nocturne.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Nocturne.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        simpleItem(ModItems.BLOODSTONE);
        simpleItem(ModItems.BLOOD_PEACH);
        simpleItem(ModItems.BELMONT_WHIP);
       // simpleItem(ModItems.WHIP);
        simpleItem(ModItems.EXPLOSIVE_STONE);
        simpleItem(ModItems.TIN_INGOT);
        simpleItem(ModItems.RAW_TIN);
        simpleItem(ModItems.BRONZE_INGOT);

        simpleBlockItem(ModBlocks.BLOODSTONE_DOOR);


        evenSimplerBlockItem(ModBlocks.BLOODSTONE_STAIRS);
        evenSimplerBlockItem(ModBlocks.BLOODSTONE_SLAB);
        evenSimplerBlockItem(ModBlocks.BLOODSTONE_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.BLOODSTONE_FENCE_GATE);

        fenceItem(ModBlocks.BLOODSTONE_FENCE,ModBlocks.BLOODSTONE_BLOCK);
        buttonItem(ModBlocks.BLOODSTONE_BUTTON,ModBlocks.BLOODSTONE_BLOCK);
        wallItem(ModBlocks.BLOODSTONE_WALL, ModBlocks.BLOODSTONE_BLOCK);

        trapdoorItem(ModBlocks.BLOODSTONE_TRAPDOOR);



    }
    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(Nocturne.MOD_ID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Nocturne.MOD_ID,"item/" + item.getId().getPath()));
    }

    public void trapdoorItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath() + "_bottom"));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(Nocturne.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  new ResourceLocation(Nocturne.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(Nocturne.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Nocturne.MOD_ID,"item/" + item.getId().getPath()));
    }
}
