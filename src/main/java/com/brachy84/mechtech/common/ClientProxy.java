package com.brachy84.mechtech.common;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.api.armor.modules.Binoculars;
import com.brachy84.mechtech.client.ClientHandler;
import com.brachy84.mechtech.common.items.MTMetaItems;
import gregtech.api.items.armor.ArmorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preLoad() {
        super.preLoad();
        ClientHandler.preInit();
    }

    @SubscribeEvent
    public static void onRender(final TickEvent.RenderTickEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.inGameHasFocus && mc.world != null && !mc.gameSettings.showDebugInfo && Minecraft.isGuiEnabled()) {
            final ArmorUtils.ModularHUD HUD = new ArmorUtils.ModularHUD();
            List<String> hudStrings = new ArrayList<>();
            long[] charge = new long[4];
            long[] maxCharge = new long[4];
            for (int i = 0; i < 4; i++) {
                ItemStack stack = mc.player.inventory.armorInventory.get(i);
                ModularArmor modularArmor = ModularArmor.get(stack);
                if (modularArmor != null) {
                    charge[i] = ModularArmor.getEnergy(stack);
                    maxCharge[i] = ModularArmor.getCapacity(stack);
                    modularArmor.addHUDInfo(stack, hudStrings);
                }
            }
            HUD.newString(I18n.format("metaarmor.hud.energy_lvl", String.format("%.1f", batteryPercentage(charge, maxCharge)) + "%"));
            for (String string : hudStrings) {
                HUD.newString(string);
            }
            HUD.draw();
            HUD.reset();
        }
    }

    private static float batteryPercentage(long[] charge, long[] maxCharge) {
        float percentage = 0;
        int max = 0;
        for (int i = 0; i < charge.length; i++) {
            max++;
            percentage += (float) charge[i] * 100.0F / maxCharge[i];
        }
        return (percentage * 100 / (max * 100));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW) //set to low so other mods don't accidentally destroy it easily
    public static void handleFovEvent(FOVUpdateEvent event) {

        IAttributeInstance iattributeinstance = event.getEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        float f = 1 / ((float) (((iattributeinstance.getAttributeValue() / (double) event.getEntity().capabilities.getWalkSpeed() + 1.0D) / 2.0D)));

        EntityPlayerSP player = Minecraft.getMinecraft().player;

        float zoom = (float) (1 / MTConfig.modularArmor.modules.binocularZoom);

        if (Mouse.isButtonDown(1)) {
            ItemStack binoculars = MTMetaItems.BINOCULARS.getStackForm();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() != binoculars.getItem() || stack.getMetadata() != binoculars.getMetadata()) {
                stack = player.getHeldItemOffhand();
            }
            if (stack.getItem() == binoculars.getItem() && stack.getMetadata() == binoculars.getMetadata()) {
                event.setNewfov(event.getNewfov() * zoom * f);//*speedFOV;
                return;
            }
        }

        ItemStack helmet = player.inventory.armorInventory.get(3);
        ModularArmor modularArmor = ModularArmor.get(helmet);
        if (modularArmor != null) {
            NBTTagCompound armorData = ModularArmor.getArmorData(helmet);
            if (!armorData.getBoolean("zoom"))
                return;
            List<IModule> modules = ModularArmor.getModulesOf(helmet);
            for (IModule module : modules) {
                if (module instanceof Binoculars) {
                    event.setNewfov(event.getNewfov() * zoom * f);//*speedFOV;
                    break;
                }
            }
        }
    }
}
