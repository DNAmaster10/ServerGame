package game.scenes;

import game.Window;
import game.objects.Player;
import game.objects.ui.Button;

import com.raylib.java.Raylib;

import static com.raylib.java.core.Color.BLACK;
import static com.raylib.java.core.Color.WHITE;
import static game.Window.properties.rl;

public class NewGame {
    private static Button oneHundredButton;
    private static Button fiveHundredButton;
    private static Button oneThousandButton;

    public static void init() {
        final int buttonWidth = 300;
        int buttonX = (Window.properties.windowWidth / 3) - buttonWidth / 2;
        oneHundredButton = new Button(buttonX, 20, buttonWidth, 50, 4, WHITE, BLACK, "100", 20, BLACK);
        buttonX += Window.properties.windowWidth / 3;
        fiveHundredButton = new Button(buttonX, 20, buttonWidth, 50, 4, WHITE, BLACK, "500", 20, BLACK);
        buttonX += Window.properties.windowWidth / 3;
        oneThousandButton = new Button(buttonX, 20, buttonWidth, 50, 4, WHITE, BLACK, "1000", 20, BLACK);
    }

    public static void tick() {
        if (oneHundredButton.checkClick()) {
            System.out.println("100");
            MainGame.init(100, 100);
            Player.setScene(2);
        }
        else if (fiveHundredButton.checkClick()) {
            System.out.println("500");
        }
        else if(oneThousandButton.checkClick()) {
            System.out.println("1000");
        }
    }

    public static void draw() {
        rl.core.ClearBackground(WHITE);

        //Draw Buttons
        oneHundredButton.draw();
        fiveHundredButton.draw();
        oneThousandButton.draw();
    }
}
