package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.actuator.inventory.RecipeHelper;

/**
 * Crafts an item from the in-player dialog menu.
 */
public class ActionCraft extends PlayerControllerAction {
    RecipeHelper recipe;

    protected ActionCraft(PlayerController pc, RecipeHelper recipe) {
        super(pc);
        this.recipe = recipe;
    }

    @Override
    protected void performAction() {

    }
}
