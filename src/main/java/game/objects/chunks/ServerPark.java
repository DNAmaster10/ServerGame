package game.objects.chunks;

import game.scenes.maingame.Chunk;
import game.scenes.maingame.Ids;
import game.scenes.maingame.chunks.Chunks;

public class ServerPark extends Chunk {
    public ServerPark(int chunkX, int chunkY) {
        super.id = Ids.getNewId();
        super.neededBuildings = Chunks.serverParkBuildings;
        super.chunkX = chunkX;
        super.chunkY = chunkY;
    }
}
