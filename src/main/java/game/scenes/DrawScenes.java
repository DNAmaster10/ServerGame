package game.scenes;

import game.objects.Player;

public class DrawScenes {
    public static void draw() {
        switch (Player.properties.currentScene) {
            case 1 -> {
                MainMenu.draw();
                break;
            }
            case 2 -> {
                MainGame.draw();
                break;
            }
            case 3 -> {
                NewGame.draw();
                break;
            }
        }
    }
}
