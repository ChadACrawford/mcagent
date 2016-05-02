package edu.utulsa.masters.shop2;

/**
 * Created by chad on 5/1/16.
 */
public abstract class Variable {
    Type type;

    boolean set = false;

    public boolean isSet() {
        return set;
    }

    public static enum Type {
        INTEGER,
        BOOLEAN,
        BLOCK,
        ITEM
    }

    protected Variable(Type type) {
        this.type = type;
    }
    protected Variable(Type type, boolean set) {
        this.type = type;
        this.set = set;
    }

    protected abstract Variable copy();

    public static class Integer extends Variable {
        public int value;
        public Integer() {
            super(Type.INTEGER);
        }
        public Integer(int value) {
            super(Type.INTEGER, true);
            this.value = value;
        }

        public void assign(int value) {
            this.set = true;
            this.value = value;
        }

        @Override
        protected Variable copy() {
            return isSet() ? new Integer(value) : new Integer();
        }

        @Override
        public String toString() {
            return String.format("%d", value);
        }
    }

    public static class Boolean extends Variable {
        public boolean value;

        protected Boolean() {
            super(Type.BOOLEAN);
        }
        protected Boolean(boolean value) {
            super(Type.BOOLEAN, true);
            this.value = value;
        }

        @Override
        protected Variable copy() {
            return null;
        }

        @Override
        public String toString() {
            return value ? "true" : "false";
        }
    }

    public static class Block extends Variable {
        public net.minecraft.block.Block block;
        public Block() {
            super(Type.BLOCK);
        }
        public Block(net.minecraft.block.Block block) {
            super(Type.BLOCK, true);
            this.block = block;
        }

        @Override
        protected Variable copy() {
            return isSet() ? new Block(block) : new Block();
        }

        @Override
        public String toString() {
            return block.getUnlocalizedName();
        }
    }

    public static class Item extends Variable {
        public net.minecraft.item.Item item;
        public Item() {
            super(Type.ITEM);
        }
        public Item(net.minecraft.item.Item item) {
            super(Type.ITEM, true);
            this.item = item;
        }

        @Override
        protected Variable copy() {
            return isSet() ? new Item(item) : new Item();
        }

        protected int getID() {
            return net.minecraft.item.Item.getIdFromItem(item);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Item && ((Item)o).getID() == getID();
        }

        @Override
        public String toString() {
            return item.getUnlocalizedName();
        }
    }
}
