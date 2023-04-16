package game.scenes;

import com.raylib.Raylib;
import game.objects.Packet;
import game.objects.Player;
import game.objects.buildings.Consumer;
import game.objects.infrastructure.Cable;
import game.objects.infrastructure.Router;
import game.objects.infrastructure.Structure;
import game.scenes.maingame.*;
import game.scenes.maingame.chunks.Hamlet;
import game.scenes.maingame.chunks.InitChunks;
import game.scenes.maingame.chunks.SpawnChunk;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.raylib.Jaylib.*;

public class MainGame {
    //Used for the timing of packet removal
    //Indicates the interval in seconds at which packets should be removed
    public static int packetRemovalInterval = 5;
    //Indicated the last time that packets where removed
    public static int lastPacketRemoval;
    public static int generateInterval = 5;
    public static int lastGenerate;
    public static HashMap<Integer, Router> routers = new HashMap<>();
    public static HashMap<Integer, Cable> cables = new HashMap<>();
    public static HashMap<String, Cable> cablesIdMap = new HashMap<>();
    public static HashMap<Integer, Consumer> consumers = new HashMap<>();
    public static HashMap<Integer, Structure> buildings = new HashMap<>();
    public static HashMap<Integer, Chunk> chunks = new HashMap<>();
    public static HashMap<Integer, Structure> structures = new HashMap<>();
    public static List<Packet> packets = new ArrayList<>();
    public static int HQId;

    public static class grid {
        public static int cellWindowWidth = 10;
        public static int cellWindowHeight = 10;

        public static int chunkWidth = 10;
        public static int chunkHeight = 10;
    }
    public static Structure getStructureByGridPos(int gridX, int gridY) {
        for (Structure structure : structures.values()) {
            if (structure.gridX == gridX && structure.gridY == gridY) {
                return(structure);
            }
        }
        return null;
    }
    public static Structure getStructureById(int id) {
        if (structures.containsKey(id)) {
            return(structures.get(id));
        }
        return(null);
    }
    public static boolean checkSolidByGridPos(int gridX, int gridY) {
        //Returns a boolean indicating whether a router OR building is present
        for (Structure structure : structures.values()) {
            if (structure.gridX == gridX && structure.gridY == gridY) {
                return(true);
            }
        }
        return false;
    }
    public static boolean checkRouterById(int id) {
        return routers.containsKey(id);
    }
    public static boolean checkChunkByChunkPos(int chunkX, int chunkY) {
        for (Chunk chunk : chunks.values()) {
            if (chunk.chunkX == chunkX && chunk.chunkY == chunkY) {
                return true;
            }
        }
        return false;
    }
    public static boolean checkCableByStructureIds(int sourceStructureId, int destStructureId) {
        return(NodeGraph.checkEdge(sourceStructureId, destStructureId));
    }
    public static Cable getCableByStructureIds(int sourceStructureId, int destStructureId) {
        String key = sourceStructureId + "," + destStructureId;
        if (cablesIdMap.containsKey(key)) {
            return(cablesIdMap.get(key));
        }
        System.out.println(cablesIdMap.keySet());
        System.out.println(sourceStructureId + " : " + destStructureId);
        return null;
    }

