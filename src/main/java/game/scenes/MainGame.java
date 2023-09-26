package game.scenes;

import game.objects.Packet;
import game.objects.Player;
import game.objects.buildings.Consumer;
import game.objects.buildings.Server;
import game.objects.chunks.ServerPark;
import game.objects.infrastructure.Cable;
import game.objects.infrastructure.Router;
import game.objects.infrastructure.Structure;
import game.scenes.maingame.*;
import game.objects.chunks.Hamlet;
import game.scenes.maingame.chunks.InitChunks;
import game.objects.chunks.SmallOfficePark;
import game.objects.chunks.SpawnChunk;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.raylib.java.Raylib;

import static com.raylib.java.core.Color.BLACK;
import static com.raylib.java.core.Color.WHITE;
import static com.raylib.java.core.rCore.GetMousePosition;
import static com.raylib.java.core.rCore.GetTime;
import static game.Window.properties.rl;

public class MainGame {
    //Used for the timing of packet removal
    //Indicates the interval in seconds at which packets should be removed
    public static int packetRemovalInterval = 5;
    //Indicated the last time that packets where removed
    public static int lastPacketRemoval;
    //The ammount of time to wait between generating a new building
    public static int generateInterval = 1;
    //The last time that a building was generated
    public static int lastGenerate;
    //Various hash maps containing different game objects
    public static HashMap<Integer, Router> routers = new HashMap<>();
    public static HashMap<Integer, Cable> cables = new HashMap<>();
    public static HashMap<String, Cable> cablesIdMap = new HashMap<>();
    public static HashMap<Integer, Consumer> consumers = new HashMap<>();
    public static HashMap<Integer, Server> servers = new HashMap<>();
    public static HashMap<Integer, Structure> buildings = new HashMap<>();
    public static HashMap<Integer, Chunk> chunks = new HashMap<>();
    public static HashMap<Integer, Structure> structures = new HashMap<>();
    //A list of all packets currently travelling through the network
    public static List<Packet> packets = new ArrayList<>();
    //The node ID of the player's HQ, used later for ensuring that a given building is connected to a destination node
    public static int HQId;

    public static class grid {
        //Diameters of a cell in window measurements
        public static int cellWindowWidth = 10;
        public static int cellWindowHeight = 10;

        //Diameters of a chunk in cell measurements
        public static int chunkWidth = 10;
        public static int chunkHeight = 10;
    }
    public static Structure getStructureByGridPos(int gridX, int gridY) {
        //Gets a structure from grid position
        for (Structure structure : structures.values()) {
            if (structure.gridX == gridX && structure.gridY == gridY) {
                return(structure);
            }
        }
        //Return null if no structure exists in the hash map
        return null;
    }
    public static Structure getStructureById(int id) {
        //Returns a structure by ID
        if (structures.containsKey(id)) {
            return(structures.get(id));
        }
        //Returns null if no structure exists with the given ID
        return(null);
    }
    public static boolean checkSolidByGridPos(int gridX, int gridY) {
        //Returns a boolean indicating whether a router OR building is present
        for (Structure structure : structures.values()) {
            if (structure.gridX == gridX && structure.gridY == gridY) {
                //Returne true here if the given position matches that of the found building
                return(true);
            }
        }
        //Returns false if neither a building nor a router is present
        return false;
    }
    public static boolean checkRouterById(int id) {
        //Returns a boolean indicating whether or not a router with the given ID exists in the hash table
        return routers.containsKey(id);
    }
    public static boolean checkChunkByChunkPos(int chunkX, int chunkY) {
        //Checks whether a chunk exists at a chiven chunk position (Note that this is different from a cell position, chunks contains multiple cells)
        for (Chunk chunk : chunks.values()) {
            if (chunk.chunkX == chunkX && chunk.chunkY == chunkY) {
                return true;
            }
        }
        //Returns false if no chunk has been generated at the given position 
        return false;
    }
    public static boolean checkCableByStructureIds(int sourceStructureId, int destStructureId) {
        //Returns a boolean if a cable / edge exists between two given structures (e.g a house and a router)
        return(NodeGraph.checkEdge(sourceStructureId, destStructureId));
    }
    public static Cable getCableByStructureIds(int sourceStructureId, int destStructureId) {
        //Returns a cable if one runs between the two given nodes. If no cable or edge exists, return Null
        String key = sourceStructureId + "," + destStructureId;
        if (cablesIdMap.containsKey(key)) {
            return(cablesIdMap.get(key));
        }
        return null;
    }
    public static Server getServerById(int id) {
        //Returns a server by the given ID
        if (servers.containsKey(id)) {
            return(servers.get(id));
        }
        //Return null if no server exists
        return null;
    }

