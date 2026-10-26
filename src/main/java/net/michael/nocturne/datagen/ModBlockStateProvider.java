package net.michael.nocturne.datagen;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper){
        super(output, Nocturne.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.BLOODSTONE_BLOCK);
        blockWithItem(ModBlocks.BLOODSTONE_ORE);
        blockWithItem(ModBlocks.TIN_ORE);

        blockWithItem(ModBlocks.MAGIC_BLOCK);
    }
    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
