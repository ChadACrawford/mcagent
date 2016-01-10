package mcagent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Chad on 5/26/2015.
 */
public class Debugger {
    EntityPlayerSP player;
    World world;
    Object o;
    boolean verbose = true;

    public Debugger(Object o) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.world = Minecraft.getMinecraft().theWorld;
        this.o = o;
    }
    public Debugger(Object o, boolean verbose) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.world = Minecraft.getMinecraft().theWorld;
        this.o = o;
        this.verbose = verbose;
    }

    HashMap<BlockPos, IBlockState> debugBlocks = new HashMap<BlockPos, IBlockState>();
    public void debugBlock(BlockPos b, IBlockState type) {
        if(!verbose) return;
        if(!debugBlocks.containsKey(b)) debugBlocks.put(b, world.getBlockState(b));
        world.setBlockState(b, type);
    }

    public void resetBlock(BlockPos b) {
        if(debugBlocks.containsKey(b)) world.setBlockState(b, debugBlocks.get(b));
    }

    public void reset() {
        for(BlockPos b: debugBlocks.keySet()) {
            world.setBlockState(b, debugBlocks.get(b));
        }
        debugBlocks.clear();
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private String time() {
        return dateFormat.format(new Date());
    }

    public void error(String msg) {
        message("ERROR", msg);
    }

    public void errorf(String format, Object... s) {
        info(String.format(format, s));
    }

    public void info(String msg) {
        message("INFO", msg);
    }
    public void format(String format, Object... s) {
        info(String.format(format, s));
    }

    public void message(String status, String msg) {
        if(verbose) System.out.format("[%s] [MinecraftAgent/%s] [%s]: %s\n", time(), status, o.toString(), msg);
    }

    public void setVerbosity(boolean verbose) {
        this.verbose = verbose;
    }
}
