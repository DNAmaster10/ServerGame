package game.objects.buildings;

import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.raylib.java.core.Color.BLUE;
import static game.Window.properties.rl;

public class Office extends Consumer {
    public int windowXPos;
    public int windowYPos;
    @Override
    public void draw() {

        rl.shapes.DrawRectangle(windowXPos, windowYPos, 10, 10, BLUE);
        /*
        if (Player.drawIds) {
            Raylib.DrawText(String.valueOf(this.id), windowXPos, windowYPos, 3, BLACK);
        }
         */
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
        super.packetEmitDelay = 0.5f;
        super.packetFrequency = 1;

        List<Integer> serverIds = new ArrayList<>();
        for (Server server : MainGame.servers.values()) {
            serverIds.add(server.id);
            this.favouriteServer = serverIds.get(ThreadLocalRandom.current().nextInt(0, serverIds.size()));
        }
    }
}
