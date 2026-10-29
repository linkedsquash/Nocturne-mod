package net.michael.nocturne.datagen;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
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
        blockWithItem(ModBlocks.TIN_BLOCK);
        blockWithItem(ModBlocks.BRONZE_BLOCK);
        blockWithItem(ModBlocks.MAGIC_BLOCK);

        stairsBlock(((StairBlock) ModBlocks.BLOODSTONE_STAIRS.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.BLOODSTONE_SLAB.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));


        buttonBlock(((ButtonBlock) ModBlocks.BLOODSTONE_BUTTON.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.BLOODSTONE_PRESSURE_PLATE.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));

        fenceBlock(((FenceBlock) ModBlocks.BLOODSTONE_FENCE.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.BLOODSTONE_FENCE_GATE.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));
        wallBlock(((WallBlock) ModBlocks.BLOODSTONE_WALL.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));


        doorBlockWithRenderType(((DoorBlock) ModBlocks.BLOODSTONE_DOOR.get()),modLoc("block/bloodstone_door_bottom"), modLoc("block/bloodstone_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.BLOODSTONE_TRAPDOOR.get()),modLoc("block/bloodstone_trapdoor"), true,"cutout");



    }
    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
