package game.objects;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.objects.infrastructure.Cable;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;
import game.scenes.maingame.NodeConnection;
import game.scenes.maingame.NodeGraph;

import java.util.List;

public class Packet {
    public boolean moving = false;
    public Jaylib.Color color = new Jaylib.Color(0, 0, 0, 255);
    public int source;
    public int destination;
    public List<NodeConnection> path;
    public float windowPositionX;
    public float windowPositionY;
    public Cable currentCable;
    public float[] currentDeltas;
    public float currentTime;
    public Structure currentDestStructure;
    //Indicates whether the packet has finished in journey. Used for removal later.
    public boolean hasArrived = false;

    public void reverse() {
        int temp = this.source;
        this.source = destination;
        this.destination = temp;
        this.path = NodeGraph.findShortestPath(source, destination);
        this.path.remove(0);
    }

    public void tick() {
        windowPositionX = windowPositionX + (currentDeltas[0] * Raylib.GetFrameTime());
        windowPositionY = windowPositionY + (currentDeltas[1] * Raylib.GetFrameTime());
        this.currentTime += Raylib.GetFrameTime();
        if (currentTime >= currentCable.travelTime) {
            moving = false;
        }
    }

    public void draw() {
        Raylib.DrawCircle((int) windowPositionX, (int) windowPositionY, 2, color);
    }

    public Packet(int source, int dest, List<NodeConnection> path) {
        this.source = source;
        this.destination = dest;

        this.path = NodeGraph.findShortestPath(source, dest);

        MainGame.packets.add(this);

        int currentGridX = MainGame.getStructureById(source).gridX;
        int currentGridY = MainGame.getStructureById(source).gridY;
        this.windowPositionX = currentGridX * MainGame.grid.cellWindowWidth;
        this.windowPositionY = currentGridY * MainGame.grid.cellWindowHeight;

        path.remove(0);

        this.currentCable = MainGame.getCableByStructureIds(source, path.get(0).getDestNode());
        this.currentTime = 0f;
        this.currentDeltas = this.currentCable.getDeltas(source, path.get(0).getDestNode());
    }
}
