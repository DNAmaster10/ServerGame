package game.objects.buildings;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

public class Headquarters extends Structure {
    public int windowXPos;
    public int windowYPos;
    public static Raylib.Color color = new Jaylib.Color(255, 0, 255, 255);
    @Override
    public void draw() {
        Raylib.DrawRectangle(windowXPos, windowYPos, 10, 10, color);
    }

    @Override
    public void tick() {

    }
    public Headquarters(int gridX, int gridY) {
        super.id = Ids.getNewId();
        windowXPos = gridX * MainGame.grid.cellWindowWidth - 5;
        windowYPos = gridY * MainGame.grid.cellWindowHeight - 5;
        super.gridX = gridX;
        super.gridY = gridY;
        MainGame.HQId = super.id;
    }
}
