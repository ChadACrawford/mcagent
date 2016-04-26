package edu.utulsa.masters.mcagent.util.render;

/**
 * cheat.util
 * <p/>
 * Created on 4/17/2015 by Matthew
 */
public class Color
{
    private long hexColor;

    public int red;
    public int green;
    public int blue;
    public int alpha;

    public Color()
    {
        setColor(255, 255, 255, 255);
    }

    public Color(int r, int g, int b, int a)
    {
        setColor(r, g, b, a);
    }

    public Color(int r, int g, int b)
    {
        setColor(r, g, b);
    }

    public Color(int[] colors)
    {
        setColor(colors);
    }

    public Color(float r, float g, float b, float a)
    {
        setColor(r, g, b, a);
    }

    public Color(float r, float g, float b)
    {
        setColor(r, g, b);
    }

    public Color(float[] colors)
    {
        setColor(colors);
    }

    public void setColor(int r, int g, int b, int a)
    {
        red = clamp(r, 0, 255);
        green = clamp(g, 0, 255);
        blue = clamp(b, 0, 255);
        alpha = clamp(a, 0, 255);
        hexColor += (long)(red) << 24;
        hexColor += (long)(green) << 16;
        hexColor += (long)(blue) << 8;
        hexColor += (long)(alpha);
    }

    public void setColor(int r, int g, int b)
    {
        red = clamp(r, 0, 255);
        green = clamp(g, 0, 255);
        blue = clamp(b, 0, 255);
        alpha = 255;
        hexColor += (long)(red) << 16;
        hexColor += (long)(green) << 8;
        hexColor += (long)(blue);
    }

    public void setColor(int[] colors)
    {
        if(colors.length == 3)
            setColor(colors[0], colors[1], colors[2]);
        else if(colors.length == 4)
            setColor(colors[0], colors[1], colors[2], colors[3]);
    }

    public void setColor(float r, float g, float b, float a)
    {
        setColor((int)(r * 255), (int)(g * 255), (int)(b * 255), (int)(a * 255));
    }

    public void setColor(float r, float g, float b)
    {
        setColor((int)(r * 255), (int)(g * 255), (int)(b * 255));
    }

    public void setColor(float[] colors)
    {
        if(colors.length == 3)
            setColor(colors[0], colors[1], colors[2]);
        else if(colors.length == 4)
            setColor(colors[0], colors[1], colors[2], colors[3]);
    }

    public long getColorAsLong()
    {
        return hexColor;
    }

    public int getColorAsInt()
    {
        return clamp(hexColor, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public float[] getColorAsArray(boolean bAlpha)
    {
        if(bAlpha)
        {
            float[] color = new float[4];
            color[0] = red / 255.f;
            color[0] = green / 255.f;
            color[0] = blue / 255.f;
            color[0] = alpha / 255.f;
            return color;
        }
        else
        {
            float[] color = new float[3];
            color[0] = red / 255.f;
            color[0] = green / 255.f;
            color[0] = blue / 255.f;
            return color;
        }
    }

    private int clamp(float value, float min, float max)
    {
        return (int)Math.max(min, Math.min(max, value));
    }
}
