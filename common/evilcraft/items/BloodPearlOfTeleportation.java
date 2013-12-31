package evilcraft.items;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import evilcraft.EvilCraft;
import evilcraft.api.config.ConfigurableDamageIndicatedItemFluidContainer;
import evilcraft.api.config.ExtendedConfig;
import evilcraft.fluids.Blood;

public class BloodPearlOfTeleportation extends ConfigurableDamageIndicatedItemFluidContainer {
    
    private static BloodPearlOfTeleportation _instance = null;
    
    private static final int SLOW_DURATION = 5;
    
    public static void initInstance(ExtendedConfig eConfig) {
        if(_instance == null)
            _instance = new BloodPearlOfTeleportation(eConfig);
        else
            eConfig.showDoubleInitError();
    }
    
    public static BloodPearlOfTeleportation getInstance() {
        return _instance;
    }

    private BloodPearlOfTeleportation(ExtendedConfig eConfig) {
        super(eConfig, 1000, Blood.getInstance());
    }
    
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        FluidStack fluidStack = this.drain(itemStack, 100, false);
        if(fluidStack != null && fluidStack.amount > 0) {
            if (player.capabilities.isCreativeMode) {
                return itemStack;
            } else {
                this.drain(itemStack, 100, true);
                world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, SLOW_DURATION * 20, 2));
                if (!world.isRemote) {
                    world.spawnEntityInWorld(new EntityEnderPearl(world, player));
                }
    
                return itemStack;
            }
        }
        return itemStack;
    }
    
}
