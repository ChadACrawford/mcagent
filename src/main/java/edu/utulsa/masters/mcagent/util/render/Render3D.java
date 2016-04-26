package edu.utulsa.masters.mcagent.util.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import org.newdawn.slick.opengl.TextureImpl;

/**
 * cheat.edu.utulsa.masters.mcagent.util
 * <p/>
 * Created on 4/17/2015 by Matthew
 */
public class Render3D extends RenderBase
{
    /**
     * Translates the edu.utulsa.masters.mcagent.util.render position about the origin
     * @param origin The position of the player (usually)
     * @param start Position of the entity
     */
    public static void translate(Vec3 origin, Vec3 start)
    {
        GlStateManager.translate(start.xCoord - origin.xCoord,
                start.yCoord - origin.yCoord,
                start.zCoord - origin.zCoord);
    }

    /**
     * Rotates the renderer so that it rotates around the player
     */
    public static void rotate()
    {
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.f, 1.f, 0.f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.f, 0.f, 0.f);
        GlStateManager.scale(0.02, 0.02, 0.02);
        GlStateManager.rotate(180.f, 0.f, 0.f, 1.f);
    }

    /**
     * Scales based on the players distance from the position
     * @param pos Where the entity is located
     * @param scale
     */
    public static void scale(Vec3 pos, double scale)
    {
        double distance = mc.thePlayer.getDistance(pos.xCoord, pos.yCoord, pos.zCoord);
        if (distance < 3.34199) distance = 3.34199;
        GlStateManager.scale(distance / scale, distance / scale, distance / scale);
    }

    /**
     * Tick offset
     */
    private double offset;

    /**
     * Default constructor
     */
    public Render3D()
    {
        super(Tessellator.getInstance());
    }

    /**
     * Initializing constructor
     * @param tess a tessellator instance
     * @param worldRenderer the current renderer instance
     */
    public Render3D(Tessellator tess, WorldRenderer worldRenderer)
    {
        super(tess, worldRenderer);
    }

    /**
     * Initializing constructor. Uses the tessellator instance to get the renderer
     * @param tess a tessellator instance
     */
    public Render3D(Tessellator tess)
    {
        super(tess);
    }

    /**
     * Initializing constructor.
     * Initializes a font renderer from a ttf file
     * @param font fonts name
     * @param size how large the font should be
     * @param antiAlias if the font rendering should use anti aliasing
     */
    public Render3D(String font, int size, boolean antiAlias)
    {
        setFont(font, size, antiAlias);
    }

    /**
     * Get the current edu.utulsa.masters.mcagent.util.render position
     * @return edu.utulsa.masters.mcagent.util.render position
     */
    public Vec3 getRenderPos()
    {
        return new Vec3(mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * offset,
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * offset,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * offset);
    }

    /**
     * Set the current tick offset
     * @param of offset
     */
    public void setOffset(double of)
    {
        offset = of;
    }

    /**
     * Draws a 3D line in the world
     * @param start Start position
     * @param end End position
     * @param width Line thickness
     * @param smooth Anti aliasing
     * @param color Color of the line
     * @param depth If it should have depth
     */
    public void Line(Vec3 start, Vec3 end, float width, boolean smooth, Color color, boolean depth)
    {
        Vec3 playerOrigin = getRenderPos();

        GlStateManager.pushMatrix();

        GlStateManager.color(color.red / 255.f, color.green / 255.f, color.blue / 255.f, color.alpha / 255.f);

        if(!depth) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        if(smooth) GL11.glEnable(GL11.GL_LINE_SMOOTH);

        getRenderer().startDrawing(GL11.GL_LINES);
        GL11.glLineWidth(width);

        translate(playerOrigin, start);

        getRenderer().addVertex(0, 0, 0);
        getRenderer().addVertex(end.xCoord - start.xCoord,
                end.yCoord - start.yCoord,
                end.zCoord - start.zCoord);
        getTessellator().draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.color(1.f, 1.f, 1.f, 1.f);

        GlStateManager.popMatrix();
    }

    /**
     * Draws a 3D line
     * @param start Start position
     * @param end End position
     * @param color Color of the line
     * @param depth If it should have depth
     */
    public void Line(Vec3 start, Vec3 end, Color color, boolean depth)
    {
        Line(start, end, 2.f, true, color, depth);
    }

    /**
     * Draws a 2D outlined rectangle in the 3D world
     * @param origin Position
     * @param startX Starting position relative to the Position
     * @param startY Ending position relative to the Position
     * @param width How wide
     * @param height How high
     * @param color Color
     * @param depth If it should have depth
     */
    public void OutlinedRect2D(Vec3 origin, double startX, double startY, double width, double height, Color color, boolean depth)
    {
        Vec3 renderPos = getRenderPos();
        //GlStateManager.color(1.f, 1.f, 1.f, 1.f);
        GlStateManager.pushMatrix();
        GlStateManager.color(color.red / 255.f, color.green / 255.f, color.blue / 255.f, color.alpha / 255.f);

        GL11.glLineWidth(1.0f);

        translate(renderPos, origin);
        rotate();
        scale(origin, 20);

        //GL11.glEnable(GL11.GL_LINE_SMOOTH);

        getRenderer().startDrawing(GL11.GL_LINE_LOOP);
        getRenderer().addVertex(startX, startY, 0);
        getRenderer().addVertex(startX, height, 0);
        getRenderer().addVertex(width, height, 0);
        getRenderer().addVertex(width, startY, 0);
        getTessellator().draw();

        //GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(1.f, 1.f, 1.f, 1.f);
        GlStateManager.popMatrix();
    }

    /**
     * Draws a 2D outlined rectangle in the 3D world
     * @param origin Position
     * @param width How wide
     * @param height How high
     * @param alignmentX Alignment on the x axis
     * @param alignmentY Alignment on the y axis
     * @param color Color of the line
     * @param depth If it should have depth
     */
    public void OutlinedRect2D(Vec3 origin, float width, float height, int alignmentX, int alignmentY, Color color, boolean depth)
    {
        RenderPositionData pos = alignPos(alignmentX, alignmentY, width, height);
        OutlinedRect2D(origin, pos.getPosTopX(), pos.getPosTopY(), pos.getPosBotX(), pos.getPosBotY(), color, depth);
    }

    /**
     * Draw 2D text in the 3D world
     * @param text String to edu.utulsa.masters.mcagent.util.render
     * @param pos position to edu.utulsa.masters.mcagent.util.render at
     * @param color color
     * @param depth if it should have depth
     */
    public void Text(String text, Vec3 pos, Color color, boolean depth)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();

        if(!depth) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);

        translate(getRenderPos(), pos);
        rotate();
        scale(pos, 10.f);

        GlStateManager.color(color.red / 255.f, color.green / 255.f, color.blue / 255.f, color.alpha / 255.f);

        GlStateManager.bindTexture(TextureImpl.getLastBind().getTextureID());
        getFontRenderer().drawString(0, 0, text, new org.newdawn.slick.Color(color.red, color.green, color.blue));

        GlStateManager.color(1.f, 1.f, 1.f, 1.f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();
    }
}
