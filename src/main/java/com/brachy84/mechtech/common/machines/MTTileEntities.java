package com.brachy84.mechtech.common.machines;

import net.minecraft.util.ResourceLocation;

import com.brachy84.mechtech.MechTech;
import com.brachy84.mechtech.common.MTConfig;
import com.brachy84.mechtech.common.machines.multis.MetaTileEntityTeslaTower;

import gregtech.common.metatileentities.MetaTileEntities;

/**
 * Claimed range 10100-10499
 */
public class MTTileEntities {

    public static MetaTileEntityTeslaTower TESLA_TOWER;
    public static MetaTileEntityArmorWorkbench ARMOR_WORKBENCH;
    public static EnergySink ENERGY_SINK;

    public static void init() {
        ARMOR_WORKBENCH = MetaTileEntities.registerMetaTileEntity(10100,
                new MetaTileEntityArmorWorkbench(loc("armor_workbench")));
        if (MTConfig.teslaTower.enable)
            TESLA_TOWER = MetaTileEntities.registerMetaTileEntity(10101,
                    new MetaTileEntityTeslaTower(loc("tesla_tower")));
        ENERGY_SINK = MetaTileEntities.registerMetaTileEntity(10490, new EnergySink(loc("energy_sink")));
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(MechTech.MODID, path);
    }
}