    public static void init(int gridWidth, int gridHeight) {
        Player.init();
        Gui.init();
        InitChunks.init();
        SpawnChunk firstChunk = new SpawnChunk(0, 0);
        firstChunk.generateFull();
        chunks.put(firstChunk.id, firstChunk);
        lastPacketRemoval = (int) Raylib.GetTime();
    }
    public static void generate() {
        //First, check if any chunks are not already full
        List<Chunk> notFullChunks = new ArrayList<>();
        for (Chunk chunk : chunks.values()) {
            if (!chunk.full) {
                notFullChunks.add(chunk);
            }
        }
        System.out.println("Found " + notFullChunks.size() + " not full chunks");
        if (notFullChunks.size() > 0) {
            int selectedChunk;
            //Out of the chunks which are not full, pick a random one, or the only one
            //And generate one
            if (notFullChunks.size() == 1) {
                selectedChunk = 0;
            }
            else {
                selectedChunk = ThreadLocalRandom.current().nextInt(0, notFullChunks.size());
            }
            notFullChunks.get(selectedChunk).generateOne();
            return;
        }
        //If not, then create a new chunk
        //First, for all the chunks with an empty border, get them
        List<Chunk> emptyBorderChunks = new ArrayList<>();
        for (Chunk chunk : chunks.values()) {
            if (!chunk.bordersFull) {
                emptyBorderChunks.add(chunk);
            }
        }
        //Out of these chunks, pick a random one
        int emptyBorderChunkIndex;
        if (emptyBorderChunks.size() == 1) {
            emptyBorderChunkIndex = 0;
        }
        else {
            emptyBorderChunkIndex = ThreadLocalRandom.current().nextInt(0, emptyBorderChunks.size());
        }
        Chunk emptyBorderChunk = emptyBorderChunks.get(emptyBorderChunkIndex);
        //For this chunk, work out which borders are empty
        List<Integer> emptyBorders = new ArrayList<>();
        if (!checkChunkByChunkPos(emptyBorderChunk.chunkX, emptyBorderChunk.chunkY - 1)) {
            emptyBorders.add(0);
        }
        if (!checkChunkByChunkPos(emptyBorderChunk.chunkX + 1, emptyBorderChunk.chunkY)) {
            emptyBorders.add(1);
        }
        if (!checkChunkByChunkPos(emptyBorderChunk.chunkX, emptyBorderChunk.chunkY + 1)) {
            emptyBorders.add(2);
        }
        if (!checkChunkByChunkPos(emptyBorderChunk.chunkX - 1, emptyBorderChunk.chunkY)) {
            emptyBorders.add(3);
        }
        //Now, select a random border
        int chunkBorder;
        if (emptyBorders.size() == 1) {
            chunkBorder = emptyBorders.get(0);
        }
        else {
            chunkBorder = emptyBorders.get(ThreadLocalRandom.current().nextInt(0, emptyBorders.size()));
        }
        int[] chunkPosition = new int[2];
        //Now based on the selected border, get the position of the new chunk
        switch(chunkBorder) {
            case 0 -> {
                chunkPosition[0] = emptyBorderChunk.chunkX;
                chunkPosition[1] = emptyBorderChunk.chunkY - 1;
            }
            case 1 -> {
                chunkPosition[0] = emptyBorderChunk.chunkX + 1;
                chunkPosition[1] = emptyBorderChunk.chunkY;
            }
            case 2 -> {
                chunkPosition[0] = emptyBorderChunk.chunkX;
                chunkPosition[1] = emptyBorderChunk.chunkY + 1;
            }
            case 3 -> {
                chunkPosition[0] = emptyBorderChunk.chunkX - 1;
                chunkPosition[1] = emptyBorderChunk.chunkY;
            }
        }
        //Now, generate the chunk in the position
        int randomChunk;
        //INSERT CODE TO GEN RANDOM CHUNK HERE
        randomChunk = 1;

        switch(randomChunk) {
            case 1 -> {
                Hamlet hamlet = new Hamlet(chunkPosition[0], chunkPosition[1]);
                chunks.put(hamlet.id, hamlet);
            }
        }
        //If the border chunk's borders are now full, change that
        if (emptyBorders.size() == 1) {
            emptyBorderChunk.bordersFull = true;
        }
    }

    public static void handleControls() throws Exception {
        Controls.handleInputs();
    }

    public static void tick() throws Exception {
        //Controls
        handleControls();
        Packets.tickPackets();
        if (Raylib.GetTime() - lastPacketRemoval > packetRemovalInterval) {
            for (int i = 0; i < packets.size(); i++) {
                if (packets.get(i).hasArrived) {
                    packets.remove(i);
                    i--;
                }
            }
        }
        if (Raylib.GetTime() - lastGenerate > generateInterval) {
            generate();
            //Reset generation time
            lastGenerate = (int) Raylib.GetTime();
        }
    }

    public static void draw() {
        ClearBackground(RED);
        Raylib.BeginMode2D(Player.camera);
        for (Cable value : cables.values()) {
            value.draw();
        }
        for (Router value : routers.values()) {
            value.draw();
        }
        for (Structure building : buildings.values()) {
            building.draw();
        }
        //Draw cable placement
        if (Player.placingCable) {
            Structure startStructure = MainGame.getStructureById(Player.firstStructure);
            assert startStructure != null;
            Raylib.DrawLine(startStructure.gridX * grid.cellWindowWidth, startStructure.gridY * grid.cellWindowHeight, (int) GetScreenToWorld2D(GetMousePosition(), Player.camera).x(), (int) GetScreenToWorld2D(GetMousePosition(), Player.camera).y(), WHITE);
        }
        //Draw packets
        for (Packet packet : MainGame.packets) {
            packet.draw();
        }
        //Draw chunk borders
        for (Chunk chunk : chunks.values()) {
            Raylib.DrawText(String.valueOf(chunk.id), chunk.chunkX * grid.chunkWidth * grid.cellWindowWidth, chunk.chunkY * grid.chunkHeight * grid.cellWindowHeight, 3, WHITE);
            Raylib.DrawRectangleLines(chunk.chunkX * grid.chunkWidth * grid.cellWindowWidth, chunk.chunkY * grid.chunkWidth * grid.cellWindowHeight, grid.chunkWidth * grid.cellWindowWidth, grid.chunkHeight * grid.cellWindowHeight, BLACK);
        }
        Raylib.DrawRectangle(-2, -2, 4, 4, BLACK);
        Raylib.EndMode2D();
        Raylib.DrawText("Total structures: " + (routers.size() + cables.size()), 10, 10, 10, BLACK);
        Gui.draw();
    }
}