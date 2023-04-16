package game.scenes.maingame.chunks;

import game.scenes.maingame.Chunk;
import game.scenes.maingame.Ids;

import java.util.ArrayList;
import java.util.List;

public class SpawnChunk extends Chunk {
    public SpawnChunk(int chunkX, int chunkY) {
        super.id = Ids.getNewId();
        System.out.println("Spawn chunk ID: " + super.id);
        super.neededBuildings = Chunks.spawnChunkBuildings;
        super.chunkX = chunkX;
        super.chunkY = chunkY;
    }
}
