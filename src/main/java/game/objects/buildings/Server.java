package game.objects.buildings;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.objects.Packet;
import game.objects.Player;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;
import game.scenes.maingame.NodeConnection;
import game.scenes.maingame.NodeGraph;
import game.scenes.maingame.Packets;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.raylib.Jaylib.BLACK;

public class Server extends Structure {
    public int windowXPos;
    public int windowYPos;
    public Raylib.Color color = new Jaylib.Color(255, 157, 0, 255);
    public HashMap<Integer, List<NodeConnection>> shortestPaths = new HashMap<>();

    public void calculatePaths() {
        //First clear existing path hash map
        shortestPaths.clear();
        for (Integer consumerId : Packets.availableConsumers) {
            shortestPaths.put(consumerId, NodeGraph.findShortestPath(this.id, consumerId));
        }
    }

    public List<NodeConnection> getPath(int destConsumerId) {
        return(shortestPaths.get(destConsumerId));
    }

    @Override
    public void draw() {
        Raylib.DrawRectangle(windowXPos, windowYPos, 10, 10, color);
        if (Player.drawIds) {
            Raylib.DrawText(String.valueOf(super.id), this.windowXPos, this.windowYPos, 3, BLACK);
        }
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
                    if (!this.connectedToHq) {
                        arrivingPacket.remove();
                        arrivingPackets.remove(i);
                        i--;
                        continue;
                    }
                    else if (!MainGame.getStructureById(arrivingPacket.source).connectedToHq) {
                        arrivingPacket.remove();
                        arrivingPackets.remove(i);
                        i--;
                        continue;
                    }
                    arrivingPacket.reverse(this);
                    arrivingPacket.windowPosition.x(this.windowXPos + 5);
                    arrivingPacket.windowPosition.y(this.windowYPos + 5);
                    arrivingPacket.currentDeltas = arrivingPacket.currentCable.getDeltas(this.id, arrivingPacket.path.get(0).getDestNode());
                    arrivingPacket.currentTime = 0f;
                    arrivingPacket.moving = true;
                    Structure nextStructure = MainGame.getStructureById(arrivingPacket.path.get(0).getDestNode());
                    if (nextStructure == null) {
                        arrivingPacket.remove();
                    }
                    else {
                        nextStructure.arrivingPackets.add(arrivingPacket);
                    }
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
