package game.objects.infrastructure;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.objects.Packet;
import game.objects.Player;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.raylib.Jaylib.BLACK;

public class Router extends Structure {
    public boolean connectedToHq;
    public int packetsPerSecond;
    public int cacheSize;
    public int routerLevel;
    public List<Packet> storedPackets = new ArrayList<>();
    public int windowXPos;
    public int windowYPos;
    public float lastPacketRelease = 0;

    @Override
    public void draw() {
        Jaylib.DrawCircle(windowXPos, windowYPos, 3, Jaylib.WHITE);
        if (Player.drawIds) {
            Jaylib.DrawText(String.valueOf(this.id), windowXPos, windowYPos, 3, BLACK);
        }
    }

    @Override
    public void tick() {
        //To Do
        if (!arrivingPackets.isEmpty()) {
            for (int i = 0; i < arrivingPackets.size(); i++) {
                if (arrivingPackets.get(i).moving) {
                    arrivingPackets.get(i).tick();
                } else {
                    arrivingPackets.get(i).path.remove(0);
                    storedPackets.add(arrivingPackets.get(i));
                    arrivingPackets.remove(i);
                    i--;
                }
            }
        }
        if (!storedPackets.isEmpty()) {
            if (Raylib.GetTime() - lastPacketRelease > packetsPerSecond) {
                Packet packet = storedPackets.get(0);
                packet.windowPosition.x(this.windowXPos);
                packet.windowPosition.y(this.windowYPos);
                if (packet.path.size() == 0) {
                    packet.currentCable = MainGame.getCableByStructureIds(super.id, packet.destination);
                    packet.currentDeltas = packet.currentCable.getDeltas(super.id, packet.destination);
                    MainGame.getStructureById(packet.destination).arrivingPackets.add(packet);
                }
                else {
                    //System.out.println("Id - " + super.id);
                    //System.out.println("Id2 - " + packet.path.get(0));
                    packet.currentCable = MainGame.getCableByStructureIds(super.id, packet.path.get(0).getDestNode());
                    if (packet.currentCable == null) {
                        System.out.println("NULL HERE!");
                    }
                    packet.currentDeltas = packet.currentCable.getDeltas(super.id, packet.path.get(0).getDestNode());
                    MainGame.getStructureById(packet.path.get(0).getDestNode()).arrivingPackets.add(packet);
                }
                packet.currentTime = 0f;
                packet.moving = true;
            }
            storedPackets.removeIf(packet -> packet.moving);
        }
    }

    public void upgrade(int level) {
        switch(level) {
            case 1: {
                packetsPerSecond = SmallRouter.packetsPerSecond;
                cacheSize = SmallRouter.maxCacheSize;
                routerLevel = level;
                break;
            }
        }
    }

    public Router(int level, int gridX, int gridY) {
        super.id = Ids.getNewId();
        super.gridX = gridX;
        super.gridY = gridY;
        this.connectedToHq = false;
        windowXPos = gridX * MainGame.grid.cellWindowWidth;
        windowYPos = gridY * MainGame.grid.cellWindowHeight;

        switch(level) {
            case 1: {
                packetsPerSecond = SmallRouter.packetsPerSecond;
                cacheSize = SmallRouter.maxCacheSize;
                routerLevel = level;
                break;
            }
        }
    }
}
