package game.scenes.maingame;

import game.objects.Packet;
import game.objects.buildings.Consumer;
import game.objects.buildings.Server;
import game.objects.infrastructure.Cable;
import game.objects.infrastructure.Router;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

public class Construct {
    //This class contains the methods used to build new structures
    public static void placeRouter(int level, int gridX, int gridY) {
        if (!MainGame.checkSolidByGridPos(gridX, gridY)) {
            //Must create the router first to generate the ID
            Router newRouter = new Router(level, gridX, gridY);
            MainGame.routers.put(newRouter.id, newRouter);
            MainGame.structures.put(newRouter.id, newRouter);
        }
        else {
            System.out.println("Cannot place here: Structure already exists");
            System.out.println(NodeGraph.findShortestPath(MainGame.getStructureByGridPos(gridX, gridY).id, MainGame.HQId));
        }
    }
    public static void placeCable(int level, int sourceRouterId, int destRouterId) throws Exception {
        if (!MainGame.checkRouterById(sourceRouterId) && !MainGame.checkRouterById(destRouterId)) {
            System.out.println("Cannot place cable: Routers do not exist!");
            return;
        }
        //Check that a cable here doesn't exist
        if (MainGame.checkCableByStructureIds(sourceRouterId, destRouterId)) {
            //If it does, get the cable
            Cable cable = MainGame.getCableByStructureIds(sourceRouterId, destRouterId);
            //Now upgrade the cable
            cable.upgrade(level);
            return;
        }
        //If it doesn't, create it!
        Cable newCable = new Cable(level, sourceRouterId, destRouterId);
        MainGame.cables.put(newCable.id, newCable);
        MainGame.cablesIdMap.put(sourceRouterId + "," + destRouterId, newCable);
        MainGame.cablesIdMap.put(destRouterId + "," + sourceRouterId, newCable);

        //Now recalculate whether a building is connected to the HQ
        for (Structure building : MainGame.buildings.values()) {
            building.connectedToHq = NodeGraph.checkHqConnection(building.id);
        }

        //Set available servers & consumers in packets
        Packets.availableServers.clear();
        Packets.availableConsumers.clear();
        for (Structure building : MainGame.buildings.values()) {
            if (building.connectedToHq) {
                if (building instanceof Server) {
                    Packets.availableServers.add(building.id);
                }
                else if (building instanceof Consumer) {
                    Packets.availableConsumers.add(building.id);
                }
            }
        }

        //Now calculate all the cached paths for consumers & servers
        for (Consumer consumer : MainGame.consumers.values()) {
            if (consumer.connectedToHq) {
                consumer.calculatePaths();
            }
        }
        for (Integer structureId : Packets.availableServers) {
            Server server = MainGame.getStructureById(structureId);
        }
    }
}
