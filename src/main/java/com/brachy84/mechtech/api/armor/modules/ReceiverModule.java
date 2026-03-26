package com.brachy84.mechtech.api.armor.modules;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.common.items.MTMetaItems;

import gregtech.api.items.metaitem.MetaItem;

public class ReceiverModule implements IModule {

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.CHEST;
    }

    @Override
    public MetaItem<?>.MetaValueItem getMetaValueItem() {
        return MTMetaItems.WIRELESS_RECEIVER;
    }

    @Override
    public String getModuleId() {
        return "wireless_receiver";
    }
}
