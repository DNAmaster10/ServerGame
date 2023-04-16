package game.scenes.maingame;

import game.objects.buildings.Consumer;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

import java.util.ArrayList;
import java.util.List;

public class Packets {
    //Calculated on edge creation
    public static List<Integer> availableServers = new ArrayList<>();
    public static void tickPackets() {
        //Create packets where needed
        for (Structure structure : MainGame.structures.values()) {
            structure.tick();
        }
    }
}
