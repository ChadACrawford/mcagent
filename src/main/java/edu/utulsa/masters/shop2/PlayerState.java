package edu.utulsa.masters.shop2;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;

/**
 * Created by chad on 5/1/16.
 */
public class PlayerState {
    protected LinkedList<ItemStack> items = new LinkedList<ItemStack>();
    protected boolean workbenchPlaced;

    public PlayerState copy() {
        PlayerState s = new PlayerState();
        s.items = new LinkedList<ItemStack>(items);
        s.workbenchPlaced = workbenchPlaced;
        return s;
    }

    public int hunger() {
        return 0;
    }

    public int health() {
        return 0;
    }

    protected ItemStack getStack(Item item) {
        for(ItemStack stack: items) {
            if(Item.getIdFromItem(item) == Item.getIdFromItem(stack.getItem())) {
                return stack;
            }
        }
        return null;
    }

    public int itemAmount(Variable.Item i) {
        ItemStack stack = getStack(i.item);
        if(stack != null)
            return stack.stackSize;
        else
            return 0;
    }

    public void addItem(Variable.Item i, Variable.Integer amount) {
        ItemStack stack = getStack(i.item);
        if(stack != null)
            stack.stackSize += amount.value;
        else
            items.push(new ItemStack(i.item, amount.value));
    }

    public void subItem(Variable.Item i, Variable.Integer amount) {
        ItemStack stack = getStack(i.item);
        if(stack != null)
            stack.stackSize -= amount.value;
    }

    public void placeWorkbench() {
        workbenchPlaced = true;
    }

    public void unplaceWorkbench() {
        workbenchPlaced = false;
    }

    public boolean isWorkbenchPlaced() {
        return workbenchPlaced;
    }
}
