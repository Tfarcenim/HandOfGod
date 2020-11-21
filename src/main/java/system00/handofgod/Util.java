package system00.handofgod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Util {

    public static boolean isGod(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            return player.inventory.hasItemStack(new ItemStack(ModItems.handOfGod));
        }
        return false;
    }

}
