package dev.rndmorris.salisarcana.mixins.late.lib.events;

import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.rndmorris.salisarcana.config.ConfigModuleRoot;
import dev.rndmorris.salisarcana.lib.MixinHelpers;
import thaumcraft.common.lib.events.EventHandlerEntity;

@Mixin(value = EventHandlerEntity.class, remap = false)
public class MixinEventHandlerEntity {

    @Unique
    private static final boolean sa$isBlacklist = ConfigModuleRoot.enhancements.mobVisBlacklist.isEnabled();

    @Unique
    private final HashSet<Class<? extends Entity>> sa$entities = MixinHelpers
        .getEntitiesFromStringArr(ConfigModuleRoot.enhancements.mobVisWhitelist.getValue());

    @WrapOperation(
        method = "livingTick(Lnet/minecraftforge/event/entity/living/LivingDeathEvent;)V",
        at = @At(
            value = "INVOKE",
            target = "Lthaumcraft/common/lib/utils/EntityUtils;getRecentlyHit(Lnet/minecraft/entity/EntityLivingBase;)I"))
    private int sa$shouldGenerateVisOrbs(EntityLivingBase e, Operation<Integer> original) {
        if (sa$isBlacklist) {
            if (!sa$entities.contains(e.getClass())) {
                return original.call(e);
            }
        } else {
            if (sa$entities.contains(e.getClass())) {
                return original.call(e);
            }
        }
        return 0;
    }
}
