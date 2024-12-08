package dev.rndmorris.tfixins.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockPlankItem extends ItemBlock {

    public BlockPlankItem(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return stack.getItemDamage() == 0 ? "tile.blockWoodenDevice.6" : "tile.blockWoodenDevice.7";
    }
}
