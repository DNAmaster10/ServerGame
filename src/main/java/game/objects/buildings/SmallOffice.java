package game.objects.buildings;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.Window;
import game.objects.Player;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.raylib.Jaylib.BLACK;

public class SmallOffice extends Consumer {
    public int windowXPos;
    public int windowYPos;
    public static Jaylib.Color color = new Jaylib.Color(17, 173, 171, 255);

    @Override
    public void draw() {
        Raylib.DrawRectangle(windowXPos, windowYPos, 10, 10, color);
        if (Player.drawIds) {
            Raylib.DrawText(String.valueOf(this.id), windowXPos, windowYPos, 3, BLACK);
        }
    }
    public SmallOffice(int gridX, int gridY) {
        super.id = Ids.getNewId();
        super.gridX = gridX;
        super.gridY = gridY;
        super.emitting = false;
        super.minPackets = 3;
        super.maxPackets = 6;

        windowXPos = gridX * MainGame.grid.cellWindowWidth - 5;
        windowYPos = gridY * MainGame.grid.cellWindowHeight - 5;
        super.packetEmitDelay = 0.9f;
        super.packetFrequency = 2;

        List<Integer> serverIds = new ArrayList<>();
        for (Server server : MainGame.servers.values()) {
            serverIds.add(server.id);
        }
        this.favouriteServer = serverIds.get(ThreadLocalRandom.current().nextInt(0, serverIds.size()));
    }
}