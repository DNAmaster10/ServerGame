package game.objects.buildings;

import com.raylib.Raylib;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import static com.raylib.Jaylib.BLUE;

public class Office extends Consumer {
    public int windowXPos;
    public int windowYPos;
    @Override
    public void draw() {
        Raylib.DrawRectangle(windowXPos, windowYPos, 10, 10, BLUE);
    }
    public Office(int gridX, int gridY) {
        super.id = Ids.getNewId();
        super.gridX = gridX;
        super.gridY = gridY;
        super.emitting = false;
        super.minPackets = 10;
        super.maxPackets = 15;

        windowXPos = gridX * MainGame.grid.cellWindowWidth - 5;
        windowYPos = gridY * MainGame.grid.cellWindowHeight - 5;
        //super.packetsPerSecond = 0.5;
    }
}
