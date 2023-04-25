package game.objects;

import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;
import game.objects.buildings.Server;
import game.objects.infrastructure.Cable;
import game.scenes.MainGame;
import game.scenes.maingame.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.raylib.java.core.rCore.GetFrameTime;
import static game.Window.properties.rl;

public class Packet {
    public boolean moving = false;
    public Color color = new Color(0, 0, 0, 255);
    public int source;
    public int destination;
    public List<NodeConnection> path;
    public Vector2 windowPosition;
    public Cable currentCable;
    public float[] currentDeltas;
    public float currentTime;
    public boolean hasReversed = false;
    public boolean shouldRemove = false;
    //Indicates whether the packet has finished in journey. Used for removal later.
    public boolean hasArrived = false;

    public void reverse(Server server) {
        int temp = this.source;
        this.source = destination;
        this.destination = temp;
        this.path =  new ArrayList<>(server.getPath(this.destination));
        this.hasReversed = true;
    }

    public void tick() {
        this.windowPosition.x = this.windowPosition.x + (currentDeltas[0] * GetFrameTime());
        this.windowPosition.y = this.windowPosition.y + (currentDeltas[1] * GetFrameTime());
        this.currentTime += GetFrameTime();
        if (currentTime >= currentCable.travelTime) {
            moving = false;
        }
    }
    public void remove() {
        //Deleted the packet
        this.shouldRemove = true;
    }

    public void draw() {
        if (this.moving) {
            rl.shapes.DrawCircleV(this.windowPosition, 2, color);
        }
    }

    public Packet(int source, int dest, List<NodeConnection> path) {
        this.source = source;
        this.destination = dest;

        this.path = new ArrayList<>(path);

        int currentGridX = Objects.requireNonNull(MainGame.getStructureById(source)).gridX;
        int currentGridY = Objects.requireNonNull(MainGame.getStructureById(source)).gridY;
        this.windowPosition = new Vector2();
        this.windowPosition.x = currentGridX * MainGame.grid.cellWindowWidth;
        this.windowPosition.y = currentGridY * MainGame.grid.cellWindowHeight;


        this.currentCable = MainGame.getCableByStructureIds(source, this.path.get(0).getDestNode());
        this.currentTime = 0f;
        assert this.currentCable != null;
        this.currentDeltas = this.currentCable.getDeltas(source);

        MainGame.packets.add(this);
    }
}
