package system00.handofgod;

import com.chaoswither.chaoswither;
import com.chaoswither.entity.EntityChaosWither;
import com.chaoswither.event.ChaosUpdateEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import system00.handofgod.ducks.EntityChaosWitherDuck;

public class ItemHandOfGod extends Item {
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
			for (EntityChaosWither entity : worldIn.getEntities(EntityChaosWither.class,entity -> true)) {
				((EntityChaosWitherDuck)entity).setActuallyDead(true);
				System.out.println(entity.isDead);
				entity.setDead();
				entity.isDead1 = true;
			}
		ChaosUpdateEvent.WITHERLIVE = false;
		chaoswither.happymode = false;
		return ActionResult.newResult(EnumActionResult.SUCCESS,playerIn.getHeldItem(handIn));
	}
}