package game.objects.ui;

import com.raylib.java.core.Color;
import com.raylib.java.core.rCore;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.shapes.rShapes;

import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static game.Window.properties.rl;

public class Button {
    public Color borderColour;
    public Color backgroundColour;
    public String text;
    public int[] position;
    public int width;
    public int height;
    public Rectangle rectangle;
    public int textSize;
    public int[] textPosition;
    public Color textColour;
    public static int borderThickness;

    public Button(int x, int y, int btnWidth, int btnHeight, int brdThickness, Color bgColour, Color brdColour, String buttonText, int btnTextSize, Color colourText) {
        //Pass null for button text if there is no text, and 0 for text size
        borderColour = brdColour;
        backgroundColour = bgColour;
        position = new int[2];
        position[0] = x;
        position[1] = y;
        width = btnWidth;
        height = btnHeight;
        rectangle = new Rectangle(x, y, btnWidth, btnHeight);
        borderThickness = brdThickness;

        if (buttonText != null) {
            text = buttonText;
        }
        textSize = btnTextSize;
        if (text != null) {
            textPosition = new int[2];
            textPosition[0] = position[0] + ((width / 2) - (rl.text.MeasureText(text, textSize) / 2));
            textPosition[1] = position[1] + ((height / 2) - (textSize / 2));
            textColour = colourText;
        }
    }

    public boolean checkClick() {
        return (rl.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && rl.shapes.CheckCollisionPointRec(rCore.GetMousePosition(), rectangle));
    }

    public void draw() {
        rShapes.DrawRectangleRec(rectangle, backgroundColour);
        rl.shapes.DrawRectangleLinesEx(rectangle, borderThickness, borderColour);
        rl.text.DrawText(text, textPosition[0], textPosition[1], textSize, textColour);
    }
}

