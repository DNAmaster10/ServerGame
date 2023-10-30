package game.scenes.maingame;

import game.objects.buildings.Consumer;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

import java.util.ArrayList;
import java.util.List;

public class Packets {
    //Calculated on edge creation, is a list of all servers connected to the HQ building
    public static List<Integer> availableServers = new ArrayList<>();
    //Calculated on edge creation, is a list of all consumers connected to the HQ
    public static List<Integer> availableConsumers = new ArrayList<>();
    public static void tickPackets() {
        //Create packets where needed
        for (Structure structure : MainGame.structures.values()) {
            structure.tick();
        }
    }
}
