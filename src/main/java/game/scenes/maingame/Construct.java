package game.scenes.maingame;

import game.Main;
import game.objects.Packet;
import game.objects.buildings.Consumer;
import game.objects.buildings.Server;
import game.objects.infrastructure.Cable;
import game.objects.infrastructure.Router;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Construct {
    //This class contains the methods used to build new structures
    public static void placeRouter(int level, int gridX, int gridY) {
        if (!MainGame.checkSolidByGridPos(gridX, gridY)) {
            //Must create the router first to generate the ID
            Router newRouter = new Router(level, gridX, gridY);
            MainGame.routers.put(newRouter.id, newRouter);
            MainGame.structures.put(newRouter.id, newRouter);
        }
        else if (MainGame.getStructureByGridPos(gridX, gridY) instanceof Router) {
            assert MainGame.getStructureByGridPos(gridX, gridY) != null;
            ((Router) MainGame.getStructureByGridPos(gridX, gridY)).upgrade(level);
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

        NodeGraph.calculatePaths();
    }

    public static void removeRouterByGridPos(int gridX, int gridY) {
        Structure structure = MainGame.getStructureByGridPos(gridX, gridY);
        assert structure != null;
        removeRouterById(structure.id);
    }
    public static void removeRouterById(int id) {
        if (!MainGame.routers.containsKey(id)) {
            return;
        }

        //First remove the router from structures
        MainGame.structures.remove(id);

        Router removedRouter = MainGame.routers.get(id);
        //Routers
        MainGame.routers.remove(id);

        //Remove cables
        int[] routerIds = new int[2];
        for (Cable cable : MainGame.cables.values()) {
            if (cable.sourceStructureId == id || cable.destStructureId == id) {
                routerIds[0] = cable.sourceStructureId;
                routerIds[1] = cable.destStructureId;
                MainGame.cablesIdMap.remove(routerIds[0] + "," + routerIds[1]);
                MainGame.cablesIdMap.remove(routerIds[1] + "," + routerIds[0]);
            }
        }
        MainGame.cables.entrySet().removeIf(e-> (e.getValue().sourceStructureId == id || e.getValue().destStructureId == id));

        //Remove from node graph
        NodeGraph.removeNode(id);

        NodeGraph.calculatePaths();

        NodeGraph.removeBrokenPackets(removedRouter);
    }

    public static void removeCableByStructureIds(int source, int dest) {
        if (!MainGame.checkCableByStructureIds(source, dest)) {
            return;
        }
        Cable cable = MainGame.getCableByStructureIds(source, dest);
        assert cable != null;
        MainGame.cables.remove(cable.id);

        MainGame.cablesIdMap.remove(source + "," + dest);
        MainGame.cablesIdMap.remove(dest + "," + source);

        NodeGraph.removeEdge(source, dest);

        NodeGraph.calculatePaths();
    }
}
