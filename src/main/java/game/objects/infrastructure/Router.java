package game.objects.infrastructure;

import com.raylib.java.core.Color;
import game.objects.Packet;
import game.objects.Player;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.List;

import static com.raylib.java.core.Color.BLACK;
import static com.raylib.java.core.rCore.GetTime;
import static game.Window.properties.rl;

public class Router extends Structure {
    public boolean connectedToHq;
    public float packetHandleDelay;
    public int cacheSize;
    public int routerLevel;
    public List<Packet> storedPackets = new ArrayList<>();
    public int windowXPos;
    public int windowYPos;
    public float lastPacketRelease = 0;
    Color color;
    //This variable indicates the amount that should be added
    //to red & green for each packet in the cache
    private final float colourChange;

    @Override
    public void draw() {
        rl.shapes.DrawCircle(windowXPos, windowYPos, 4, this.color);
        if (Player.drawIds) {
            rl.text.DrawText(String.valueOf(this.id), windowXPos, windowYPos, 3, BLACK);
        }
    }

    @Override
    public void tick() {
        //Used to decide whether the colour of the router should be recalculated
        int preCacheSize = storedPackets.size();

        if (!arrivingPackets.isEmpty()) {
            //If there are incoming packets
            for (int i = 0; i < arrivingPackets.size(); i++) {
                //For each incoming packet
                if (arrivingPackets.get(i).moving) {
                    arrivingPackets.get(i).tick();
                }
                else {
                    //If the packet has reached the end of the cable
                    //Remove the last path item
                    arrivingPackets.get(i).path.remove(0);
                    //Add the packet to cache or destroy it if full;
                    if (storedPackets.size() <= cacheSize) {
                        arrivingPackets.get(i).moving = false;
                        storedPackets.add(arrivingPackets.get(i));
                        arrivingPackets.remove(i);
                    }
                    else {
                        //Destory the packet
                        arrivingPackets.get(i).remove();
                        arrivingPackets.remove(i);
                        Player.lostPackets++;
                    }
                    i--;
                }
            }
        }

        //Now we deal with stored packets

        //If the router has any
        while (!storedPackets.isEmpty() && GetTime() - lastPacketRelease > packetHandleDelay) {
            //If the router is ready to handle another packet
            //Get the next packet to handle
            Packet packet = storedPackets.get(0);

            //Set the packets new window position
            packet.windowPosition.x = this.windowXPos;
            packet.windowPosition.y = this.windowYPos;

            //if the packets path is empty
            if (packet.path.size() == 0) {
                packet.currentCable = MainGame.getCableByStructureIds(super.id, packet.destination);
                //Check if the next cable hasn't been deleted
                if (packet.currentCable == null) {
                    packet.remove();
                    Player.lostPackets++;
                }
                else {
                    packet.currentDeltas = packet.currentCable.getDeltas(super.id);
                    Structure nextStructure = MainGame.getStructureById(packet.destination);
                    if (nextStructure == null) {
                        packet.remove();
                        Player.lostPackets++;
                    }
                    else {
                        nextStructure.arrivingPackets.add(packet);
                    }
                }
            }
            else {
                packet.currentCable = MainGame.getCableByStructureIds(super.id, packet.path.get(0).getDestNode());
                if (packet.currentCable == null) {
                    packet.remove();
                    Player.lostPackets++;
                }
                else {
                    packet.currentDeltas = packet.currentCable.getDeltas(super.id);
                    Structure nextStructure = MainGame.getStructureById(packet.path.get(0).getDestNode());
                    if (nextStructure == null) {
                        packet.remove();
                        Player.lostPackets++;
                    }
                    else {
                        nextStructure.arrivingPackets.add(packet);
                    }
                }
            }
            packet.currentTime = 0f;
            packet.moving = true;
            lastPacketRelease += packetHandleDelay;

            storedPackets.removeIf(e -> e.moving);
            storedPackets.removeIf(e -> e.shouldRemove);
        //remove any packets from the cache if they have departed
        }

        if (preCacheSize != storedPackets.size()) {
            //Recalculate colour
            int newColourValue = (int) (storedPackets.size() * this.colourChange);
            if (newColourValue <= 255) {
                this.color = new Color(newColourValue, 255, 0, 255);
            }
            else {
                this.color = new Color(255, 255 - (newColourValue - 255), 0, 255);
            }
        }
    }

    public void upgrade(int level) {
        switch(level) {
            case 1 -> {
                packetHandleDelay = SmallRouter.packetHandleDelay;
                cacheSize = SmallRouter.maxCacheSize;
                routerLevel = level;
            }
            case 2 -> {
                packetHandleDelay = MediumRouter.packetHandleDelay;
                cacheSize = MediumRouter.maxCacheSize;
                routerLevel = level;
            }
            case 3 -> {
                packetHandleDelay = LargeRouter.packetHandleDelay;
                cacheSize = LargeRouter.maxCacheSize;
                routerLevel = level;
            }
            case 4 -> {
                packetHandleDelay = HugeRouter.packetHandleDelay;
                cacheSize = HugeRouter.maxCacheSize;
                routerLevel = level;
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
        this.color = new Color(0, 255 ,0 ,255);

        switch(level) {
            case 1 -> {
                packetHandleDelay = SmallRouter.packetHandleDelay;
                cacheSize = SmallRouter.maxCacheSize;
                routerLevel = level;
            }
            case 2 -> {
                packetHandleDelay = MediumRouter.packetHandleDelay;
                cacheSize = MediumRouter.maxCacheSize;
                routerLevel = level;
            }
            case 3 -> {
                packetHandleDelay = LargeRouter.packetHandleDelay;
                cacheSize = LargeRouter.maxCacheSize;
                routerLevel = level;
            }
            case 4 -> {
                packetHandleDelay = HugeRouter.packetHandleDelay;
                cacheSize = HugeRouter.maxCacheSize;
                routerLevel = level;
            }
        }
        this.colourChange = 510 / (float) this.cacheSize;
    }
}
