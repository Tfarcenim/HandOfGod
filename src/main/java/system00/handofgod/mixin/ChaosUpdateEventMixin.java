package system00.handofgod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
        if (Util.isGod(Minecraft.getMinecraft().player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onTick(Lnet/minecraftforge/fml/common/gameevent/TickEvent$PlayerTickEvent;)V",at = @At("HEAD"),cancellable = true,remap = false)
    private void intercept$onPlayerTick(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (Util.isGod(event.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onWorldTickEvent",at = @At("HEAD"),cancellable = true)
    private void intercept$onWorldTickEvent(TickEvent.WorldTickEvent event, CallbackInfo ci) {
        World world = event.world;
        for (EntityPlayer player : world.getEntities(EntityPlayer.class,player -> true)) {
            if (Util.isGod(player)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onServerTick",at = @At("HEAD"),cancellable = true)
    private void intercept$onServerTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
        for (EntityPlayer player : world.getEntities(EntityPlayer.class,player -> true)) {
            if (Util.isGod(player)) {
                ci.cancel();
            }
        }
    }
}
