package game.objects;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import game.objects.buildings.Server;
import game.objects.infrastructure.Cable;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;
import game.scenes.maingame.NodeConnection;
import game.scenes.maingame.NodeGraph;

import java.util.ArrayList;
import java.util.List;

public class Packet {
    public boolean moving = false;
    public Jaylib.Color color = new Jaylib.Color(0, 0, 0, 255);
    public int source;
    public int destination;
    public List<NodeConnection> path;
    public Raylib.Vector2 windowPosition;
    public Cable currentCable;
    public float[] currentDeltas;
    public float currentTime;
    public Structure currentDestStructure;
    //Indicates whether the packet has finished in journey. Used for removal later.
    public boolean hasArrived = false;

    public void reverse(Server server) {
        int temp = this.source;
        this.source = destination;
        this.destination = temp;
        this.path =  new ArrayList<>(server.getPath(this.destination));
    }

    public void tick() {
        this.windowPosition.x(this.windowPosition.x() + (currentDeltas[0] * Raylib.GetFrameTime()));
        this.windowPosition.y(this.windowPosition.y() + (currentDeltas[1] * Raylib.GetFrameTime()));
        this.currentTime += Raylib.GetFrameTime();
        if (currentTime >= currentCable.travelTime) {
            moving = false;
        }
    }

    public void draw() {
        Raylib.DrawCircleV(this.windowPosition, 2, color);
    }

    public Packet(int source, int dest, List<NodeConnection> path) {
        this.source = source;
        this.destination = dest;

        this.path = new ArrayList<>(path);

        int currentGridX = MainGame.getStructureById(source).gridX;
        int currentGridY = MainGame.getStructureById(source).gridY;
        this.windowPosition = new Raylib.Vector2();
        this.windowPosition.x(currentGridX * MainGame.grid.cellWindowWidth);
        this.windowPosition.y(currentGridY * MainGame.grid.cellWindowHeight);


        this.currentCable = MainGame.getCableByStructureIds(source, this.path.get(0).getDestNode());
        this.currentTime = 0f;
        this.currentDeltas = this.currentCable.getDeltas(source, this.path.get(0).getDestNode());

        MainGame.packets.add(this);
    }
}
