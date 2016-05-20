package edu.utulsa.masters.mcagent;

import edu.utulsa.masters.mcagent.actuator.PlayerController;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chad on 5/17/16.
 */
public class PlayerCalibratorAgent extends MCAgent {
    public PlayerCalibratorAgent(EntityPlayerSP player) {
        super(player);
    }

    private CountDownLatch latch = new CountDownLatch(1);
    @Override
    public void exec() {
        overrideWindow = false;
        PlayerController.doOverrideKeys = false;

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        overrideWindow = true;
        PlayerController.doOverrideKeys = true;

        tickStage++;

        startPos = player.getPositionVector();
        latch = new CountDownLatch(1);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    int tickStage = 0;
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if(tickStage == 1) {
            if(testJumpingDriver())
                tickStage++;
        }
        else if(tickStage == 2) {
            latch.countDown();
        }
    }

    Vec3 startPos;

    JumpData[] jumpData;
    int currentJumpStage = 0;
    int subJumpStage = 0;
    int startTicks;
    Vec3 jumpToPos;
    protected boolean testJumpingDriver() {
        if(currentJumpStage >= 3) return false;

        if(jumpData == null) {
            jumpData = new JumpData[3];
            jumpToPos = new Vec3(startPos.xCoord, startPos.yCoord, startPos.zCoord + 100);
            for(int i = 0; i < 3; i++) jumpData[i] = new JumpData();
        }

        if(subJumpStage == 0) {
            if(currentJumpStage == 0) {
                debug.info("Testing walking.");
            }
            else if(currentJumpStage == 1) {
                debug.info("Testing sprinting.");
            }
            else {
                debug.info("Testing sneaking.");
            }
            startTicks = 1000;
            //run to start point
            double dist = pc.moveTo(startPos.xCoord, startPos.yCoord, startPos.zCoord);
            if(dist < 0.1) {
                subJumpStage++;
            }
        }
        else if(subJumpStage == 1) {
            if(currentJumpStage == 0) {
                pc.walk();
            }
            else if(currentJumpStage == 1) {
                pc.sprint();
            }
            else {
                pc.sneak();
            }
            pc.forward();
            debug.info(startTicks + "");
            startTicks--;
            if(startTicks <= 0) {
                currentJumpStage++;
            }
        }
        else if(subJumpStage == 2) {
            debug.info("Stage 3");
            if(currentJumpStage == 0) {
                pc.walk();
            }
            else if(currentJumpStage == 1) {
                pc.sprint();
            }
            else {
                pc.sneak();
            }
            pc.forward();
            if(testJumping(jumpData[currentJumpStage])) {
                currentJumpStage++;
                subJumpStage = 0;
            }
        }
        return false;
    }

    class JumpData {
        Vec3 origPos;
        Vec3 lastPos;
        double velocity;
        double distance;
        double maxHeight;
        int ticks;
        int stage;
    }
    protected boolean testJumping(JumpData current) {
        current.ticks++;
        current.velocity += pc.getCurrentVelocity();
        if(current.stage == 0) { //when the player begins
            pc.jump();
            current.origPos = player.getPositionVector();
            current.stage++;
        }
        else if(current.stage == 1) { //when the player starts falling
            if(current.lastPos.yCoord > player.posY) {
                current.maxHeight = current.lastPos.yCoord - current.origPos.yCoord;
                current.stage++;
            }
        }
        else if(current.stage == 2) {
            if(current.origPos.yCoord == player.posY) {
                current.distance = Math.sqrt(Math.pow(current.origPos.xCoord - player.posX, 2) + Math.pow(current.origPos.zCoord - player.posZ, 2));
                current.velocity /= current.ticks;
                current.stage++;
                return true;
            }
        }
        current.lastPos = player.getPositionVector();
        return false;
    }

    class Message {
        String user;
        String text;
        public Message(String user, String text) {
            this.user = user;
            this.text = text;
        }
    }

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
        Message message = processMessage(event.message);
        if(message == null) {
            debug.info("Error in regexp!");
            return;
        }
        if(message.text.equals("start calibration")) {
            latch.countDown();
        }
    }
}
