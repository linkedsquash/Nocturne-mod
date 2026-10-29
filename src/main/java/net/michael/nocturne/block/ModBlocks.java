package net.michael.nocturne.block;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;
import java.util.function.Supplier;

public class ModBlocks {
    // Store this mod's block registrations until Forge processes them during startup.
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Nocturne.MOD_ID);

        // Register a block that copies the strength and other basic properties of a diamond block.
        public static final RegistryObject<Block> BLOODSTONE_BLOCK = registerBlock("bloodstone_block",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(2.0F).requiresCorrectToolForDrops()));

        //slab and stairs
     public static final RegistryObject<Block> BLOODSTONE_STAIRS = registerBlock("bloodstone_stairs",
            () -> new StairBlock(() -> ModBlocks.BLOODSTONE_BLOCK.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> BLOODSTONE_SLAB = registerBlock("bloodstone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    //redstone blocks
    public static final RegistryObject<Block> BLOODSTONE_BUTTON = registerBlock("bloodstone_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON), BlockSetType.OAK, 20, true));
    public static final RegistryObject<Block> BLOODSTONE_PRESSURE_PLATE = registerBlock("bloodstone_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BlockSetType.IRON));

    // wall and fences
    public static final RegistryObject<Block> BLOODSTONE_FENCE = registerBlock("bloodstone_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> BLOODSTONE_FENCE_GATE = registerBlock("bloodstone_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), SoundEvents.CHAIN_PLACE, SoundEvents.CHAIN_BREAK));
    public static final RegistryObject<Block> BLOODSTONE_WALL = registerBlock("bloodstone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    public static final RegistryObject<Block> BLOODSTONE_DOOR = registerBlock("bloodstone_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<Block> BLOODSTONE_TRAPDOOR = registerBlock("bloodstone_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion(), BlockSetType.IRON));


        // Register an ore block that drops between 3 and 6 experience when mined correctly.
        public static  final RegistryObject<Block> BLOODSTONE_ORE = registerBlock("bloodstone_ore",
                () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).requiresCorrectToolForDrops(), UniformInt.of(1,1)));

        public static  final RegistryObject<Block> MAGIC_BLOCK = registerBlock("magic_block",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2.0F).requiresCorrectToolForDrops()));

    public static  final RegistryObject<Block> TIN_ORE = registerBlock("tin_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).requiresCorrectToolForDrops(), UniformInt.of(2,5)));

    public static  final RegistryObject<Block> TIN_BLOCK = registerBlock("tin_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2.0F).requiresCorrectToolForDrops()));

    public static  final RegistryObject<Block> BRONZE_BLOCK = registerBlock("bronze_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2.0F).requiresCorrectToolForDrops()));



    // Register a block and its matching inventory item, then return the block registration.
        private static <T extends  Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
            // Ask Forge to create the block when registrations are processed.
            RegistryObject<T> toReturn = BLOCKS.register(name, block);
            // Also make a placeable item so players can hold and place this block.
            registerBlockItem(name, toReturn);
            return  toReturn;
        }

        // Register the inventory item that places the given block in the world.
        private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
                return ModItems.ITEMS.register(name,  () -> new BlockItem(block.get(), new Item.Properties()));
        }





        public static void  register(IEventBus eventBus){
            // Attach this block register to Forge's startup event bus.
            BLOCKS.register(eventBus);
        }

}
