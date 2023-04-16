package game.objects.buildings;

import com.raylib.Raylib;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.raylib.Jaylib.GREEN;

public class House extends Consumer {
    public int windowXPos;
    public int windowYPos;

    @Override
    public void draw() {
        Raylib.DrawRectangle(windowXPos, windowYPos, 10, 10, GREEN);
    }

    public House(int gridX, int gridY) {
        super.id = Ids.getNewId();
        super.gridX = gridX;
        super.gridY = gridY;
        super.emitting = false;
        super.minPackets = 5;
        super.maxPackets = 10;

        windowXPos = gridX * MainGame.grid.cellWindowWidth - 5;
        windowYPos = gridY * MainGame.grid.cellWindowHeight - 5;
        super.packetsPerSecond = 1;
        super.packetFrequency = 1;

        List<Integer> serverIds = new ArrayList<>();
        for (Structure structure : MainGame.structures.values()) {
            if (structure instanceof Server) {
                serverIds.add(structure.id);
            }
        }
        this.favouriteServer = serverIds.get(ThreadLocalRandom.current().nextInt(0, serverIds.size()));
    }
}
