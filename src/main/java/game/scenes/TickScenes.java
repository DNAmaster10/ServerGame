package game.scenes;

import game.objects.Player;

public class TickScenes {
    public static void tick() throws Exception {
        switch (Player.properties.currentScene) {
            case 1 -> {
                MainMenu.tick();
            }
            case 2 -> {
                MainGame.tick();
            }
            case 3 -> {
                NewGame.tick();
            }
        }
    }
}
