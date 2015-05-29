package mcagent;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chad on 5/26/2015.
 */
public class Debugger {
    private static Debugger instance;
    EntityPlayerSP player;
    World world;
    private Debugger() {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.world = Minecraft.getMinecraft().theWorld;
    }
    public static Debugger getInstance() {
        if(instance == null)
            instance = new Debugger();
        return instance;
    }

    class BlockData {
        public BlockPos pos;
        public IBlockState state;
        public BlockData(BlockPos pos, IBlockState state) {
            this.pos = pos;
            this.state = state;
        }
    }
    HashMap<Object, List<BlockData>> debugBlocks = new HashMap<Object, List<BlockData>>();
    public void debugBlock(Object o, BlockPos b, IBlockState type) {
        if(!debugBlocks.containsKey(o)) debugBlocks.put(o, new LinkedList<BlockData>());
        List<BlockData> items = debugBlocks.get(o);
        items.add(new BlockData(b, world.getBlockState(b)));
        world.setBlockState(b, type);
    }

    public void reset(Object o) {
        if(!debugBlocks.containsKey(o)) return;
        for(BlockData b: debugBlocks.get(o)) {
            world.setBlockState(b.pos, b.state);
        }
        debugBlocks.remove(o);
    }
}
