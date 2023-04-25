package game.objects.ui;

import com.raylib.java.core.Color;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.shapes.rShapes;

import static com.raylib.java.shapes.rShapes.DrawRectangleRec;
import static game.Window.properties.rl;

public class TextBox {
    public Color borderColour;
    public Color backgroundColour;
    public String placeholderText;
    public int[] position;
    public int width;
    public int height;
    public Rectangle rectangle;
    public int textSize;
    public int[] textPosition;
    public Color textColour;
    public String text;
    public String drawText;
    public int borderThickness;
    public boolean hasText = false;
    public boolean selected = false;

    public TextBox(int x, int y, int bWidth, int bHeight, int bThickness, Color bgColour, Color brdColour, String bPlaceholderText, int bTextSize, Color colourText) {
        borderColour = brdColour;
        backgroundColour = bgColour;
        position = new int[2];
        position[0] = x;
        position[1] = y;
        width = bWidth;
        height = bHeight;
        textSize = bTextSize;
        textColour = colourText;
        borderThickness = bThickness;
        rectangle = new Rectangle(x, y, bWidth, bHeight);
        textPosition = new int[2];
        textPosition[0] = position[0] + bThickness + 2;
        textPosition[1] = position[1] + ((height / 2) - (textSize / 2));
        placeholderText = bPlaceholderText;
    }

    public void draw() {
        DrawRectangleRec(rectangle, backgroundColour);
        rl.shapes.DrawRectangleLinesEx(rectangle, borderThickness, borderColour);
        if (hasText) {
            rl.text.DrawText(drawText, textPosition[0], textPosition[1], textSize, textColour);
        }
        else {
            rl.text.DrawText(placeholderText, textPosition[0], textPosition[1], textSize, textColour);
        }
    }

    public void addChar(char character) {
        if (hasText) {
            text += character;
            String temp = "";
            for (int i = text.length() - 1; i >= 0; i--) {
                temp = text.charAt(i) + temp;
                if (rl.text.MeasureText(temp, textSize) > width - (borderThickness + 20)) {
                    break;
                }
            }
            drawText = temp;
        }
        else {
            text = "" + character;
            drawText = "" + character;
            hasText = true;
        }
    }
    public void removeChar() {
        if (!hasText) {
            return;
        }
        if (text.length() == 1) {
            text = "";
            hasText = false;
            drawText = placeholderText;
            return;
        }
        text = text.substring(0, text.length() - 1);
        String temp = "";
        for (int i = text.length() - 1; i >= 0; i--) {
            temp = text.charAt(i) + temp;
            if (rl.text.MeasureText(temp, textSize) > width - (borderThickness + 20)) {
                break;
            }
        }
        drawText = temp;
        System.out.println(drawText);
    }
}
