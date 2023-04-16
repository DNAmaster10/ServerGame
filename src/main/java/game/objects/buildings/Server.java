package game.objects.buildings;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.objects.Packet;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.raylib.Jaylib.BLACK;

public class Server extends Structure {
    public int windowXPos;
    public int windowYPos;
    public Raylib.Color color = new Jaylib.Color(255, 157, 0, 255);

    @Override
    public void draw() {
        Raylib.DrawRectangle(windowXPos, windowYPos, 10, 10, color);
        Raylib.DrawText(String.valueOf(super.id), this.windowXPos, this.windowYPos, 3, BLACK);
    }

    @Override
    public void tick() {
        if (!super.arrivingPackets.isEmpty()) {
            for (int i = 0; i < arrivingPackets.size(); i++) {
                Packet arrivingPacket = arrivingPackets.get(i);
                if (arrivingPacket.moving) {
                    arrivingPacket.tick();
                }
                else {
                    arrivingPacket.reverse();
                    arrivingPacket.windowPositionX = this.windowXPos + 5;
                    arrivingPacket.windowPositionY = this.windowYPos + 5;
                    arrivingPacket.currentDeltas = arrivingPacket.currentCable.getDeltas(this.id, arrivingPacket.path.get(0).getDestNode());
                    arrivingPacket.currentTime = 0f;
                    arrivingPacket.moving = true;
                    MainGame.getStructureById(arrivingPacket.path.get(0).getDestNode()).arrivingPackets.add(arrivingPacket);
                    arrivingPackets.remove(i);
                    i--;
                }
            }
        }
    }

    public Server(int gridX, int gridY) {
        super.id = Ids.getNewId();
        super.gridX = gridX;
        super.gridY = gridY;
        windowXPos = gridX * MainGame.grid.cellWindowWidth - 5;
        windowYPos = gridY * MainGame.grid.cellWindowHeight - 5;
    }
}
