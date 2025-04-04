package dev.rndmorris.salisarcana.lib;

import java.util.HashSet;
import java.util.Stack;

import net.glease.tc4tweak.api.BrowserPagingAPI;
import net.glease.tc4tweak.api.TC4TweaksAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.Tuple;

import dev.rndmorris.salisarcana.common.compat.Mods;
import thaumcraft.client.gui.GuiResearchBrowser;

public class MixinHelpers {

    private static final BrowserPagingAPI browserPaging = getBrowserPaging();

    // we can't just static init it otherwise we get classnotfound without tc4tweak installed
    private static BrowserPagingAPI getBrowserPaging() {
        if (Mods.TC4Tweak.isLoaded()) {
            return TC4TweaksAPI.getBrowserPagingAPI();
        }
        return null;
    }

    public static java.util.Map<String, thaumcraft.api.research.ResearchCategoryList> BrowserPaging$GetTabsOnCurrentPage(
        GuiResearchBrowser browser, String player) {
        return browserPaging.getTabsOnCurrentPage(browser, player);
    }

    public static int BrowserPaging$GetTabsPerPage(GuiResearchBrowser browser) {
        return browserPaging.getTabsPerPage(browser);
    }

    public static void BrowserPaging$SetPage(GuiResearchBrowser browser, int page) {
        browserPaging.setPage(browser, page);
    }

    public static void BrowserPaging$NextPage(GuiResearchBrowser browser) {
        browserPaging.moveNextPage(browser);
    }

    public static int BrowserPaging$CurrentPageIndex(GuiResearchBrowser browser) {
        return browserPaging.getCurrentPage(browser);
    }

    public static int BrowserPaging$MaxPageIndex(GuiResearchBrowser browser) {
        return browserPaging.getTotalPages(browser);
    }

    // used client side only in MixinGuiResearchRecipe, MixinGuiResearchBrowser_RightClickClose
    public static final Stack<Tuple> RightClickClose$ScreenStack = new Stack<>();

    public static HashSet<Class<? extends Entity>> getEntitiesFromStringArr(String[] value) {
        HashSet<Class<? extends Entity>> entities = new HashSet<>();
        for (String entityName : value) {
            Class<? extends Entity> entity = EntityList.stringToClassMapping.get(entityName);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }
}
