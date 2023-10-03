package game.objects.buildings;

import com.raylib.java.core.Color;
import game.objects.Player;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.raylib.java.core.Color.BLACK;
import static game.Window.properties.rl;

public class SmallOffice extends Consumer {
    public int windowXPos;
    public int windowYPos;
    public static Color color = new Color(17, 173, 171, 255);

    @Override
    public void draw() {
        rl.shapes.DrawRectangle(windowXPos, windowYPos, 10, 10, color);
        if (Player.drawIds) {
            rl.text.DrawText(String.valueOf(this.id), windowXPos, windowYPos, 3, BLACK);
        }
    }
    public SmallOffice(int gridX, int gridY) {
        //Constructor
        super.id = Ids.getNewId();
        super.gridX = gridX;
        super.gridY = gridY;
        super.emitting = false;
        //The minimum ammount of packets which will be released at once
        super.minPackets = 3;
        //The maximum ammount of packets which will be released at once
        super.maxPackets = 6;

        windowXPos = gridX * MainGame.grid.cellWindowWidth - 5;
        windowYPos = gridY * MainGame.grid.cellWindowHeight - 5;
        //The ammount of time to wait between emiting packets within a single burst
        super.packetEmitDelay = 0.9f;
        //The minimum ammount of time to wait before emiting another burst of packets
        super.packetFrequency = 2;

        List<Integer> serverIds = new ArrayList<>();
        for (Server server : MainGame.servers.values()) {
            serverIds.add(server.id);
        }
        //The consumer's favourite server. This can include servers which are not connected to the network.
        this.favouriteServer = serverIds.get(ThreadLocalRandom.current().nextInt(0, serverIds.size()));
    }
}
