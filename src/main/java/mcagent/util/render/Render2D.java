package mcagent.util.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class Render2D extends RenderBase
{
    private int lengthLongestString;

    public Render2D()
    {
        super();
    }

    public Render2D(Tessellator tess, WorldRenderer worldRenderer)
    {
        super(tess, worldRenderer);
    }

    public Render2D(Tessellator tess)
    {
        super(tess);
    }

    public Render2D(String font, int size, boolean antiAlias)
    {
        setFont(font, size, antiAlias);
    }

    public void setLengthLongestString(int len)
    {
        if(len > lengthLongestString)
            lengthLongestString = len;
    }

    public int getLengthLongestString()
    {
        return lengthLongestString;
    }

    public int getTextHeight()
    {
        return (int)(getFontRenderer().getHeight());
    }

    public int getTextWidth(String str)
    {
        return (int)(getFontRenderer().getWidth(str));
    }

    public void Line(Vec3 start, Vec3 end, float width, boolean smooth, Color color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTextureStates();

        GlStateManager.color(color.red, color.green, color.blue, color.alpha);

        getRenderer().startDrawing(GL11.GL_LINES);
        GL11.glLineWidth(width);

        if(smooth) GL11.glEnable(GL11.GL_LINE_SMOOTH);

        getRenderer().addVertex(start.xCoord, start.yCoord, 0);
        getRenderer().addVertex(end.xCoord, end.yCoord, 0);
        getTessellator().draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTextureStates();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void Line(int startX, int startY, int endX, int endY, float width, boolean smooth, Color color)
    {
        Line(new Vec3(startX, startY, 0), new Vec3(endX, endY, 0), width, smooth, color);
    }

    public void Rect(Vec3 start, int width, int height, Color color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTextureStates();

        GlStateManager.color(color.red / 255.f, color.green / 255.f, color.blue / 255.f, color.alpha / 255.f);

        getRenderer().startDrawing(GL11.GL_QUADS);
        getRenderer().addVertex(start.xCoord, start.yCoord, 0);
        getRenderer().addVertex(start.xCoord, start.yCoord + height, 0);
        getRenderer().addVertex(start.xCoord + width, start.yCoord + height, 0);
        getRenderer().addVertex(start.xCoord + width, start.yCoord, 0);
        getTessellator().draw();

        GlStateManager.enableTextureStates();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void Rect(int startX, int startY, int width, int height, Color color)
    {
        Rect(new Vec3(startX, startY, 0), width, height, color);
    }

    public void OutlinedRect(Vec3 start, int width, int height, Color color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(color.red / 255.f, color.green / 255.f, color.blue / 255.f, color.alpha / 255.f);
        GlStateManager.disableTextureStates();

        GL11.glLineWidth(1.0f);

        getRenderer().startDrawing(GL11.GL_LINE_LOOP);
        getRenderer().addVertex(start.xCoord, start.yCoord, 0);
        getRenderer().addVertex(start.xCoord, start.yCoord + height, 0);
        getRenderer().addVertex(start.xCoord + width, start.yCoord + height, 0);
        getRenderer().addVertex(start.xCoord + width, start.yCoord, 0);
        getTessellator().draw();

        GlStateManager.enableTextureStates();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void OutlinedRect(int startX, int startY, int width, int height, Color color)
    {
        OutlinedRect(new Vec3(startX, startY, 0), width, height, color);
    }

    public void Text(String text, Vec3 pos, int offsetY, int alignX, int alignY, boolean shadow, Color color)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTextureStates();

        GlStateManager.scale(0.5f, 0.5f, 0.5f);

        GlStateManager.bindTexture(TextureImpl.getLastBind().getTextureID());

        RenderPositionData alignment = alignPos(alignX, alignY,
                getTextWidth(text),
                getTextHeight());

        int x = (int)pos.xCoord + (int)alignment.getPosTopX();
        int y = (int)pos.yCoord + getFontRenderer().getHeight() * offsetY;

        if(shadow) {
            // recursion = naughty naughty
            GlStateManager.color(0.f, 0.f, 0.f, 1.f);
            getFontRenderer().drawString(x + 1, y + 1, text, new org.newdawn.slick.Color(0, 0, 0));
        }

        GlStateManager.color(color.red / 255.f, color.green / 255.f, color.blue / 255.f, color.alpha / 255.f);
        getFontRenderer().drawString(x, y, text, new org.newdawn.slick.Color(color.red, color.green, color.blue));

        GlStateManager.color(1.f, 1.f, 1.f, 1.f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void Text(String text, int posX, int posY, int offsetY, int alignX, int alignY, Color color)
    {
        Text(text, new Vec3(posX, posY, 0), offsetY, alignX, alignY, true, color);
    }
}
