package game.objects.chunks;

import game.scenes.maingame.Chunk;
import game.scenes.maingame.Ids;
import game.scenes.maingame.chunks.Chunks;

import java.util.ArrayList;
import java.util.List;

public class Hamlet extends Chunk {
    public Hamlet(int chunkX, int chunkY) {
        super.id = Ids.getNewId();
        super.neededBuildings = Chunks.hamletChunkBuildings;
        super.chunkX = chunkX;
        super.chunkY = chunkY;
    }
}
