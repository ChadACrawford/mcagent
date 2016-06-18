package edu.utulsa.masters.mcagent.actuator.inventory;

import edu.utulsa.masters.mcagent.actuator.PlayerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chad on 4/26/16.
 */
public class PlayerInventory {
    PlayerController pc;

    // As far as I can tell, this persists.
    // This the the inventory that the player accesses when they press "e".
    ContainerPlayer playerContainer;

    static List<RecipeHelper> recipes = new ArrayList<RecipeHelper>();

    public static void loadRecipes() {
        List newRecipes = CraftingManager.getInstance().getRecipeList();
        for(Object recipe: newRecipes) {
            if(recipe instanceof IRecipe) {
                recipes.add(new RecipeHelper((IRecipe)recipe));
            }
        }
    }

    public static List<RecipeHelper> getRecipes() {
        return recipes;
    }

    public PlayerInventory(PlayerController pc) {
        this.pc = pc;
        this.playerContainer = (ContainerPlayer)pc.getPlayer().inventoryContainer;
    }

    /**
     * Moves the items from the given source slot into the player's inventory.
     * @param source The source slot.
     * @return The number of items left.
     */
    public int mergeSlotIntoInventory(Slot source) {
        if(!source.getHasStack()) return 0;
        ItemStack stack = source.getStack();

        LinkedList<Slot> sinks = getSlotsWithItem(stack.getItem());
        for(Slot sink: sinks) {
            mergeIntoSlot(source, sink, stack.stackSize);
            if(stack.stackSize <= 0) return 0;
        }

        if(stack.stackSize > 0) {
            for(Slot sink: getEmptySlots()) {
                mergeIntoSlot(source, sink, stack.stackSize);
                if(stack.stackSize <= 0) return 0;
            }
        }

        return stack.stackSize;
    }

    /**
     * Places an item from the player's inventory into the target slot.
     * @param target The target slot.
     * @param item The desired item.
     * @return If the operation was a success.
     */
    public boolean placeItemsInSlot(Slot target, Item item, int amount) {
        if(target.isItemValid(new ItemStack(item)))
            return false;

        LinkedList<Slot> sources = getSlotsWithItem(item);

        int totalAmount = 0;
        for(Slot s: sources) totalAmount += s.getStack().stackSize;
        if(totalAmount < amount) return false;

        for(Slot s: sources) {
            ItemStack stack = s.getStack();
            if(stack.stackSize < amount) {
                amount -= stack.stackSize;
                target.putStack(stack.splitStack(stack.stackSize));
            }
            else {
                target.putStack(stack.splitStack(amount));
                break;
            }
        }

        return true;
    }

    /**
     * Returns the amount of an item that the player has.
     * @param item
     * @return
     */
    public int getItemAmount(Item item) {
        int amount = 0;
        for(Slot s: getSlotsWithItem(item)) amount += s.getStack().stackSize;
        return amount;
    }

    public LinkedList<Slot> getInventorySlots() {
        LinkedList<Slot> slots = new LinkedList<Slot>();
        for(int i = 5; i < 45; i++) { //iterate over slots in the player's inventory
            Slot slot = playerContainer.getSlot(i);
            slots.add(slot);
        }
        return slots;
    }

    /**
     * Grabs all slots with a given item from the player's inventory.
     * @param item The item to search for.
     * @return List of slots with the given item.
     */
    public LinkedList<Slot> getSlotsWithItem(Item item) {
        LinkedList<Slot> slots = new LinkedList<Slot>();
        for(Slot slot: getInventorySlots()) {
            if(slot.getHasStack() && slot.getStack().getItem().equals(item)) {
                slots.add(slot);
            }
        }
        return slots;
    }

    public LinkedList<Slot> getEmptySlots() {
        LinkedList<Slot> slots = new LinkedList<Slot>();
        for(Slot slot: getInventorySlots()) {
            if(!slot.getHasStack()) {
                slots.add(slot);
            }
        }
        return slots;
    }



    /**
     * Moves items from a source slot into the target slot. If the target slot has fewer items than the specified
     * amount, it moves as many items as possible.
     * @param source The source slot.
     * @param target The target slot.
     * @param amount The amount of items to be moved.
     * @return The number of items that were moved.
     */
    public int mergeIntoSlot(Slot source, Slot target, int amount) {
        if(!source.getHasStack() || source.getStack().stackSize < amount ||
                !target.isItemValid(source.getStack()))
            return 0;

        int maxAvailable = target.getSlotStackLimit() - target.getStack().stackSize;

        if(amount > maxAvailable) {
            target.putStack(source.getStack().splitStack(maxAvailable));
            return maxAvailable;
        }
        else {
            target.putStack(source.getStack().splitStack(amount));
            return amount;
        }
    }

    /**
     * Grabs all slots available for crafting, with the first item being the SlotCrafting slots.
     * @return Array of crafting slots.
     */
    public Slot[] craftingSlots() {
        if( currentlyInInventory() ) {
            Slot[] slots = new Slot[5];
            for(int i = 0; i < 5; i++) {
                slots[i] = playerContainer.getSlot(i);
            }
            return slots;
        }
        else if( currentlyInWorkbench() ) {
            Slot[] slots = new Slot[10];
            for(int i = 0; i < 10; i++) {
                slots[i] = playerContainer.getSlot(i);
            }
            return slots;
        }
        else {
            return null;
        }
    }

    /**
     * Returns the size of the current inventory.
     * @return
     */
    public int craftingDimensions() {
        if( currentlyInInventory() ) {
            return 4;
        }
        else if( currentlyInWorkbench() ) {
            return 9;
        }
        return 0;
    }

    public boolean swapSlots(Slot slot1, Slot slot2) {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;

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
            return mergeIntoSlot(slot2, slot1, stack2.stackSize) > 0;
        }
        else {
            return mergeIntoSlot(slot1, slot2, stack1.stackSize) > 0;
        }
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

    Method handleMouseClick;

    /**
     *
     * @param slotIn
     * @param clickedButton
     * @param clickType The mode, which is important i guess. Modes are:
     *                  0 - Basic click
     *                  1 - Shift click
     *                  2 - Hotbar
     *                  3 - Pick block
     *                  4 - Drop
     *                  5 - ?
     *                  6 - Double click
     */
    public void mouseClick(Slot slotIn, int clickedButton, int clickType) {
        try {
            GuiContainer gui = (GuiContainer)Minecraft.getMinecraft().currentScreen;
            handleMouseClick.invoke(gui, slotIn, 0, clickedButton, clickType);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
