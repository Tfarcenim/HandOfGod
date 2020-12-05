package system00.handofgod.mixin;

import com.chaoswither.entity.EntityChaosWither;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import system00.handofgod.ducks.EntityChaosWitherDuck;

@Mixin(EntityChaosWither.class)
abstract class EntityChaosWitherMixin extends Entity {
    public EntityChaosWitherMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "setDead",cancellable = true,at = @At("HEAD"))
    private void kill(CallbackInfo ci) {
        if (((EntityChaosWitherDuck)this).isActuallyDead()) {
            isDead = true;
            ci.cancel();
        }
    }
}
