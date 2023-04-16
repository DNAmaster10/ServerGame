package game.objects.buildings;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

public class LargeOffice extends Consumer {
    public static int packetsPerSecond = 1;
    public int favouriteServer;
    public boolean emitting = false;
    public static int emitChange = 3;
    public static int minPackets = 10;
    public static int maxPackets = 20;
    public int windowXPos;
    public int windowYPos;
    public static Jaylib.Color color = new Jaylib.Color(6, 81, 186, 255);

    @Override
    public void draw() {
        Raylib.DrawRectangle(windowXPos, windowYPos, 10, 10, color);
    }

    @Override
    public void tick() {

    }

    public LargeOffice(int gridX, int gridY) {
        super.id = Ids.getNewId();
        super.gridX = gridX;
        super.gridY = gridY;

        windowXPos = gridX * MainGame.grid.cellWindowWidth - 5;
        windowYPos = gridY * MainGame.grid.cellWindowHeight - 5;
    }
}
