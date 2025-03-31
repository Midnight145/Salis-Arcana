package dev.rndmorris.salisarcana.mixins.late.gui;

import static dev.rndmorris.salisarcana.lib.MixinHelpers.RightClickClose$ScreenStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.rndmorris.salisarcana.config.ConfigModuleRoot;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.gui.GuiResearchRecipe;

@Mixin(GuiResearchRecipe.class)
public class MixinGuiResearchRecipe extends GuiScreen {

    @Shadow(remap = false)
    protected double guiMapX;

    @Shadow(remap = false)
    protected double guiMapY;

    @Shadow(remap = false)
    private int page;

    @Shadow(remap = false)
    private ResearchItem research;

    // Switching pages doesn't init a new GuiResearchRecipe, so that's not automatically added to the stack in initGui.
    // We have to track whenever a page changes manually
    @Unique
    private int sa$currentPage = 0;

    @WrapMethod(method = "initGui")
    private void onInit(Operation<Void> original) {
        RightClickClose$ScreenStack.push(new Tuple(research, page));
        this.sa$currentPage = page;
        original.call();
    }

    @WrapMethod(method = "mouseClicked")
    private void wrapMouseClicked(int mouseX, int mouseY, int button, Operation<Void> original) {
        if (button == 1) {
            if ((RightClickClose$ScreenStack.size() <= 1)) {
                // it can be zero in the case of opening directly to a page, where the page was originally the only
                // thing in the stack
                RightClickClose$ScreenStack.clear();
                this.mc.displayGuiScreen(new GuiResearchBrowser());
            } else {
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                RightClickClose$ScreenStack.pop(); // current screen
                Tuple item = RightClickClose$ScreenStack.pop(); // next screen
                player.worldObj.playSound(player.posX, player.posY, player.posZ, "thaumcraft:page", 0.66F, 1.0F, false);
                this.mc.displayGuiScreen(
                    new GuiResearchRecipe(
                        (ResearchItem) item.getFirst(),
                        (int) item.getSecond(),
                        this.guiMapX,
                        this.guiMapY));
            }
            return;
        }

        original.call(mouseX, mouseY, button);

        // mouseClicked is the only place where the page can change (someone clicked the arrows)
        // so we check against the current page and push it to the stack if necessary.
        if (this.sa$currentPage != this.page) {
            this.sa$currentPage = this.page;
            RightClickClose$ScreenStack.push(new Tuple(this.research, this.page));
        }
    }

    @WrapOperation(
        method = "keyTyped",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    private void onKeyTyped(Minecraft instance, GuiScreen i, Operation<Void> original) {
        if (ConfigModuleRoot.enhancements.nomiconSavePage.isEnabled()) {
            // just close the window entirely
            original.call(instance, null);
            return;
        }
        original.call(instance, i);
    }
}
