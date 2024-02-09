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
        //Border colour for the button
        borderColour = brdColour;
        //Background colour for the button
        backgroundColour = bgColour;
        
        //X and y position defined in an array of length 2
        position = new int[2];
        position[0] = x;
        position[1] = y;

        //Button dimensions specified in the below variables
        width = btnWidth;
        height = btnHeight;

        //Rectangle is used by raylib when drawing hte button to the screen and also when checking if the button is being pressed.
        rectangle = new Rectangle(x, y, btnWidth, btnHeight);

        //The thickness of the border around the button
        borderThickness = brdThickness;

        //If no text was specific in the constructor, treat it as null
        if (buttonText != null) {
            text = buttonText;
        }
        //The size which Raylib will draw the text within the button
        textSize = btnTextSize;
        if (text != null) {
            //Position of the text has to be calculated based on the font size, as well as the height of the button, or else text will "bleed" out of the text box
            textPosition = new int[2];
            textPosition[0] = position[0] + ((width / 2) - (rl.text.MeasureText(text, textSize) / 2));
            textPosition[1] = position[1] + ((height / 2) - (textSize / 2));
            textColour = colourText;
        }
    }
    public boolean checkClick() {
        //Returns true if the button is being pressed by the player
        return (rl.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && rl.shapes.CheckCollisionPointRec(rCore.GetMousePosition(), rectangle));
    }

    public void draw() {
        //Draws the button to the screen, starting with the background, then the border, then the text.
        //Here the text is drawn last to take priority over the other elements drawn behind it.
        rShapes.DrawRectangleRec(rectangle, backgroundColour);
        rl.shapes.DrawRectangleLinesEx(rectangle, borderThickness, borderColour);
        rl.text.DrawText(text, textPosition[0], textPosition[1], textSize, textColour);
    }
}

