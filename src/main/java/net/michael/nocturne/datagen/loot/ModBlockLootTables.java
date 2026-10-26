package net.michael.nocturne.datagen.loot;

import net.michael.nocturne.block.ModBlocks;
import net.michael.nocturne.item.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.MAGIC_BLOCK.get());
        this.dropSelf(ModBlocks.BLOODSTONE_BLOCK.get());

        this.add(ModBlocks.BLOODSTONE_ORE.get(),
              block -> createOreDrop(ModBlocks.BLOODSTONE_ORE.get(), ModItems.BLOODSTONE.get()));

        this.add(ModBlocks.TIN_ORE.get(),
                block -> createOreDrop(ModBlocks.TIN_ORE.get(), ModItems.RAW_TIN.get()));
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
