package org.cyclops.evilcraft.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.inventory.NBTSimpleInventoryItemHeld;
import org.cyclops.cyclopscore.item.ItemGui;
import org.cyclops.evilcraft.client.gui.container.GuiExaltedCrafter;
import org.cyclops.evilcraft.entity.item.EntityItemEmpowerable;
import org.cyclops.evilcraft.inventory.container.ContainerExaltedCrafter;

import java.util.List;

/**
 * A portable crafting table with a built-in ender chest.
 * @author rubensworks
 *
 */
public class ExaltedCrafter extends ItemGui implements IItemEmpowerable {

    private static final String NBT_RETURNTOINNER = "returnToInner";
    
    private static ExaltedCrafter _instance = null;
    
    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static ExaltedCrafter getInstance() {
        return _instance;
    }

    public ExaltedCrafter(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }
    
    @Override
    public boolean hasEffect(ItemStack itemStack){
    	return isEmpowered(itemStack);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack itemStack){
        return isEmpowered(itemStack) ? EnumRarity.UNCOMMON : super.getRarity(itemStack);
    }
    
    @Override
	public boolean isEmpowered(ItemStack itemStack) {
    	return (itemStack.getItemDamage() >> 1 & 1) == 1;
    }
    
    @Override
	public ItemStack empower(ItemStack itemStack) {
        if(itemStack.getItem() == this) {
            itemStack.setItemDamage(itemStack.getItemDamage() | 2);
        }
        return itemStack;
    }
    
    public boolean isWooden(ItemStack itemStack) {
    	return (itemStack.getItemDamage() & 1) == 1;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName() + (isWooden(itemStack) ? ".wood" : "");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List itemList) {
    	itemList.add(new ItemStack(item, 1, 0));
    	itemList.add(new ItemStack(item, 1, 1));
    	itemList.add(new ItemStack(item, 1, 2));
    	itemList.add(new ItemStack(item, 1, 3));
    }
    
    /**
     * Get the supplementary inventory of the given crafter.
     * @param player The player using the crafter.
     * @param itemStack The item stack.
     * @param itemIndex The item index.
     * @return The inventory.
     */
    public IInventory getSupplementaryInventory(EntityPlayer player, ItemStack itemStack, int itemIndex) {
    	if(isWooden(itemStack)) {
    		return new NBTSimpleInventoryItemHeld(player, itemIndex, 27, 64);
    	}
    	return player.getInventoryEnderChest();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        if(isEmpowered(itemStack))
            list.add(TextFormatting.RED + "Empowered");
    }
    
    @Override
    public boolean hasCustomEntity(ItemStack itemStack) {
    	return true;
    }
    
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemStack) {
    	return new EntityItemEmpowerable(world, (EntityItem) location);
    }

    public void setReturnToInner(ItemStack itemStack, boolean returnToInner) {
        if(itemStack.hasTagCompound()) {
            itemStack.getTagCompound().setBoolean(NBT_RETURNTOINNER, returnToInner);
        }
    }

    public boolean isReturnToInner(ItemStack itemStack) {
        if(itemStack.hasTagCompound()) {
            return itemStack.getTagCompound().getBoolean(NBT_RETURNTOINNER);
        }
        return false;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerExaltedCrafter.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Class<? extends GuiScreen> getGui() {
        return GuiExaltedCrafter.class;
    }

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, NBTTagCompound nbt) {
        final IItemHandler itemHandler= new ItemHandler(stack);
        return new ICapabilityProvider() {
            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                return isWooden(stack) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                return isWooden(stack) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                        ? (T) itemHandler : null;
            }
        };
    }

    public class ItemHandler implements IItemHandlerModifiable {

        protected ItemStack itemStack;

        public ItemHandler(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        protected ItemStack[] getItemList() {
            ItemStack[] itemStacks = new ItemStack[getSlots()];
            NBTTagCompound rootTag = itemStack.getTagCompound();
            if (rootTag != null && rootTag.hasKey(NBTSimpleInventoryItemHeld.NBT_TAG_ROOT, MinecraftHelpers.NBTTag_Types.NBTTagList.ordinal())) {
                NBTTagList nbttaglist = rootTag.getTagList(NBTSimpleInventoryItemHeld.NBT_TAG_ROOT, MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal());
                for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                    NBTTagCompound slot = nbttaglist.getCompoundTagAt(j);
                    int index;
                    if (slot.hasKey("index")) {
                        index = slot.getInteger("index");
                    } else {
                        index = slot.getByte("Slot");
                    }
                    if (index >= 0 && index < getSlots()) {
                        itemStacks[index] = ItemStack.loadItemStackFromNBT(slot);
                    }
                }
            }
            return itemStacks;
        }

        protected void setItemList(ItemStack[] itemStacks) {
            NBTTagCompound rootTag = ItemStackHelpers.getSafeTagCompound(itemStack);
            NBTTagList slots = new NBTTagList();
            for (byte index = 0; index < getSlots(); ++index) {
                ItemStack itemStack = itemStacks[index];
                if (itemStack != null && itemStack.stackSize > 0) {
                    NBTTagCompound slot = new NBTTagCompound();
                    slots.appendTag(slot);
                    slot.setByte("Slot", index);
                    itemStack.writeToNBT(slot);
                }
            }
            rootTag.setTag(NBTSimpleInventoryItemHeld.NBT_TAG_ROOT, slots);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            ItemStack[] itemStacks = getItemList();
            itemStacks[slot] = stack;
            setItemList(itemStacks);
        }

        @Override
        public int getSlots() {
            return 27;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return getItemList()[slot];
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack == null) {
                return null;
            }

            ItemStack[] itemStacks = getItemList();
            ItemStack existingStack = itemStacks[slot];

            int maxStackSize;
            if (existingStack != null) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existingStack))
                    return stack;

                maxStackSize = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - existingStack.stackSize;

                if (stack.stackSize <= maxStackSize) {
                    if (!simulate) {
                        ItemStack copy = stack.copy();
                        copy.stackSize += existingStack.stackSize;
                        setStackInSlot(slot, copy);
                    }

                    return null;
                } else  {
                    // copy the stack to not modify the original one
                    stack = stack.copy();
                    if (!simulate) {
                        ItemStack copy = stack.splitStack(maxStackSize);
                        copy.stackSize += existingStack.stackSize;
                        setStackInSlot(slot, copy);
                        return stack;
                    }  else {
                        stack.stackSize -= maxStackSize;
                        return stack;
                    }
                }
            } else {
                maxStackSize = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
                if (maxStackSize < stack.stackSize) {
                    // copy the stack to not modify the original one
                    stack = stack.copy();
                    if (!simulate) {
                        setStackInSlot(slot, stack.splitStack(maxStackSize));
                        return stack;
                    } else {
                        stack.stackSize -= maxStackSize;
                        return stack;
                    }
                } else {
                    if (!simulate) {
                        setStackInSlot(slot, stack);
                    }
                    return null;
                }
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0)
                return null;

            ItemStack existingStack = getStackInSlot(slot);

            if (existingStack == null) {
                return null;
            }

            ItemStack extracted = existingStack.splitStack(amount);
            if (!simulate) {
                setStackInSlot(slot, existingStack);
            }
            return extracted;
        }

        private int getSlotLimit(int slot) {
            return 64;
        }
    }
}
