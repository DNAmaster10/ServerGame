package game.objects.buildings;

import com.raylib.Raylib;
import game.objects.Packet;
import game.objects.Player;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;
import game.scenes.maingame.NodeConnection;
import game.scenes.maingame.NodeGraph;
import game.scenes.maingame.Packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Consumer extends Structure {
    //A hash map containing all shortest paths to all servers on the map. Recalculated upon cable placement
    HashMap<Integer, List<NodeConnection>> shortestPaths = new HashMap<>();

    //A list of integers indicating the destination for the packets
    //which are departing.
    List<Integer> departingPackets = new ArrayList<>();
    int favouriteServer;
    private double lastPacketTime = 0;
    boolean emitting = false;
    int minPackets;
    int maxPackets;
    float packetEmitDelay;
    //An integer used to decide whether or not a building should start emitting
    int packetFrequency;

    public void calculatePaths() {
        //First clear existing path hash map
        shortestPaths.clear();
        for (Integer serverId : Packets.availableServers) {
            shortestPaths.put(serverId, NodeGraph.findShortestPath(this.id, serverId));
        }
    }
    public List<NodeConnection> getPath(int destServerId) {
        return(shortestPaths.get(destServerId));
    }

    @Override
    public void tick() {
        if (!super.connectedToHq) {
            return;
        }
        //If already emitting, check if a packet should be released and release it
        if (emitting) {
            if (Raylib.GetTime() - lastPacketTime > packetEmitDelay) {
                Packet packet = new Packet(this.id, departingPackets.get(0), this.getPath(departingPackets.get(0)));
                departingPackets.remove(0);
                if (departingPackets.size() == 0) {
                    emitting = false;
                }
                //now add the packet to the next router
                packet.moving = true;
                MainGame.getStructureById(packet.path.get(0).getDestNode()).arrivingPackets.add(packet);
                lastPacketTime = Raylib.GetTime();
            }
        }
        //Decide whether the building should start emitting packets
        else {
            int chance = ThreadLocalRandom.current().nextInt(0, 1001);
            if (!(chance > this.packetFrequency)) {
                //Check if the favourite server is connected to the network
                if (MainGame.structures.get(favouriteServer).connectedToHq) {
                    chance = ThreadLocalRandom.current().nextInt(0, 101);
                    if (chance > 50) {
                        int packetCount = ThreadLocalRandom.current().nextInt(this.minPackets, this.maxPackets + 1);
                        for (int i = 0; i < packetCount; i++) {
                            this.departingPackets.add(this.favouriteServer);
                        }
                        emitting = true;
                    }
                }
                //Get a random server
                if (!Packets.availableServers.isEmpty() && !emitting) {

                    int server = Packets.availableServers.get(ThreadLocalRandom.current().nextInt(0, Packets.availableServers.size()));
                    int packetCount = ThreadLocalRandom.current().nextInt(this.minPackets, this.maxPackets + 1);
                    for (int i = 0; i < packetCount; i++) {
                        this.departingPackets.add(Packets.availableServers.get(0));
                    }
                    emitting = true;
                }
            }
        }

        //Deal with incoming packets
        if (!super.arrivingPackets.isEmpty()) {
            for (int i = 0; i < arrivingPackets.size(); i++) {
                if (arrivingPackets.get(i).moving) {
                    arrivingPackets.get(i).tick();
                }
                else {
                    arrivingPackets.get(i).moving = false;
                    arrivingPackets.get(i).hasArrived = true;
                    Player.deliveredPackets++;
                    arrivingPackets.remove(i);
                    i--;
                }
            }
        }
    }
}
