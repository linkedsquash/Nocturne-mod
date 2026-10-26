package net.michael.nocturne.block;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;

public class ModBlocks {
    // Store this mod's block registrations until Forge processes them during startup.
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Nocturne.MOD_ID);

        // Register a block that copies the strength and other basic properties of a diamond block.
        public static final RegistryObject<Block> BLOODSTONE_BLOCK = registerBlock("blood_block",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).strength(2.0F).requiresCorrectToolForDrops()));

        // Register an ore block that drops between 3 and 6 experience when mined correctly.
        public static  final RegistryObject<Block> BLOODSTONE_ORE = registerBlock("bloodstone_ore",
                () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2f).requiresCorrectToolForDrops(), UniformInt.of(3,6)));

        public static  final RegistryObject<Block> MAGIC_BLOCK = registerBlock("magic_block",
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
