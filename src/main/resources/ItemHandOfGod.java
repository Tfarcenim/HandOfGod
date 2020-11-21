import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import cofh.redstoneflux.RedstoneFluxProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import ic2.core.IC2;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import system00.handofgod.client.tab.CreativeTabLoader;
import system00.handofgod.common.config.ConfigLoader;
import system00.handofgod.common.entity.IEntityArchangel;
import system00.handofgod.common.gui.IGodInventory;
import system00.handofgod.common.gui.InventoryHandOfGod;
import system00.handofgod.common.item.tool.IGod;
import system00.handofgod.util.HandOfGodUtil;
import system00.handofgod.util.IC2Util;

@Optional.InterfaceList({
		@Optional.Interface(modid = RedstoneFluxProps.MOD_ID, iface = "cofh.redstoneflux.api.IEnergyContainerItem"),
		@Optional.Interface(modid = IC2.MODID, iface = "ic2.api.item.ISpecialElectricItem") })
public class ItemHandOfGod extends ItemPickaxe implements IGod, IEnergyContainerItem, ISpecialElectricItem {

	public static final Item.ToolMaterial GOD = EnumHelper.addToolMaterial("GOD", 100, 0, 0, 0, 0);

	private static ItemStack def = null;

	@SuppressWarnings("unused")
	private static void init() {
		def = new ItemStack(ModItems.handOfGod);
		Map<Enchantment, Integer> enchMap = Maps.newLinkedHashMap();
		enchMap.put(Enchantments.FORTUNE, 100);
		EnchantmentHelper.setEnchantments(enchMap, def);
		NBTTagList list = new NBTTagList();
		NBTTagCompound element = new NBTTagCompound();
		def.setTagInfo("GodPotion", list);
	}

	public static ItemStack getDef() {
		if (def == null) {
			init();
		}
		return def;
	}

