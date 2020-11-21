package system00.handofgod.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import system00.handofgod.Util;

@Mixin(targets = "com.chaoswither.event.ChaosUpdateEvent")
public class ChaosUpdateEventMixin {

    @Inject(method = "isGod",at = @At("HEAD"),cancellable = true,remap = false)
    private static void handofgodisGod(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (Util.isGod(entity)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onLivingUpdate",at = @At("HEAD"),cancellable = true,remap = false)
    private void intercept$onLivingUpdate(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (Util.isGod(event.getEntity())) {
            ci.cancel();
        }
    }

    @Inject(method = "onTick(Lnet/minecraftforge/fml/common/gameevent/TickEvent$PlayerTickEvent;)V",at = @At("HEAD"),cancellable = true,remap = false)
    private void intercept$onPlayerTick(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (Util.isGod(event.player)) {
            ci.cancel();
        }
    }
}
