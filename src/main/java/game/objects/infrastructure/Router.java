package game.objects.infrastructure;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.objects.Packet;
import game.objects.Player;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.List;

import static com.raylib.Jaylib.BLACK;

public class Router extends Structure {
    public boolean connectedToHq;
    public int packetHandleDelay;
    public int cacheSize;
    public int routerLevel;
    public List<Packet> storedPackets = new ArrayList<>();
    public int windowXPos;
    public int windowYPos;
    public float lastPacketRelease = 0;
    Jaylib.Color color;
    //This variable indicates the amount that should be added
    //to red & green for each packet in the cache
    private float colourChange;

    @Override
    public void draw() {
        Jaylib.DrawCircle(windowXPos, windowYPos, 4, this.color);
        if (Player.drawIds) {
            Jaylib.DrawText(String.valueOf(this.id), windowXPos, windowYPos, 3, BLACK);
        }
    }

    @Override
    public void tick() {
        //Used to decide whether the colour of the router should be recalculated
        int preCacheSize = storedPackets.size();
        //To Do
        if (!arrivingPackets.isEmpty()) {
            for (int i = 0; i < arrivingPackets.size(); i++) {
                if (arrivingPackets.get(i).moving) {
                    arrivingPackets.get(i).tick();
                } else {
                    arrivingPackets.get(i).path.remove(0);
                    //Add packet to cache or destroy if full
                    if (storedPackets.size() <= cacheSize) {
                        storedPackets.add(arrivingPackets.get(i));
                        arrivingPackets.remove(i);
                    }
                    else {
                        //Destroy the packet
                        //Add one to packet loss
                        arrivingPackets.get(i).hasArrived = true;
                        arrivingPackets.remove(i);
                        Player.lostPackets++;
                    }
                    i--;
                }
            }
        }
        if (!storedPackets.isEmpty()) {
            if (Raylib.GetTime() - lastPacketRelease > packetHandleDelay) {
                Packet packet = storedPackets.get(0);
                packet.windowPosition.x(this.windowXPos);
                packet.windowPosition.y(this.windowYPos);
                if (packet.path.size() == 0) {
                    packet.currentCable = MainGame.getCableByStructureIds(super.id, packet.destination);
                    packet.currentDeltas = packet.currentCable.getDeltas(super.id, packet.destination);
                    MainGame.getStructureById(packet.destination).arrivingPackets.add(packet);
                }
                else {
                    packet.currentCable = MainGame.getCableByStructureIds(super.id, packet.path.get(0).getDestNode());
                    packet.currentDeltas = packet.currentCable.getDeltas(super.id, packet.path.get(0).getDestNode());
                    MainGame.getStructureById(packet.path.get(0).getDestNode()).arrivingPackets.add(packet);
                }
                packet.currentTime = 0f;
                packet.moving = true;
                lastPacketRelease = (float) Raylib.GetTime();
            }
            storedPackets.removeIf(packet -> packet.moving);
        }
        if (preCacheSize != storedPackets.size()) {
            //Recalculate colour
            int newColourValue = (int) (storedPackets.size() * this.colourChange);
            if (newColourValue <= 255) {
                this.color = new Jaylib.Color(newColourValue, 255, 0, 255);
            }
            else {
                this.color = new Jaylib.Color(255, 255 - (newColourValue - 255), 0, 255);
            }
        }
    }

    public void upgrade(int level) {
        switch(level) {
            case 1: {
                packetHandleDelay = SmallRouter.packetHandleDelay;
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
        this.color = new Jaylib.Color(0, 255 ,0 ,255);

        switch(level) {
            case 1 -> {
                packetHandleDelay = SmallRouter.packetHandleDelay;
                cacheSize = SmallRouter.maxCacheSize;
                routerLevel = level;
            }
        }
        this.colourChange = 510 / (float) this.cacheSize;
    }
}
