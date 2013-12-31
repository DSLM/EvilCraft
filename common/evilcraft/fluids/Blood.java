package evilcraft.fluids;

import evilcraft.EvilCraft;
import evilcraft.api.config.ConfigurableFluid;
import evilcraft.api.config.ExtendedConfig;

public class Blood extends ConfigurableFluid{
    
    private static Blood _instance = null;
    
    public static void initInstance(ExtendedConfig eConfig) {
        if(_instance == null)
            _instance = new Blood(eConfig);
        else
            eConfig.showDoubleInitError();
    }
    
    public static Blood getInstance() {
        return _instance;
    }

    private Blood(ExtendedConfig eConfig) {
        super(eConfig);
        setDensity(1500); // How tick the fluid is, affects movement inside the liquid.
        setViscosity(3000); // How fast the fluid flows.
    }

}
