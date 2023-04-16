package game.scenes.maingame;

import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

public class Ids {
    private static int currentId = 0;

    public static int getNewId() {
        currentId++;
        return(currentId - 1);
    }
}
