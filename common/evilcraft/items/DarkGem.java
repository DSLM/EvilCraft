package evilcraft.items;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.EvilCraft;
import evilcraft.api.Helpers;
import evilcraft.api.config.ConfigurableItem;
import evilcraft.api.config.ExtendedConfig;
import evilcraft.entities.monster.Werewolf;
import evilcraft.entities.monster.WerewolfConfig;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class DarkGem extends ConfigurableItem {
    
    private static DarkGem _instance = null;
    
    public static void initInstance(ExtendedConfig eConfig) {
        if(_instance == null)
            _instance = new DarkGem(eConfig);
        else
            eConfig.showDoubleInitError();
    }
    
    public static DarkGem getInstance() {
        return _instance;
    }

    private DarkGem(ExtendedConfig eConfig) {
        super(eConfig);
    }

}
