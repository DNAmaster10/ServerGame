package game.objects.ui;

import com.raylib.Jaylib;

import static com.raylib.Raylib.*;

public class Button {
    public Color borderColour;
    public Color backgroundColour;
    public String text;
    public int[] position;
    public int width;
    public int height;
    public Jaylib.Rectangle rectangle;
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
        rectangle = new Jaylib.Rectangle(x, y, btnWidth, btnHeight);
        borderThickness = brdThickness;

        if (buttonText != null) {
            text = buttonText;
        }
        textSize = btnTextSize;
        if (text != null) {
            textPosition = new int[2];
            textPosition[0] = position[0] + ((width / 2) - (MeasureText(text, textSize) / 2));
            textPosition[1] = position[1] + ((height / 2) - (textSize / 2));
            textColour = colourText;
        }
    }

    public boolean checkClick() {
        return (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && CheckCollisionPointRec(GetMousePosition(), rectangle));
    }

    public void draw() {
        Jaylib.DrawRectangleRec(rectangle, backgroundColour);
        Jaylib.DrawRectangleLinesEx(rectangle, borderThickness, borderColour);
        Jaylib.DrawText(text, textPosition[0], textPosition[1], textSize, textColour);
    }
}

