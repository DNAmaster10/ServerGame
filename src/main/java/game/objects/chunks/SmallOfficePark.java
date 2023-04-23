package game.objects.chunks;

import game.scenes.maingame.Chunk;
import game.scenes.maingame.Ids;
import game.scenes.maingame.chunks.Chunks;

public class SmallOfficePark extends Chunk {
    public SmallOfficePark(int chunkX, int chunkY) {
        super.id = Ids.getNewId();
        super.neededBuildings = Chunks.smallOfficeParkBuildings;
        super.chunkX = chunkX;
        super.chunkY = chunkY;
    }
}
