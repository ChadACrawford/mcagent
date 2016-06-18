package edu.utulsa.masters.mcagent;

import edu.utulsa.masters.mcagent.actuator.ActionMove;
import edu.utulsa.masters.mcagent.actuator.PlayerController;
import edu.utulsa.masters.mcagent.actuator.movement.MoveControl;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Testing movement module.
 */
@SideOnly(Side.CLIENT)
public class TestMovementAgent extends MCAgent {
    BlockPos start, end;

    boolean continueAction = true;

    protected TestMovementAgent(EntityPlayerSP player) {
        super(player);
    }

    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void exec() {
        while(stayAlive) {
            debug.message("info", "Waiting for player to choose block coords...");

            overrideWindow = false;
            PlayerController.doOverrideKeys = false;
            try {
                latch.await();

                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            debug.reset();

            overrideWindow = true;
            PlayerController.doOverrideKeys = true;

//            MoveControl mc = new MoveControl(pc, new Vec3(end.getX()+0.5, end.getY()+1, end.getZ()+0.5));
//
//            System.out.println(mc.dist());
//            do {
//                System.out.println(mc.dist());
//                mc.moveTo(end.getX()+0.5, end.getY(), end.getZ()+0.5);
//            } while(continueAction && mc.dist() > 1.0);

            ActionMove a = new ActionMove(pc, end.getX(), end.getY(), end.getZ());

            setAction(a);

            waitOnActionComplete();

            if(currentAction.getStatus() == ControllerStatus.FAILURE) {
                debug.info("Action failed!");
            }
            else {
                debug.info("Action completed!");
            }

            latch = new CountDownLatch(1);
            debug.info("Action complete.");
            continueAction = true;
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && latch.getCount() > 0) {
            BlockPos at = new BlockPos(event.pos);
            if(!at.equals(end)) {
                debug.format("Setting block at %s", at.toString());
                end = at;
            }
            //debug.debugBlock(at, Block.getStateById(57));
        }
    }

    class Message {
        String user;
        String text;
        public Message(String user, String text) {
            this.user = user;
            this.text = text;
        }
    }

//    @Override
//    @SubscribeEvent
//    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        super.onPlayerTick(event);
//        debug.format("Forward: %8.4f Strafe: %8.4f", pc.getPlayer().movementInput.moveForward, pc.getPlayer().movementInput.moveStrafe);
//    }

    Pattern chatPattern = Pattern.compile("<(.*)> (.*)");
    private Message processMessage(IChatComponent message) {
        Matcher m = chatPattern.matcher(message.getUnformattedText());
        if(m.matches()) {
            return new Message(m.group(1), m.group(2));
        }
        else {
            return null;
        }
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        //debug.format("Received message: '%s'", event.message.getUnformattedText());
        Message message = processMessage(event.message);
        if(message == null) {
            debug.info("Error in regexp!");
            return;
        }
        if(message.text.equals("stop")) {
            stopAction();
        }
        if(message.text.equals("start") && end != null) {
            latch.countDown();
        }
    }
}
