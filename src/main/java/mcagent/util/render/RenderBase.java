package mcagent.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;
import java.io.InputStream;

/**
 * cheat.mcagent.util
 * <p/>
 * Created on 4/18/2015 by Matthew
 */
public class RenderBase
{
    protected static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Align to the center
     */
    public static final int ALIGN_CENTER = 0;

    /**
     * Align to the left
     */
    public static final int ALIGN_LEFT = 1;

    /**
     * Align to the right
     */
    public static final int ALIGN_RIGHT = 2;

    private Tessellator tessellator;
    private WorldRenderer renderer;

    /**
     * Draws text with a given font
     */
    private TrueTypeFont fontRenderer;

    /**
     * Default constructor
     */
    public RenderBase()
    {
        tessellator = null;
        renderer = null;
    }

    /**
     * Initializing constructor
     * @param tess a tessellator instance
     * @param worldRenderer the current renderer instance
     */
    public RenderBase(Tessellator tess, WorldRenderer worldRenderer)
    {
        tessellator = tess;
        renderer = worldRenderer;
    }

    /**
     * Initializing constructor. Uses the tessellator instance to get the renderer
     * @param tess a tessellator instance
     */
    public RenderBase(Tessellator tess)
    {
        tessellator = tess;
        setRenderer(tessellator.getWorldRenderer());
    }

    /**
     * Initializing constructor.
     * Initializes a font renderer from a ttf file
     * @param font fonts name
     * @param size how large the font should be
     * @param antiAlias if the font rendering should use anti aliasing
     */
    public RenderBase(String font, int size, boolean antiAlias)
    {
        setFont(font, size, antiAlias);
    }

    /**
     * Sets the current tesselator instance and the world renderer
     * @param tess a tessellator instance
     */
    public void setTessellator(Tessellator tess)
    {
        tessellator = tess;
        setRenderer(tessellator.getWorldRenderer());
    }

    /**
     * Gets the current tessellator instance
     * @return tessellator instance
     */
    public Tessellator getTessellator()
    {
        return tessellator;
    }

    /**
     * Set the renderer instance
     * @param worldRenderer the renderer instance
     */
    public void setRenderer(WorldRenderer worldRenderer)
    {
        renderer = worldRenderer;
    }

    /**
     * Get the current renderer instance
     * @return the current renderer instance
     */
    public WorldRenderer getRenderer()
    {
        return renderer;
    }

    /**
     * Setup the font renderer
     * @param fontName name of the font to use
     * @param size size of the font
     * @param antiAlias if the font rendering should use anti aliasing
     */
    public void setFont(String fontName, int size, boolean antiAlias)
    {
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream(fontName + ".ttf");
            java.awt.Font awtFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
            awtFont = awtFont.deriveFont(size + 0.f);
            fontRenderer = new TrueTypeFont(awtFont, antiAlias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current font rendering instance
     * @return font rendering instance
     */
    public TrueTypeFont getFontRenderer()
    {
        return fontRenderer;
    }

    /**
     * Disables GL settings that will mess up the renderer
     */
    public static void EnableDrawingMode()
    {
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        //GlStateManager.color(1.f, 1.f, 1.f, 1.f);
    }

    /**
     * Re-enable those settings
     */
    public static void DisableDrawingMode()
    {
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.f, 1.f, 1.f, 1.f);
    }

    public static RenderPositionData alignPos(int alignmentX, int alignmentY, int alignmentZ, double posX, double posY, double posZ)
    {
        RenderPositionData pos = new RenderPositionData();
        int[] alignments = {alignmentX, alignmentY, alignmentZ};
        double[] positions = {posX, posY, posZ};
        for(int i = 0; i < alignments.length; i++)
        {
            switch(alignments[i])
            {
                case RenderBase.ALIGN_CENTER:
                {
                    pos.topPos[i] = -positions[i] / 2.f;
                    pos.botPos[i] = positions[i] / 2.f;
                    break;
                }
                case RenderBase.ALIGN_LEFT:
                {
                    pos.topPos[i] = -positions[i];
                    pos.botPos[i] = 0.f;
                    break;
                }
                case RenderBase.ALIGN_RIGHT:
                default:
                {
                    pos.topPos[i] = 0.f;
                    pos.botPos[i] = positions[i];
                    break;
                }
            }
        }
        return pos;
    }

    public static RenderPositionData alignPos(int alignmentX, int alignmentY, double posX, double posY)
    {
        return alignPos(alignmentX, alignmentY, 0, posX, posY, 0.f);
    }

    public static class RenderPositionData
    {
        public double topPos[];
        public double botPos[];

        public RenderPositionData()
        {
            topPos = new double[3];
            botPos = new double[3];
        }

        public double getPosTopX()
        {
            return topPos[0];
        }

        public double getPosTopY()
        {
            return topPos[1];
        }

        public double getPosTopZ()
        {
            return topPos[2];
        }

        public double getPosBotX()
        {
            return botPos[0];
        }

        public double getPosBotY()
        {
            return botPos[1];
        }

        public double getPosBotZ()
        {
            return botPos[2];
        }
    }
}
