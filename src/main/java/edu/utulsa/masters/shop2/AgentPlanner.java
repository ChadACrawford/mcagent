package edu.utulsa.masters.shop2;

import edu.utulsa.masters.mcagent.actuator.inventory.PlayerInventory;
import edu.utulsa.masters.mcagent.actuator.inventory.RecipeHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.*;

/**
 * Planning algorithm using GraphPlan.
 *
 * I'm going to try implementing the SHOP2 planner. Let's see how that turns out.
 */
public class AgentPlanner {
    public static HashMap<String, LinkedList<Method>> methods = new LinkedHashMap<String, LinkedList<Method>>();

    public static void addMethod(Method m) {
        if(!methods.containsKey(m.getHead())) {
            methods.put(m.getHead(), new LinkedList<Method>());
        }
        methods.get(m.getHead()).push(m);
    }

    private static void addRecipe(RecipeHelper recipe) {
        if(recipe.result == null || recipe.required.length <= 0 || recipe.requiredAmounts.length <= 0)
            return;
        if(Item.getIdFromItem(recipe.result.getItem()) == 54) {
            System.out.println("Hi");
        }
        int numIngedients = recipe.requiredAmounts.length;
        Variable.Boolean workbenchRequired = new Variable.Boolean(recipe.required.length > 4);
        Variable.Item[] requiredItems = new Variable.Item[numIngedients];
        Variable.Integer[] requiredAmounts = new Variable.Integer[numIngedients];
        double cost = 0;
        for(int i = 0; i < numIngedients; i++) {
            requiredItems[i] = new Variable.Item(recipe.requiredAmounts[i].getItem());
            requiredAmounts[i] = new Variable.Integer(recipe.requiredAmounts[i].stackSize);
            cost += recipe.requiredAmounts[i].stackSize;
        }
        Variable.Item result = new Variable.Item(recipe.result.getItem());
        Variable.Integer resultAmount = new Variable.Integer(recipe.result.stackSize);
        addMethod(new Method.GetItemCraft(result, resultAmount, workbenchRequired, requiredItems, requiredAmounts, cost));
        addMethod(new Method.CraftItem(result, resultAmount, workbenchRequired, requiredItems, requiredAmounts, cost));
    }

    public static void initialize() {
        for(RecipeHelper recipe: PlayerInventory.getRecipes()) {
            addRecipe(recipe);
        }

//        for(Object key: GameData.getItemRegistry().getKeys()) {
//            Object value = GameData.getItemRegistry().getObject(key);
//            if(value instanceof ItemBlock) {
//                Variable.Item item = new Variable.Item((Item)value);
//                Variable.Block block = new Variable.Block(Block.getBlockFromItem(item.item));
//                Variable.Integer amount = new Variable.Integer(1);
//                addMethod(new Method.GetItemMine(block, item, amount));
//            }
//        }
        int[] ids = new int[] {1, 2, 3, 4, 17, 18};
        for(int id: ids) {
            Item itemObj = Item.getItemById(id);
            Variable.Item item = new Variable.Item(itemObj);
            Variable.Block block = new Variable.Block(Block.getBlockFromItem(itemObj));
            Variable.Integer amount = new Variable.Integer(1);
            addMethod(new Method.GetItemMine(block, item, amount));
        }

        addMethod(new Method.CraftAndPlaceWorkbench());
        addMethod(new Method.PlaceWorkbench());
    }

    public AgentPlanner() {
    }

    public LinkedList<Operator> plan(Task task) {
        PlayerState initialState = new PlayerState();
        LinkedList<Task> tasks = new LinkedList<Task>();
        tasks.add(task);
        return seekPlan(initialState, tasks);
    }

    public LinkedList<Method> match(Task task, PlayerState state) {
        return methods.get(task.getHead());
    }

    public LinkedList<Operator> seekPlan(PlayerState currentState, LinkedList<Task> tasks) {
        if(tasks.size() <= 0) {
            return new LinkedList<Operator>();
        }
        LinkedList<Task> nextTasks = new LinkedList<Task>(tasks);
        Task t0 = nextTasks.pop();
        LinkedList<Method> methods = match(t0, currentState);
        if(methods == null) return null;
        for(Method m: methods) {
            if(m.isPrimitive()) {
                Operator o = ((Method.Primitive)m).getOperator(currentState, t0.getVariables());
                if(o == null) {
                    continue;
                }
                PlayerState newState = o.act(currentState);
                if(newState != null) {
                    LinkedList<Operator> plan = seekPlan(newState, nextTasks);
                    if(plan != null) {
                        plan.addFirst(o);
                        return plan;
                    }
                }
            }
            else {
                Task[] subtasks = ((Method.Compound)m).getSubtasks(currentState, t0.getVariables());
                if(subtasks == null) {
                    continue;
                }
                LinkedList<Task> newTasks = new LinkedList<Task>();
                newTasks.addAll(Arrays.asList(subtasks));
                newTasks.addAll(nextTasks);
                LinkedList<Operator> plan = seekPlan(currentState, newTasks);
                if(plan != null) {
                    return plan;
                }
            }
        }
        return null;
    }
}
