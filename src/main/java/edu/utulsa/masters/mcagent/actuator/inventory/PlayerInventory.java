package edu.utulsa.masters.mcagent.actuator.inventory;

import edu.utulsa.masters.mcagent.actuator.PlayerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by chad on 4/26/16.
 */
public class PlayerInventory {
    PlayerController pc;

    // As far as I can tell, this persists.
    // This the the inventory that the player accesses when they press "e".
    Container playerContainer;

    public PlayerInventory(PlayerController pc) {
        this.pc = pc;
        this.playerContainer = pc.getPlayer().inventoryContainer;
    }

    public boolean swapSlots(Slot slot1, Slot slot2) {
        ItemStack stack1 = slot1.getHasStack() ? slot1.getStack() : null;
        ItemStack stack2 = slot2.getHasStack() ? slot2.getStack() : null;
        if(stack1 == null && stack2 == null) {
            return false;
        }
        else if(stack1 != null && stack2 != null) {
            slot1.decrStackSize(stack1.stackSize);
            slot2.decrStackSize(stack2.stackSize);
            slot1.putStack(stack2);
            slot2.putStack(stack1);
            return true;
        }
        else if(stack1 == null) {
            slot2.decrStackSize(stack2.stackSize);
            slot1.putStack(stack2);
            return true;
        }
        else {
            slot1.decrStackSize(stack1.stackSize);
            slot2.putStack(stack1);
        }
        return false;
    }

    public boolean currentlyInInventory() {
        Gui currentScreen = Minecraft.getMinecraft().currentScreen;
        return currentScreen instanceof GuiContainer &&
                ((GuiContainer)currentScreen).inventorySlots instanceof ContainerPlayer;
    }

    public boolean currentlyInWorkbench() {
        Gui currentScreen = Minecraft.getMinecraft().currentScreen;
        return currentScreen instanceof GuiContainer &&
                ((GuiContainer)currentScreen).inventorySlots instanceof ContainerWorkbench;
    }

    public boolean craft() {
        Slot craftingStack = playerContainer.getSlot(0);
        return false;
    }
}