    public static void init(int gridWidth, int gridHeight) {
        //Initializes the game
        Player.init();
        Gui.init();
        InitChunks.init();
        //A chunk must first be created to kickstart chunk generation
        SpawnChunk firstChunk = new SpawnChunk(0, 0);
        firstChunk.generateFull();
        chunks.put(firstChunk.id, firstChunk);
        lastPacketRemoval = (int) GetTime();
    }
    public static void generate() {
        //First, check if any chunks are not already full
        List<Chunk> notFullChunks = new ArrayList<>();
        for (Chunk chunk : chunks.values()) {
            if (!chunk.full) {
                //If there is a chunk which is not full, add it to the list of chunks which can be generated
                notFullChunks.add(chunk);
            }
        }
        if (notFullChunks.size() > 0) {
            int selectedChunk;
            //Out of the chunks which are not full, pick a random one, or the only one, and add a building to that chunk
            //If all chunks are full, a new chunk will be generated later
            if (notFullChunks.size() == 1) {
                selectedChunk = 0;
            }
            else {
                selectedChunk = ThreadLocalRandom.current().nextInt(0, notFullChunks.size());
            }
            //Exit if a chunk had a building added to it
            notFullChunks.get(selectedChunk).generateOne();
            return;
        }
        //If not, then create a new chunk
        //First, all the chunks with an empty border must be selected
        List<Chunk> emptyBorderChunks = new ArrayList<>();
        for (Chunk chunk : chunks.values()) {
            if (!chunk.bordersFull) {
                emptyBorderChunks.add(chunk);
            }
        }
        //Out of these chunks...
        int emptyBorderChunkIndex;
        if (emptyBorderChunks.size() == 1) {
            //Select the only existing one if only 1 chunk has an empty border
            emptyBorderChunkIndex = 0;
        }
        else {
            //or select a random one if there are multiple
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
        //Now, select a random empty border
        int chunkBorder;
        if (emptyBorders.size() == 0) {
            //If the chunk had no empty borders, we can set it to "full", since there was likely an error somewhere. We can return and the chunk will be skipped next time.
            //It's unlikely that this branch will ever be executed, but it acts as a failsafe.
            emptyBorderChunk.bordersFull = true;
            return;
        }
        if (emptyBorders.size() == 1) {
            //Set the chunk border to the only integer in emptyBorders, since there is only one
            chunkBorder = emptyBorders.get(0);
        }
        else {
            //Pick a random border out of the empty borders around the chunk
            chunkBorder = emptyBorders.get(ThreadLocalRandom.current().nextInt(0, emptyBorders.size()));
        }
        //Used as a 2D position vector
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
        //Select a chunk type to generate
        randomChunk = ThreadLocalRandom.current().nextInt(1, 4);
        //INSERT CODE TO GEN RANDOM CHUNK HERE

        switch(randomChunk) {
            case 1 -> {
                Hamlet hamlet = new Hamlet(chunkPosition[0], chunkPosition[1]);
                chunks.put(hamlet.id, hamlet);
            }
            case 2 -> {
                SmallOfficePark officePark = new SmallOfficePark(chunkPosition[0], chunkPosition[1]);
                chunks.put(officePark.id, officePark);
            }
            case 3 -> {
                ServerPark serverPark = new ServerPark(chunkPosition[0], chunkPosition[1]);
                chunks.put(serverPark.id, serverPark);
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
        packets.removeIf(packet -> packet.hasArrived || packet.shouldRemove);
        if (GetTime() - lastGenerate > generateInterval) {
            generate();
            //Reset generation time
            lastGenerate = (int) GetTime();
        }
    }

    public static void draw() {
        rl.core.ClearBackground(WHITE);
        rl.core.BeginMode2D(Player.camera);
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
            rl.shapes.DrawLine(startStructure.gridX * grid.cellWindowWidth, startStructure.gridY * grid.cellWindowHeight, (int) rl.core.GetScreenToWorld2D(GetMousePosition(), Player.camera).x, (int) rl.core.GetScreenToWorld2D(GetMousePosition(), Player.camera).y, BLACK);
        }
        //Draw packets
        for (Packet packet : MainGame.packets) {
            packet.draw();
        }
        //Draw chunk borders
        //for (Chunk chunk : chunks.values()) {
        //    Raylib.DrawText(String.valueOf(chunk.id) + ", " + chunk.bordersFull, chunk.chunkX * grid.chunkWidth * grid.cellWindowWidth, chunk.chunkY * grid.chunkHeight * grid.cellWindowHeight, 3, WHITE);
        //    Raylib.DrawRectangleLines(chunk.chunkX * grid.chunkWidth * grid.cellWindowWidth, chunk.chunkY * grid.chunkWidth * grid.cellWindowHeight, grid.chunkWidth * grid.cellWindowWidth, grid.chunkHeight * grid.cellWindowHeight, BLACK);
        //}
        rl.shapes.DrawRectangle(-2, -2, 4, 4, BLACK);
        rl.core.EndMode2D();
        //Draw money and packet details
        rl.text.DrawText("Delivered Packets: " + Player.deliveredPackets, 120, 10, 10, BLACK);
        rl.text.DrawText("Lost Packets: " + Player.lostPackets, 250, 10, 10, BLACK);
        rl.text.DrawText("Total structures: " + (routers.size() + cables.size()), 10, 10, 10, BLACK);
        Gui.draw();
    }
}
