package net.michael.nocturne.tag;

import net.michael.nocturne.Nocturne;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTags {

    public static class Blocks {

        public static final TagKey<Block> BLOCKS = tag("BLOCKS");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Nocturne.MOD_ID, name));
        };
    }
    public static class Items {

        public static final TagKey<Item> WHIPS = tag("whips");
        };

        public static final TagKey<Item> TIN = tag("tin");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(Nocturne.MOD_ID, name));
        };
    }
