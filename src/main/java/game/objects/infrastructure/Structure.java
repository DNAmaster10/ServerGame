package game.objects.infrastructure;

import game.objects.Packet;

import java.util.ArrayList;
import java.util.List;

public abstract class Structure {
    public int id;
    public int gridX;
    public int gridY;
    public boolean connectedToHq;

    public List<Packet> arrivingPackets = new ArrayList<>();

    public int packetsPerSecond;

    //Type indicated the structure type.
    //0 is router
    //1 is cable

    public abstract void draw();
    public abstract void tick();
}
