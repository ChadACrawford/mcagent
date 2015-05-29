package autoplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tools.WorldTools;

import java.util.Random;

/**
 * Created by Chad on 3/28/2015.
 */
//@SideOnly(Side.CLIENT)
//@Mod(modid = AutoPlayer.MODID, version = AutoPlayer.VERSION)
public class AutoPlayer {
    public static final String MODID = "Auto Player";
    public static final String VERSION = "0.1";

    PlayerController pc;

    //@Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(new AutoPlayer());
    }


    private double x,y,z;
    private long startTime = 0;
    private Random rand = new Random();
    Path p;
    //@SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
        //initialization
        if(pc == null) pc = new PlayerController(Minecraft.getMinecraft(), Minecraft.getMinecraft().thePlayer);
        if(pc.p.getPosition() == null) return;
        if(pc.delay()) return;
        World w = Minecraft.getMinecraft().theWorld;
        if(!w.isAreaLoaded(pc.p.getPosition(),10)) return;
//        if(Minecraft.getSystemTime()-startTime > 20000) {
//            startTime = Minecraft.getSystemTime();
//            x = rand.nextInt(10)-20+e.player.posX;
//            y = rand.nextInt(10)+e.player.posY-1;
//            z = rand.nextInt(10)-20+e.player.posZ;
////            x = 10+e.player.posX;
////            y = e.player.posY;
////            z = e.player.posZ;
//            System.out.format("Now heading towards (%f,%f,%f)\n",x,y,z);
//            w.setBlockState(new BlockPos(x, y, z), Block.getStateById(57));
//        }
//        else {
//            w.setBlockState(new BlockPos(x, y, z), Block.getStateById(57));
//            pc.moveTo(x + 0.5, y,     z + 0.5);
//            pc.lookAt(x + 0.5, y-1, z + 0.5);
//        }
        if(p == null || p.status == Status.FINISHED || p.getStatus() == Status.FAILED) {
            x = rand.nextInt(100)-50+e.player.posX;
            z = rand.nextInt(100)-50+e.player.posZ;
            //System.out.println(w.getHeight());
            BlockPos b;
            b = WorldTools.findGroundBlock(w, new BlockPos(x,w.getHeight()-1,z));
            if(b == null) return;
            p = Path.greedyPath(pc, Minecraft.getMinecraft().theWorld, pc.p.getPosition().add(0,-1,0), b);
            //if(p != null) p.drawPath();
        }
        else {
            p.act();
        }//*/
        /*if(Math.random()<0.99) return;
        for(int i = -20; i < 20; i++) {
            for(int j = -20; j < 20; j++) {
                x = e.player.posX+i;
                y = e.player.posY-1;
                z = e.player.posZ+j;
                BlockPos to = new BlockPos(x,y,z);
                if(WorldTools.isValidPath(w,pc.p.getPosition().add(0,-1,0),to)) {
                    w.setBlockState(to, Block.getStateById(133));
                }
                else {
                    w.setBlockState(to, Block.getStateById(0));
                }
            }
        }//*/
    }
}
