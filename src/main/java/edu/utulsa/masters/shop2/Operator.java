package edu.utulsa.masters.shop2;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

/**
 * Created by chad on 5/1/16.
 */
public abstract class Operator {
    public abstract PlayerState act(PlayerState s);

    public static class NOOP extends Operator {
        @Override
        public PlayerState act(PlayerState s) {
            return s.copy();
        }
    }

    public static class MineBlock extends Operator {
        Variable.Block block;
        Variable.Item item;
        Variable.Integer amount;
        public MineBlock(Variable.Block block, Variable.Item item, Variable.Integer amount) {
            this.block = block;
            this.item = item;
            this.amount = amount;
        }

        @Override
        public PlayerState act(PlayerState s) {
            PlayerState ns = s.copy();
            ns.addItem(item, amount);
            return ns;
        }

        public String toString() {
            return String.format("mine-block %s %s %d", block.toString(), item.toString(), amount.value);
        }
    }

    public static class CraftItem extends Operator {
        Variable.Item item;
        Variable.Integer amount;
        Variable.Item[] required;
        Variable.Integer[] requiredAmount;

        public CraftItem(Variable.Item item, Variable.Integer amount, Variable.Item[] required, Variable.Integer[] requiredAmount) {
            this.item = item;
            this.amount = amount;
            this.required = required;
            this.requiredAmount = requiredAmount;
        }

        @Override
        public PlayerState act(PlayerState s) {
            PlayerState ns = s.copy();
            for(int i = 0; i < required.length; i++) {
                ns.subItem(required[i], requiredAmount[i]);
            }
            ns.addItem(item, amount);
            return ns;
        }

        @Override
        public String toString() {
            return String.format("craft-item %s %d", item.toString(), amount.value);
        }
    }

    public static class PlaceWorkbench extends Operator {
        public PlaceWorkbench() {
            //do nothing!!!
        }

        @Override
        public PlayerState act(PlayerState s) {
            PlayerState ns = s.copy();
            Variable.Item craftingTable = new Variable.Item(Item.getItemFromBlock(Blocks.crafting_table));
            if(ns.itemAmount(craftingTable) <= 0) {
                return null;
            }
            ns.subItem(craftingTable, new Variable.Integer(1));
            ns.placeWorkbench();
            return ns;
        }

        public String toString() { return "place-workbench"; }
    }
}
