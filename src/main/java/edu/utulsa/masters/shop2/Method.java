package edu.utulsa.masters.shop2;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chad on 5/1/16.
 */
public abstract class Method {
    public final String head;
    protected final double cost;
    protected final boolean primitive;

    public Method(boolean primitive, String head, double cost) {
        this.head = head;
        this.cost = cost;
        this.primitive = primitive;
    }

    public abstract static class Primitive extends Method {
        public Primitive(String head, double cost) {
            super(true, head, cost);
        }
        public abstract Operator getOperator(PlayerState state, Variable... args);
    }

    public abstract static class Compound extends Method {
        public Compound(String head, double cost) {
            super(false, head, cost);
        }
        public abstract Task[] getSubtasks(PlayerState state, Variable... args);
    }

    public String getHead() {
        return head;
    }

    public boolean isPrimitive() {
        return primitive;
    }
    public boolean isCompound() {
        return !primitive;
    }

    public double getCost() {
        return cost;
    }

    public static class GetNothing extends Compound {
        public GetNothing() {
            super("get-item", 0);
        }

        @Override
        public Task[] getSubtasks(PlayerState state, Variable... args) {
            Variable.Integer amount = (Variable.Integer)args[1];
            if(amount.value > 0) {
                return null;
            }
            return new Task[0];
        }
    }

    /**
     * get-item %block %amount
     */
    public static class GetItemMine extends Primitive {
        Variable.Block block;
        Variable.Item item;
        Variable.Integer amount;
        public GetItemMine(Variable.Block block, Variable.Item item, Variable.Integer amount) {
            super("get-item", 1);
            this.block = block;
            this.item = item;
            this.amount = amount;
        }

        @Override
        public Operator getOperator(PlayerState state, Variable... args) {
            Variable.Item argItem = (Variable.Item)args[0];
            Variable.Integer count = (Variable.Integer)args[1];

            if(!argItem.equals(item)) return null;

            Variable.Integer total = new Variable.Integer(count.value * amount.value);
            return new Operator.MineBlock(block, item, total);
        }

        public String toString() {
            return String.format("get-item %s %s %d", item.toString(), block.toString(), amount.value);
        }
    }

    /**
     * get-item %block %amount
     */
    public static class GetItemCraft extends Compound {
        Variable.Item result;
        Variable.Integer resultAmount;
        Variable.Boolean workbenchRequired;
        Variable.Item[] required;
        Variable.Integer[] requiredAmount;
        public GetItemCraft(Variable.Item result, Variable.Integer resultAmount, Variable.Boolean workbenchRequired,
                         Variable.Item[] required, Variable.Integer[] requiredAmount, double cost) {
            super("get-item", cost);
            this.result = result;
            this.resultAmount = resultAmount;
            this.workbenchRequired = workbenchRequired;
            this.required = required;
            this.requiredAmount = requiredAmount;
        }

        @Override
        public Task[] getSubtasks(PlayerState state, Variable... args) {
            Variable.Item item = (Variable.Item)args[0];
            Variable.Integer amount = (Variable.Integer)args[1];

            if(!amount.isSet()) {
                amount.assign(0);
            }

            if(!item.equals(result)) {
                return null;
            }

            int numRepeats = amount.value / resultAmount.value;
            if(resultAmount.value < amount.value || numRepeats == 0) numRepeats++;

            Variable.Integer[] needed = new Variable.Integer[required.length];
            Condition[] conditions = new Condition[required.length];
            for(int i = 0; i < required.length; i++) {
                needed[i] = new Variable.Integer();
                conditions[i] = new Condition.NeedsItem(
                        required[i],
                        new Variable.Integer(requiredAmount[i].value * numRepeats),
                        needed[i]
                );
            }
            Condition c = new Condition.And(conditions);

            boolean r = c.evaluate(state);

            if(!r) return null;

            int windex = (workbenchRequired.value ? 1 : 0);
            Task[] tasks = new Task[required.length + 1 + windex];
            if(workbenchRequired.value) {
                tasks[0] = new Task("place-workbench");
            }
            for(int i = 0; i < required.length; i++) {
                tasks[i + windex] = new Task(
                        "get-item",
                        required[i],
                        needed[i]
                );
            }

            tasks[tasks.length-1] = new Task("craft-item", result, new Variable.Integer(numRepeats));

            return tasks;
        }

        public String toString() {
            return String.format("get-item %s %d", result.toString(), resultAmount.value);
        }
    }

    public static class CraftItem extends Primitive {
        Variable.Item result;
        Variable.Integer resultAmount;
        Variable.Boolean workbenchRequired;
        Variable.Item[] required;
        Variable.Integer[] requiredAmount;
        public CraftItem(Variable.Item result, Variable.Integer resultAmount, Variable.Boolean workbenchRequired,
                         Variable.Item[] required, Variable.Integer[] requiredAmount, double cost) {
            super("craft-item", cost);
            this.result = result;
            this.resultAmount = resultAmount;
            this.workbenchRequired = workbenchRequired;
            this.required = required;
            this.requiredAmount = requiredAmount;
        }

        @Override
        public Operator getOperator(PlayerState state, Variable... args) {
            Variable.Item item = (Variable.Item)args[0];
            Variable.Integer amount = (Variable.Integer)args[1];

            if(workbenchRequired.value && !state.workbenchPlaced || !item.equals(result)) {
                return null;
            }

            Condition[] conditions = new Condition[required.length];
            for(int i = 0; i < required.length; i++) {
                conditions[i] = new Condition.HasItem(
                        required[i],
                        new Variable.Integer(requiredAmount[i].value * amount.value)
                );
            }
            Condition c = new Condition.And(conditions);

            boolean r = c.evaluate(state);

            if(!r) return null;

            Variable.Integer total = new Variable.Integer(resultAmount.value * amount.value);

            return new Operator.CraftItem(result, total, required, requiredAmount);
        }

        public String toString() {
            return String.format("craft-item %s %d", result.toString(), resultAmount.value);
        }
    }

    public static class CraftAndPlaceWorkbench extends Compound {
        public CraftAndPlaceWorkbench() {
            super("place-workbench", 2);
        }

        @Override
        public Task[] getSubtasks(PlayerState state, Variable... args) {
            if(state.isWorkbenchPlaced()) return null;

            Variable.Item workbench = new Variable.Item(Item.getItemFromBlock(Blocks.crafting_table));
            Variable.Integer amount = new Variable.Integer(1);

            return new Task[] {
                    new Task("get-item", workbench, amount),
                    new Task("place-workbench")
            };
        }
    }

    public static class PlaceWorkbench extends Primitive {
        public PlaceWorkbench() {
            super("place-workbench", 0);
        }

        @Override
        public Operator getOperator(PlayerState state, Variable... args) {
            if(state.isWorkbenchPlaced()) return null;

            Variable.Item workbench = new Variable.Item(Item.getItemFromBlock(Blocks.crafting_table));
            Variable.Integer amount = new Variable.Integer(1);

            Condition condition = new Condition.HasItem(workbench, amount);

            boolean r = condition.evaluate(state);

            if(!r) return null;

            return new Operator.PlaceWorkbench();
        }
    }
}