	public ItemHandOfGod() {
		super(GOD);
		this.setTranslationKey("handOfGod");
		//this.setCreativeTab(CreativeTabLoader.handOfGodTab);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		super.setDamage(stack, 0);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return 0.0F;
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canHarvestBlock(IBlockState blockIn) {
		return false;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return leftClickEntity(player, entity);
	}

	public boolean leftClickEntity(EntityLivingBase god, Entity entity) {
		if (!entity.world.isRemote && (god instanceof EntityPlayer || god instanceof IEntityArchangel)) {
			ItemStack stack = god.getHeldItemMainhand();
			boolean success = false;
			HandOfGodUtil.kill(entity, god);
			success = true;
			if (ConfigLoader.getBoolean(stack, "handOfGodKillFacing")) {
				HandOfGodUtil.killFacing(god);
				success = true;
			}
			if (success && god instanceof EntityPlayerMP) {
				BlockPos pos = god.getPosition();
				((EntityPlayerMP) god).connection.sendPacket(new SPacketCustomSound("handofgod:successsound",
						SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
			}
			return success;
		}
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			if (player.isSneaking()) {
				if (ConfigLoader.getBoolean(stack, "handOfGodAreaKill")) {
					int range = ConfigLoader.getInt(stack, "handOfGodAreaKillRange");
					int count = HandOfGodUtil.killRangeEntity(world, player, range);
					player.sendMessage(new TextComponentTranslation("handOfGod.handOfGodAreaKill", range * 2, count));
					if (player instanceof EntityPlayerMP) {
						BlockPos pos = player.getPosition();
						((EntityPlayerMP) player).connection.sendPacket(new SPacketCustomSound("handofgod:successsound",
								SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
					}
				}
			} else {
				NBTTagCompound nbt = stack.getTagCompound();
				if (nbt == null) {
					nbt = new NBTTagCompound();
					nbt.setInteger("range", 0);
					stack.setTagCompound(nbt);
				} else {
					if (nbt.hasKey("range")) {
						nbt.setInteger("range", nbt.getInteger("range") >= ConfigLoader.handOfGodMaxRange ? 0
								: nbt.getInteger("range") + 1);
					} else {
						nbt.setInteger("range", 1);
					}
				}
				ITextComponent message = new TextComponentTranslation("handOfGod.range",
						1 + 2 * nbt.getInteger("range"));
				player.sendMessage(message);
				if (player instanceof EntityPlayerMP) {
					BlockPos pos = player.getPosition();
					((EntityPlayerMP) player).connection.sendPacket(new SPacketCustomSound("handofgod:successsound",
							SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public int getDamage(ItemStack stack) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		tooltip.add(I18n.format("handOfGod.currentRange", 1 + 2 * getRange(stack)));
		if (ConfigLoader.getBoolean(stack, "handOfGodKillAura")) {
			tooltip.add(
					I18n.format("handOfGod.killAuraRange", 2 * ConfigLoader.getInt(stack, "handOfGodKillAuraRange")));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodKillFacing")) {
			tooltip.add(I18n.format("handOfGod.killFacing", ConfigLoader.getInt(stack, "handOfGodKillFacingRange"),
					ConfigLoader.getDouble(stack, "handOfGodKillFacingSlope")));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodAreaKill")) {
			tooltip.add(
					I18n.format("handOfGod.areaKillRange", 2 * ConfigLoader.getInt(stack, "handOfGodAreaKillRange")));
		}
		double distance = ConfigLoader.getDouble(stack, "handOfGodBlockReachDistance");
		if (distance > 0) {
			tooltip.add(I18n.format("handOfGod.blockReachDistance", distance));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodSurrenderArms")) {
			tooltip.add(I18n.format("handOfGod.surrenderArms"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodInventoryDestruction")) {
			tooltip.add(I18n.format("handOfGod.inventoryDestruction"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodRemoveStackLimit")) {
			tooltip.add(I18n.format("handOfGod.removeStackLimit"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodAutoPickup")) {
			tooltip.add(I18n.format("handOfGod.autoPickup"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodDropLoot")) {
			tooltip.add(I18n.format("handOfGod.dropLoot"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodFindOwner")) {
			tooltip.add(I18n.format("handOfGod.findOwner"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodOmnipresence")) {
			tooltip.add(I18n.format("handOfGod.omnipresence"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodRaytraceToFluids")) {
			tooltip.add(I18n.format("handOfGod.raytraceToFluids"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodTriggerBreakEvent")) {
			tooltip.add(I18n.format("handOfGod.triggerBreakEvent"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodInfiniteEnergy")) {
			tooltip.add(I18n.format("handOfGod.infiniteEnergy"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodTrueInvisibility")) {
			tooltip.add(I18n.format("handOfGod.trueInvisibility"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodTrueVision")) {
			tooltip.add(I18n.format("handOfGod.trueVision"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodNoclip")) {
			tooltip.add(I18n.format("handOfGod.noclip"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodThorns")) {
			tooltip.add(I18n.format("handOfGod.thorns"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodSpiteKill")) {
			tooltip.add(I18n.format("handOfGod.spiteKill"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodSilencedDeath")) {
			tooltip.add(I18n.format("handOfGod.silencedDeath"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodKillFriendly")) {
			tooltip.add(I18n.format("handOfGod.killFriendly"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodKillAll")) {
			tooltip.add(I18n.format("handOfGod.killAll"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodKickPlayer")) {
			tooltip.add(I18n.format("handOfGod.kickPlayer"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodBeyondRedemption")) {
			tooltip.add(I18n.format("handOfGod.beyondRedemption"));
		}
		if (ConfigLoader.getBoolean(stack, "handOfGodIzanami")) {
			tooltip.add(I18n.format("handOfGod.izanami"));
		}
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!world.isRemote) {
			if (entity instanceof EntityPlayer) {
				NBTTagCompound nbt;
				if (stack.hasTagCompound()) {
					nbt = stack.getTagCompound();
				} else {
					nbt = new NBTTagCompound();
					stack.setTagCompound(nbt);
				}
				if (!hasOwner(stack)) {
					setOwner(stack, (EntityPlayer) entity);
				}
			}
			if (ConfigLoader.getBoolean(stack, "handOfGodInfiniteEnergy")) {
				if (Loader.isModLoaded(IC2.MODID)) {
					ic2charge(stack, world, entity, itemSlot, isSelected);
				}
				if (Loader.isModLoaded(RedstoneFluxProps.MOD_ID)) {
					rfReceive(stack, world, entity, itemSlot, isSelected);
				}
			}
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			items.add(getDef());
		}
	}

	@Optional.Method(modid = IC2.MODID)
	private void ic2charge(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!entity.world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack toCharge = player.inventory.getStackInSlot(i);
				if (!toCharge.isEmpty()) {
					ElectricItem.manager.charge(toCharge,
							ElectricItem.manager.getMaxCharge(toCharge) - ElectricItem.manager.getCharge(toCharge),
							Integer.MAX_VALUE, true, false);
				}
			}
			if (Loader.isModLoaded(Baubles.MODID)) {
				for (ItemStack toCharge : getBaubles(player)) {
					ElectricItem.manager.charge(toCharge,
							ElectricItem.manager.getMaxCharge(toCharge) - ElectricItem.manager.getCharge(toCharge),
							Integer.MAX_VALUE, true, false);
				}
			}
		}
	}

	@Optional.Method(modid = RedstoneFluxProps.MOD_ID)
	private void rfReceive(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!entity.world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack receive = player.inventory.getStackInSlot(i);
				if (!receive.isEmpty()) {
					if (receive.getItem() instanceof IEnergyContainerItem) {
						IEnergyContainerItem energy = (IEnergyContainerItem) receive.getItem();
						energy.receiveEnergy(receive,
								energy.getMaxEnergyStored(receive) - energy.getEnergyStored(receive), false);
					}
					if (receive.hasCapability(CapabilityEnergy.ENERGY, null)) {
						IEnergyStorage cap = (IEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
						if ((cap != null) && (cap.canReceive())) {
							cap.receiveEnergy(cap.getMaxEnergyStored() - cap.getEnergyStored(), false);
						}
					}
				}
			}
			if (Loader.isModLoaded(Baubles.MODID)) {
				for (ItemStack receive : getBaubles(player)) {
					if (receive.getItem() instanceof IEnergyContainerItem) {
						IEnergyContainerItem energy = (IEnergyContainerItem) receive.getItem();
						energy.receiveEnergy(receive,
								energy.getMaxEnergyStored(receive) - energy.getEnergyStored(receive), false);
					}
					if (receive.hasCapability(CapabilityEnergy.ENERGY, null)) {
						IEnergyStorage cap = (IEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
						if ((cap != null) && (cap.canReceive())) {
							cap.receiveEnergy(cap.getMaxEnergyStored() - cap.getEnergyStored(), false);
						}
					}
				}
			}
		}
	}

	@Optional.Method(modid = Baubles.MODID)
	private List<ItemStack> getBaubles(EntityPlayer player) {
		IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
		if (handler == null) {
			return Lists.newArrayList();
		}
		return IntStream.range(0, handler.getSlots()).mapToObj(handler::getStackInSlot)
				.filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
	}

	@Override
	public boolean hasOwner(ItemStack stack) {
		return stack.hasTagCompound()
				&& (stack.getTagCompound().hasKey("Owner") || stack.getTagCompound().hasKey("OwnerUUID"));
	}

	@Override
	public boolean isOwner(ItemStack stack, EntityPlayer player) {
		return stack.getTagCompound().getString("Owner").equals(player.getName())
				|| stack.getTagCompound().getString("OwnerUUID").equals(player.getUniqueID().toString());
	}

	public void setOwner(ItemStack stack, EntityPlayer player) {
		stack.setTagInfo("Owner", new NBTTagString(player.getName()));
		stack.setTagInfo("OwnerUUID", new NBTTagString(player.getUniqueID().toString()));
	}

	@Override
	public int getRange(ItemStack stack) {
		int range = 1;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null && nbt.hasKey("range")) {
			range = nbt.getInteger("range");
		}
		return range;
	}

	@Override
	public boolean hasInventory(ItemStack stack) {
		return true;
	}

	@Override
	public IGodInventory getInventory(ItemStack stack) {
		return new InventoryHandOfGod(stack);
	}

	@Override
	@Optional.Method(modid = RedstoneFluxProps.MOD_ID)
	public int receiveEnergy(ItemStack stack, int energy, boolean simulate) {
		return ConfigLoader.getBoolean(stack, "handOfGodInfiniteEnergy") ? energy : 0;
	}

	@Override
	@Optional.Method(modid = RedstoneFluxProps.MOD_ID)
	public int extractEnergy(ItemStack stack, int energy, boolean simulate) {
		return ConfigLoader.getBoolean(stack, "handOfGodInfiniteEnergy") ? energy : 0;
	}

	@Override
	@Optional.Method(modid = RedstoneFluxProps.MOD_ID)
	public int getEnergyStored(ItemStack stack) {
		return ConfigLoader.getBoolean(stack, "handOfGodInfiniteEnergy") ? Integer.MAX_VALUE / 2 : 0;
	}

	@Override
	@Optional.Method(modid = RedstoneFluxProps.MOD_ID)
	public int getMaxEnergyStored(ItemStack stack) {
		return ConfigLoader.getBoolean(stack, "handOfGodInfiniteEnergy") ? Integer.MAX_VALUE : 0;
	}

	@Override
	@Optional.Method(modid = IC2.MODID)
	public IElectricItemManager getManager(ItemStack stack) {
		return ConfigLoader.getBoolean(stack, "handOfGodInfiniteEnergy") ? IC2Util.infinite : null;
	}
}