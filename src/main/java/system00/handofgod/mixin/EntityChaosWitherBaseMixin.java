package system00.handofgod.mixin;

import com.chaoswither.entity.EntityChaosWither;
import com.chaoswither.entity.EntityChaosWitherBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import system00.handofgod.ducks.EntityChaosWitherDuck;

@Mixin(EntityChaosWitherBase.class)
abstract class EntityChaosWitherBaseMixin extends Entity {
    public EntityChaosWitherBaseMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "isEntityAlive",cancellable = true,at = @At("HEAD"))
    private void kill(CallbackInfoReturnable<Boolean> cir) {
        if (((EntityChaosWitherDuck)this).isActuallyDead()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "setMaxHealth",at = @At("HEAD"),cancellable = true,remap = false)
    private void kill2(CallbackInfo ci) {
        if (((EntityChaosWitherDuck)this).isActuallyDead()) {
            ci.cancel();
        }
    }

    @Inject(method = "onRemovedFromWorld",at = @At("HEAD"),cancellable = true,remap = false)
    private void kill3(CallbackInfo ci) {
        if (((EntityChaosWitherDuck) this).isActuallyDead()) {
            super.onRemovedFromWorld();
            ci.cancel();
        }
    }

    @Inject(method = "onKillCommand",at = @At("HEAD"),cancellable = true)
    private void kill4(CallbackInfo ci) {
        ((EntityChaosWitherDuck)this).setActuallyDead(true);
        this.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
        ci.cancel();
    }
}
