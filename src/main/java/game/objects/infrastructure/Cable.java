package game.objects.infrastructure;

import com.raylib.Raylib;
import game.scenes.MainGame;
import game.scenes.maingame.Ids;
import game.scenes.maingame.NodeGraph;

import java.io.IOError;

import static com.raylib.Jaylib.BLACK;
import static com.raylib.Raylib.DrawLine;
import static com.raylib.Raylib.DrawLineEx;
import static java.lang.Math.sqrt;

public class Cable {
    public int id;
    //The ammount of time in seconds it takes for a packet to travel from one end to the other
    public float travelTime;
    //The length of the cable in grid length
    public float gridLength;
    public int windowLength;
    public int reliability;
    public int windowXPosStart;
    public int windowYPosStart;
    public int windowXPosEnd;
    public int windowYPosEnd;
    public Raylib.Vector2 startPos = new Raylib.Vector2();
    public Raylib.Vector2 endPos = new Raylib.Vector2();
    public int sourceStructureId;
    public int destStructureId;
    //These are the numbers to add to x and y for packets each step
    public float deltaXPerSecond;
    public float deltaYPerSecond;

    public float reverseXPerSecond;
    public float reverseYPerSecond;

    public float[] getDeltas(int sourceId, int destId) {
        //returns a different delta x or delta y depending on which is the source and dest
        if (sourceId == this.sourceStructureId) {
            float[] returnFloat = new float[2];
            returnFloat[0] = this.deltaXPerSecond;
            returnFloat[1] = this.reverseYPerSecond;
            return returnFloat;
        }
        else {
            float[] returnFloat = new float[2];
            returnFloat[0] = this.reverseXPerSecond;
            returnFloat[1] = this.deltaYPerSecond;
            return returnFloat;
        }
    }

    public void draw() {

        DrawLineEx(startPos, endPos, 1, BLACK);
    }

    public void upgrade(int level) {
        switch(level) {
            case 1: {
                travelTime = gridLength / CopperCable.speed;
                if (gridLength > CopperCable.maxLength) {
                    reliability = (int) (CopperCable.maxReliability - ((CopperCable.maxLength - gridLength) * CopperCable.reliablityDrop));
                    if (reliability < CopperCable.minReliablity) {
                        reliability = CopperCable.minReliablity;
                    }
                }
                else {
                    reliability = CopperCable.maxReliability;
                }
            }
        }
        //update the connection in the node graph
        NodeGraph.addConnection(sourceStructureId, destStructureId, travelTime);
    }

    public Cable(int level, Integer sourceStructureId, Integer destStructureId) throws Exception {
        //get positions for structures
        Structure sourceStructure = MainGame.getStructureById(sourceStructureId);
        if (sourceStructure == null) {
            throw new Exception("No source structure with ID " + sourceStructureId + " could be found");
        }
        Structure destStructure = MainGame.getStructureById(destStructureId);
        if (destStructure == null) {
            throw new Exception("No dest structure with ID " + destStructure + " could be found");
        }

        this.id = Ids.getNewId();
        windowXPosStart = sourceStructure.gridX * MainGame.grid.cellWindowWidth;
        windowYPosStart = sourceStructure.gridY * MainGame.grid.cellWindowHeight;
        windowXPosEnd = destStructure.gridX * MainGame.grid.cellWindowWidth;
        windowYPosEnd = destStructure.gridY * MainGame.grid.cellWindowHeight;

        startPos.x(windowXPosStart);
        startPos.y(windowYPosStart);
        endPos.x(windowXPosEnd);
        endPos.y(windowXPosEnd);

        this.sourceStructureId = sourceStructureId;
        this.destStructureId = destStructureId;

        float deltaX = sourceStructure.gridX - destStructure.gridX;
        float deltaY = sourceStructure.gridY - destStructure.gridY;
        gridLength = (float) sqrt((deltaX * deltaX) + (deltaY * deltaY));

        deltaX = windowXPosStart - windowXPosEnd;
        deltaY = windowYPosStart - windowYPosEnd;
        windowLength = (int) sqrt((deltaX * deltaX) + (deltaY * deltaY));

        switch (level) {
            case 1 -> {
                travelTime = gridLength / CopperCable.speed;
                if (gridLength > CopperCable.maxLength) {
                    reliability = (int) (CopperCable.maxReliability - ((CopperCable.maxLength - gridLength) * CopperCable.reliablityDrop));
                    if (reliability < CopperCable.minReliablity) {
                        reliability = CopperCable.minReliablity;
                    }
                } else {
                    reliability = CopperCable.maxReliability;
                }
            }
        }
        //Recalculate correct verion of deltaX
        deltaX = windowXPosEnd - windowXPosStart;
        this.deltaXPerSecond = deltaX / travelTime;
        this.deltaYPerSecond = deltaY / travelTime;

        //Now do the reverse calculations
        deltaX = windowXPosStart - windowXPosEnd;
        deltaY = windowYPosEnd - windowYPosStart;
        this.reverseXPerSecond = deltaX / travelTime;
        this.reverseYPerSecond = deltaY / travelTime;

        //Register connection in node graph
        NodeGraph.addConnection(sourceStructureId, destStructureId, travelTime);
    }
}
