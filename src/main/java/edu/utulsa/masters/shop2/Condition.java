package edu.utulsa.masters.shop2;

import org.chocosolver.solver.Solver;

/**
 * Created by chad on 5/1/16.
 */
public abstract class Condition {
    public abstract boolean evaluate(PlayerState state);
    //protected abstract void setConditions(PlayerState state, Solver solver);


    /**
     * Conjunction of conditions.
     */
    public static class And extends Condition {
        Condition[] conditions;
        public And(Condition... conditions) {
            this.conditions = conditions;
        }

        @Override
        public boolean evaluate(PlayerState state) {
            for(Condition c: conditions) {
                if(!c.evaluate(state)) return false;
            }
            return true;
        }
    }

    /**
     * True if the player has `amount` of `item`.
     */
    public static class HasItem extends Condition {
        Variable.Item item;
        Variable.Integer amount;
        public HasItem(Variable.Item item, Variable.Integer amount) {
            this.item = item;
            this.amount = amount;
        }

        public boolean evaluate(PlayerState state) {
            if(!item.isSet()) return false;
            int has = state.itemAmount(item);
            if(!amount.isSet()) {
                amount.assign(has);
            }
            return has >= amount.value;
        }
    }

    /**
     * True if the player needs at least `amount` of `item`.
     */
    public static class NeedsItem extends Condition {
        Variable.Item item;
        Variable.Integer amount, needed;
        public NeedsItem(Variable.Item item, Variable.Integer amount, Variable.Integer needed) {
            this.item = item;
            this.amount = amount;
            this.needed = needed;
        }

        public boolean evaluate(PlayerState state) {
            if(!item.isSet()) return false;
            int has = state.itemAmount(item);
            if(!amount.isSet()) {
                amount.assign(has);
            }
            if(!needed.isSet()) {
                needed.assign(Math.max(0, amount.value - has));
            }
            return true;
        }
    }

}
