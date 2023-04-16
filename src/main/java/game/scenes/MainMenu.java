package game.scenes;

import com.raylib.Jaylib;
import game.Window;
import game.objects.Player;
import game.objects.ui.Button;

import static com.raylib.Jaylib.*;
import static com.raylib.Raylib.ClearBackground;

public class MainMenu {
    //Id is 1

    private static Button newGameButton;
    private static Button loadGameButton;
    private static Button exitGameButton;

    public static void init() {
        final int buttonWidth = 300;
        int buttonX = (Window.properties.windowWidth / 2) - buttonWidth / 2;

        newGameButton = new Button(buttonX, 20, buttonWidth, 50, 4, WHITE, BLACK, "New Game", 20, BLACK);
        loadGameButton = new Button(buttonX, 80, buttonWidth, 50, 4, WHITE, BLACK, "Load Game", 20, BLACK);
        exitGameButton = new Button(buttonX, 140, buttonWidth, 50, 4, WHITE, BLACK, "Exit Game", 20, BLACK);
    }

    public static void tick() {
        if (newGameButton.checkClick()) {
            Player.setScene(3);
        }
        else if (loadGameButton.checkClick()) {
            System.out.println("Loading save");
        }
        else if(exitGameButton.checkClick()) {
            System.out.println("Exiting");
            CloseWindow();
        }
    }

    public static void draw() {
        ClearBackground(WHITE);

        //Draw Buttons
        newGameButton.draw();
        loadGameButton.draw();
        exitGameButton.draw();
    }
}
