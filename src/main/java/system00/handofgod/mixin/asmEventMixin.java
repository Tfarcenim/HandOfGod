package system00.handofgod.mixin;

import com.chaoswither.util.asmEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import system00.handofgod.Util;

@Mixin(asmEvent.class)
public class asmEventMixin {
    @Inject(method = "isGod",at = @At("HEAD"), cancellable = true,remap = false)
    private static void intercept$isGod(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Util.isGod(entity)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "runTick",at = @At("HEAD"), cancellable = true,remap = false)
    private static void intercept$runTick(Minecraft mc, CallbackInfo ci) {
        if (Util.isGod(mc.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "removeEntity",at = @At("RETURN"),cancellable = true ,remap = false)
    private static void intercept$removeEntity(World world, Entity entity, CallbackInfo ci) {
        if (Util.isGod(Minecraft.getMinecraft().player)) {
            ci.cancel();
        }
    }

    @Inject(method = "removeEntityDangerously",at = @At("RETURN"),cancellable = true ,remap = false)
    private static void intercept$removeEntityDangerously(World world, Entity entity, CallbackInfo ci) {
        if (Util.isGod(Minecraft.getMinecraft().player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onEntityRemoved",at = @At("RETURN"),cancellable = true ,remap = false)
    private static void intercept$onEntityRemoved(World world, Entity entity, CallbackInfo ci) {
        if (Util.isGod(Minecraft.getMinecraft().player)) {
            ci.cancel();
        }
    }

    @Inject(method = "removeEntityFromWorld",at = @At("RETURN"),cancellable = true ,remap = false)
    private static void intercept$removeEntityFromWorld(WorldClient client, int entityID, CallbackInfo ci) {
        if (Util.isGod(Minecraft.getMinecraft().player)) {
            ci.cancel();
        }
    }
}
