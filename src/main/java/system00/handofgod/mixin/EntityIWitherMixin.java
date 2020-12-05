package system00.handofgod.mixin;

import com.chaoswither.entity.EntityIWither;
import org.spongepowered.asm.mixin.Mixin;
import system00.handofgod.ducks.EntityChaosWitherDuck;

@Mixin(EntityIWither.class)
public class EntityIWitherMixin implements EntityChaosWitherDuck {

    private boolean actuallyDead;

    @Override
    public boolean isActuallyDead() {
        return actuallyDead;
    }

    @Override
    public void setActuallyDead(boolean b) {
        actuallyDead = b;
    }
}
