package dev.rndmorris.salisarcana.common.item;

import static dev.rndmorris.salisarcana.SalisArcana.MODID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.rndmorris.salisarcana.config.ConfigModuleRoot;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;

/**
 * Placeholder item class, for use in the Thaumonomicon
 */
public abstract class PlaceholderItem extends Item {

    public static PlaceholderItem capPlaceholder;
    public static PlaceholderItem rodPlaceholder;

    public static void registerPlaceholders() {
        if (ConfigModuleRoot.enhancements.replaceWandCapsSettings.isEnabled()) {
            GameRegistry.registerItem(capPlaceholder = new WandCapPlaceholderItem(), MODID + ":capPlaceholder");
        }
        if (ConfigModuleRoot.enhancements.replaceWandCoreSettings.isEnabled()) {
            GameRegistry.registerItem(rodPlaceholder = new WandRodPlaceholderItem(), MODID + ":rodPlaceholder");
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] cachedIcons;
    private ItemStack[] baseItemCache;

    public PlaceholderItem() {
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    private ItemStack[] baseItemCache() {
        if (baseItemCache == null) {
            baseItemCache = getBaseItems().toArray(ItemStack[]::new);
        }
        return baseItemCache;
    }

    public List<ItemStack> getAllBaseItems() {
        final var result = new ArrayList<ItemStack>();
        Collections.addAll(result, baseItemCache());
        return result;
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] cachedIcons() {
        if (cachedIcons == null) {
            cachedIcons = Arrays.stream(baseItemCache())
                .map(itemStack -> {
                    Item item;
                    if (itemStack == null || (item = itemStack.getItem()) == null) {
                        return null;
                    }
                    return item.getIconFromDamage(itemStack.getItemDamage());
                })
                .filter(Objects::nonNull)
                .toArray(IIcon[]::new);
        }
        return cachedIcons;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        final var cachedIcons = cachedIcons();
        if (0 <= damage && damage < cachedIcons.length) {
            return cachedIcons[damage];
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> outputList) {
        final var cachedIcons = cachedIcons();
        for (var index = 0; index < cachedIcons.length; ++index) {
            outputList.add(new ItemStack(item, 0, index));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack forItemStack) {
        final var damage = forItemStack.getItemDamage();
        final var cachedItems = baseItemCache();
        if (0 <= damage && damage < cachedItems.length) {
            return cachedItems[damage].getUnlocalizedName();
        }
        return null;
    }

    /**
     * All items that this placeholder represents
     */
    protected abstract Stream<ItemStack> getBaseItems();

    public static class WandCapPlaceholderItem extends PlaceholderItem {

        @Override
        public Stream<ItemStack> getBaseItems() {
            return WandCap.caps.values()
                .stream()
                .map(WandCap::getItem);
        }
    }

    public static class WandRodPlaceholderItem extends PlaceholderItem {

        @Override
        public Stream<ItemStack> getBaseItems() {
            return WandRod.rods.values()
                .stream()
                .map(WandRod::getItem);
        }
    }

}
