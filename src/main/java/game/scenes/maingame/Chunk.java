package game.scenes.maingame;

import game.objects.buildings.*;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Chunk {
    public int id;
    //This class is used for building generation & whatnot
    public int chunkX;
    public int chunkY;
    public boolean full = false;
    //This boolean indicated whether all the adjacent
    //chunks around this chunk are full
    public boolean bordersFull = false;

    public List<Structure> buildings = new ArrayList<>();
    public int[] neededBuildings;

    public void generateOne() {
        //First, check if buildings are actually needed
        if (this.buildings.size() == this.neededBuildings.length) {
            this.full = true;
            return;
        }

        //First, find empty cells
        List<int[]> emptyCells = new ArrayList<>();
        boolean outputed = false;
        for (int x = this.chunkX * MainGame.grid.chunkWidth; x < (this.chunkX * MainGame.grid.chunkWidth) + MainGame.grid.chunkWidth; x++) {
            for (int y = this.chunkY * MainGame.grid.chunkHeight; y < (this.chunkY * MainGame.grid.chunkHeight) + MainGame.grid.chunkHeight; y++) {
                if (!outputed) {
                    outputed = true;
                }
                if (!MainGame.checkSolidByGridPos(x, y)) {
                    emptyCells.add(new int[]{x, y});
                }
            }
        }
        //If there are none, set the chunk full boolean to true and return
        if (emptyCells.size() == 0) {
            this.full = true;
            return;
        }
        //Now, select a random index in empty cells list
        int emptySpotIndex;
        if (emptyCells.size() == 1) {
            emptySpotIndex = 0;
        }
        else {
            emptySpotIndex = ThreadLocalRandom.current().nextInt(0, emptyCells.size());
        }

        //Now we have an empty spot, populate it
        switch(neededBuildings[buildings.size()]) {
            case 0 -> {
                //THIS SHOULD ONLY RUN ONCE AT THE START OF THE GAME!
                Headquarters hq = new Headquarters(emptyCells.get(emptySpotIndex)[0], emptyCells.get(emptySpotIndex)[1]);
                MainGame.buildings.put(hq.id, hq);
                MainGame.structures.put(hq.id, hq);
                this.buildings.add(hq);
                MainGame.HQId = hq.id;
            }
            case 1 -> {
                House house = new House(emptyCells.get(emptySpotIndex)[0], emptyCells.get(emptySpotIndex)[1]);
                MainGame.buildings.put(house.id, house);
                MainGame.structures.put(house.id, house);
                MainGame.consumers.put(house.id, house);
                this.buildings.add(house);
            }
            case 2 -> {
                //Apartment
            }
            case 3 -> {
                Office office = new Office(emptyCells.get(emptySpotIndex)[0], emptyCells.get(emptySpotIndex)[1]);
                MainGame.buildings.put(office.id, office);
                MainGame.structures.put(office.id, office);
                MainGame.consumers.put(office.id, office);
                this.buildings.add(office);
            }
            case 4 -> {
                //Large Office
            }
            case 5 -> {
                //Small commercial
            }
            case 6 -> {
                //Large Commercial
            }
            case 7 -> {
                //School
            }
            case 8 -> {
                Server server = new Server(emptyCells.get(emptySpotIndex)[0], emptyCells.get(emptySpotIndex)[1]);
                MainGame.buildings.put(server.id, server);
                MainGame.structures.put(server.id, server);
                MainGame.servers.put(server.id, server);
                this.buildings.add(server);
            }
        }
        if (buildings.size() == neededBuildings.length) {
            this.full = true;
        }
    }
    public void generateFull() {
        //First, find empty cells
        List<int[]> emptyCells = new ArrayList<>();
        for (int x = this.chunkX * MainGame.grid.chunkWidth; x < MainGame.grid.chunkWidth; x++) {
            for (int y = this.chunkY * MainGame.grid.chunkHeight; y < MainGame.grid.chunkHeight; y++) {
                if (!MainGame.checkSolidByGridPos(x, y)) {
                    emptyCells.add(new int[]{x, y});
                }
            }
        }
        //If there are none, set the chunk full boolean to true and return
        if (emptyCells.size() == 0) {
            this.full = true;
            return;
        }

        //Now, for each needed building which hasn't been built
        for (int i = buildings.size(); i < neededBuildings.length; i++) {
            //If there are not empty spaces left, break;
            if (emptyCells.size() == 0) {
                break;
            }
            //If the empty spot count is 1, use that
            int emptySpotIndex;
            if (emptyCells.size() == 1) {
                emptySpotIndex = 1;
            }
            else {
                emptySpotIndex = ThreadLocalRandom.current().nextInt(0, emptyCells.size());
            }
            //Now we have an empty spot index, create the next needed building at this spot
            switch(neededBuildings[i]) {
                case 0 -> {
                    //THIS SHOULD ONLY RUN ONCE AT THE START OF THE GAME!
                    Headquarters hq = new Headquarters(emptyCells.get(emptySpotIndex)[0], emptyCells.get(emptySpotIndex)[1]);
                    MainGame.buildings.put(hq.id, hq);
                    MainGame.structures.put(hq.id, hq);
                    this.buildings.add(hq);
                    MainGame.HQId = hq.id;
                }
                case 1 -> {
                    House house = new House(emptyCells.get(emptySpotIndex)[0], emptyCells.get(emptySpotIndex)[1]);
                    MainGame.buildings.put(house.id, house);
                    MainGame.structures.put(house.id, house);
                    MainGame.consumers.put(house.id, house);
                    this.buildings.add(house);
                }
                case 2 -> {
                    //Apartment
                }
                case 3 -> {
                    SmallOffice office = new SmallOffice(emptyCells.get(emptySpotIndex)[0], emptyCells.get(emptySpotIndex)[1]);
                    MainGame.buildings.put(office.id, office);
                    MainGame.structures.put(office.id, office);
                    MainGame.consumers.put(office.id, office);
                    this.buildings.add(office);
                }
                case 4 -> {
                    //Large Office
                }
                case 5 -> {
                    //Small commercial
                }
                case 6 -> {
                    //Large Commercial
                }
                case 7 -> {
                    //School
                }
                case 8 -> {
                    Server server = new Server(emptyCells.get(emptySpotIndex)[0], emptyCells.get(emptySpotIndex)[1]);
                    MainGame.buildings.put(server.id, server);
                    MainGame.structures.put(server.id, server);
                    MainGame.servers.put(server.id, server);
                    this.buildings.add(server);
                }
            }
            //Now remove the empty spot from the empty spot list
            emptyCells.remove(emptySpotIndex);
        }
        //Now change the chunk properties e.t.c
        this.full = true;
    }

    public List<Structure> getBuildings() {
        return(buildings);
    }
}