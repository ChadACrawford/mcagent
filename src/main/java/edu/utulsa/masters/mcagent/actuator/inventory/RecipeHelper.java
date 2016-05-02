package edu.utulsa.masters.mcagent.actuator.inventory;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;
import java.util.List;

/**
 * Created by chad on 4/27/16.
 */
public class RecipeHelper {
    public IRecipe recipe;
    public ItemStack[] required;
    public ItemStack[] requiredAmounts;
    public ItemStack result;

    public RecipeHelper(IRecipe recipe) {
        this.recipe = recipe;

        this.result = recipe.getRecipeOutput();

        if(result != null && Item.getIdFromItem(result.getItem()) == 54) {
            System.out.println("hi");
        }

        if(recipe instanceof ShapedRecipes) {
            ShapedRecipes r = (ShapedRecipes)recipe;
            this.required = r.recipeItems;
        }
        else if(recipe instanceof ShapedOreRecipe) {
            ShapedOreRecipe r = (ShapedOreRecipe)recipe;
            Object[] input = r.getInput();
            this.required = new ItemStack[input.length];
            for(int i = 0; i < input.length; i++) {
                if(input[i] instanceof ItemStack) {
                    this.required[i] = (ItemStack) input[i];
                }
                else if(input[i] instanceof List) {
                    this.required[i] = (ItemStack)((List) input[i]).get(0);
                }
            }
        }
        else {
            required = new ItemStack[0];
        }
        setRequiredAmounts();
    }

    private void setRequiredAmounts() {
        if(required.length <= 0) {
            requiredAmounts = new ItemStack[0];
            return;
        }
        HashMap<String, ItemStack> items = new HashMap<String, ItemStack>();
        for(ItemStack stack: required) {
            if(stack == null) continue;
            String itemName = stack.getItem().getUnlocalizedName();
            if( !items.containsKey(itemName) ) {
                items.put(itemName, new ItemStack(stack.getItem(), 0));
            }
            items.get(itemName).stackSize += stack.stackSize;
        }
        requiredAmounts = items.values().toArray(new ItemStack[items.size()]);
    }

    public boolean canCraft(PlayerInventory pi) {
        for(ItemStack requirement: requiredAmounts) {
            if( pi.getItemAmount(requirement.getItem()) < requirement.stackSize )
                return false;
        }
        return true;
    }

    public boolean craft(PlayerInventory pi) {
        Slot[] craftingSlots = pi.craftingSlots();
        if(craftingSlots.length != required.length + 1 || !canCraft(pi)) {
            return false;
        }

        // Place items from inventory into crafting slots
        for(int i = 0; i < required.length; i++) {
            if(required != null) {
                pi.placeItemsInSlot(craftingSlots[i + 1], required[i].getItem(), required[i].stackSize);
            }
        }

        // Initially move all items from the crafting slot into the inventory, then move any extra items out as well.
        for(int i = 0; i < craftingSlots.length; i++) {
            pi.mergeSlotIntoInventory(craftingSlots[i]);
        }

        return true;
    }
}
