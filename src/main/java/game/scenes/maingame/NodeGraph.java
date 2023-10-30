package game.scenes.maingame;

import game.objects.Packet;
import game.objects.buildings.Consumer;
import game.objects.buildings.Server;
import game.objects.infrastructure.Cable;
import game.objects.infrastructure.Router;
import game.objects.infrastructure.Structure;
import game.scenes.MainGame;

import java.util.*;

public class NodeGraph {
    //This class is responsible for the path finding for packets and holding the information for the node graph
    //The class contains information about the entire player built network, but exists entirely seperately to the rest of the code
    
    //The connections hashmap is a hash map where each key is a source node
    public static HashMap<Integer, List<NodeConnection>> connections = new HashMap<>();

    //Add a connection to the node graph
    public static void addConnection(int sourceNodeId, int destNodeId, float weight) {
        boolean containsEdge = false;
        
        //If the source node already exsits in the node graph
        if (connections.containsKey(sourceNodeId)) {
            for (int i = 0; i < connections.get(sourceNodeId).size(); i++) {
                if (connections.get(sourceNodeId).get(i).getDestNode() == destNodeId) {
                    connections.get(sourceNodeId).get(i).setWeight(weight);
                    containsEdge = true;
                }
            }
            if (!containsEdge) {
                connections.get(sourceNodeId).add(new NodeConnection(destNodeId, weight));
            }
        }
        else {
            connections.put(sourceNodeId, new ArrayList<>());
            connections.get(sourceNodeId).add(new NodeConnection(destNodeId, weight));
        }
        containsEdge = false;
        if (connections.containsKey(destNodeId)) {
            for (int i = 0; i < connections.get(destNodeId).size(); i++) {
                if (connections.get(destNodeId).get(i).getDestNode() == sourceNodeId) {
                    connections.get(destNodeId).get(i).setWeight(weight);
                    containsEdge = true;
                }
            }
            if (!containsEdge) {
                connections.get(destNodeId).add(new NodeConnection(sourceNodeId, weight));
            }
        }
        else {
            connections.put(destNodeId, new ArrayList<>());
            connections.get(destNodeId).add(new NodeConnection(sourceNodeId, weight));
        }
    }

    public static void addOneWayConnection(int sourceNodeId, int destNodeId, int weight) {
        removeEdge(sourceNodeId, destNodeId);
        connections.get(sourceNodeId).add(new NodeConnection(destNodeId, weight));
    }

    public static boolean checkNode(int nodeId) {
        return(connections.containsKey(nodeId));
    }
    public static boolean checkEdge(int sourceNodeId, int destNodeId) {
        //Returns a boolean indicating whether a node already exists
        if (!connections.containsKey(sourceNodeId)) {
            return false;
        }
        else {
            for (int i = 0; i < connections.get(sourceNodeId).size(); i++) {
                if (connections.get(sourceNodeId).get(i).getDestNode() == destNodeId) {
                    return true;
                }
            }
        }
        return false;
    }
    public static void removeNode(int nodeId) {
        if (!connections.containsKey(nodeId)) {
            return;
        }
        //First, remove this node
        connections.remove(nodeId);

        //Then, remove the connections within the other nodes
        for (List<NodeConnection> connection : connections.values()) {
            for (int i = 0; i < connection.size(); i++) {
                if (connection.get(i).getDestNode() == nodeId) {
                    connection.remove(i);
                    i--;
                }
            }
        }
        //Remove those nodes if they have no more connections
        connections.entrySet().removeIf(e -> (e.getValue().size() == 0));
    }

    public static void removeEdge(int sourceNodeId, int destNodeId) {
        if(!checkEdge(sourceNodeId, destNodeId)) {
            return;
        }
        for (int i = 0; i < connections.get(sourceNodeId).size(); i++) {
            if(connections.get(sourceNodeId).get(i).getDestNode() == destNodeId) {
                connections.get(sourceNodeId).remove(i);
                if (connections.get(sourceNodeId).size() == 0) {
                    connections.remove(sourceNodeId);
                }
                break;
            }
        }
        for (int i = 0; i < connections.get(destNodeId).size(); i++) {
            if (connections.get(destNodeId).get(i).getDestNode() == sourceNodeId) {
                connections.get(destNodeId).remove(i);
                if (connections.get(destNodeId).size() == 0) {
                    connections.remove(destNodeId);
                }
                break;
            }
        }
    }

    public static boolean checkHqConnection(int nodeId) {
        //First check that the given node is in a network
        if (!connections.containsKey(nodeId)) {
            return(false);
        }
        //Check that the HQ is in a network
        if (!connections.containsKey(MainGame.HQId)) {
            return(false);
        }
        List<Integer> foundNodes = new ArrayList<>();
        foundNodes.add(nodeId);
        List<Integer> searchedNodes = new ArrayList<>();
        boolean isConnected = false;
        while(foundNodes.size() > 0 && !isConnected) {
            List<NodeConnection> currentNodeConnections = connections.get(foundNodes.get(0));
            for (NodeConnection currentNodeConnection : currentNodeConnections) {
                if (currentNodeConnection.getDestNode() == MainGame.HQId) {
                    isConnected = true;
                    break;
                }
                if (searchedNodes.contains(currentNodeConnection.getDestNode())) {
                    continue;
                }
                if (!foundNodes.contains(currentNodeConnection.getDestNode())) {
                    foundNodes.add(currentNodeConnection.getDestNode());
                }
            }
            searchedNodes.add(foundNodes.get(0));
            foundNodes.remove(0);
        }
        return(isConnected);
    }

    public static List<NodeConnection> findShortestPath(int startNode, int endNode) {
        //The following hash map uses node id's as keys.
        //Each value is an array of integers, 2 in length.
        //Position 0 is the total distance from the start node.
        //Position 1 is the previous node the given node connects to
        //on the shortest path to the start node.
        HashMap<Integer, DijkstraPath> pathMap = new HashMap<>();
        List<Integer> unvisited = new ArrayList<>();
        //Get all consumers who are connected to the HQ
        for (Consumer consumer : MainGame.consumers.values()) {
            if (consumer.connectedToHq) {
                pathMap.put(consumer.id, new DijkstraPath());
                unvisited.add(consumer.id);
            }
        }
        //Get all servers who are connected to the HQ
        for (Server server : MainGame.servers.values()) {
            if (server.connectedToHq) {
                pathMap.put(server.id, new DijkstraPath());
                unvisited.add(server.id);
            }
        }
        //Get all routers
        for (Router router : MainGame.routers.values()) {
            if (router.connectedToHq) {
                pathMap.put(router.id, new DijkstraPath());
                unvisited.add(router.id);
            }
        }
        //Lastly, get the HQ
        pathMap.put(MainGame.HQId, new DijkstraPath());
        unvisited.add(MainGame.HQId);

        //Now we have a hash map filled with all the id's which are connected to the HQ
        //Set the source distance
        if (!pathMap.containsKey(startNode)) {
            System.out.println("Error: Start node not found");
            return(null);
        }
        if (!pathMap.containsKey(endNode)) {
            System.out.println("Error: Dest node not found");
            return(null);
        }
        //Set start node distance to 0, since we are starting here
        pathMap.get(startNode).weight = 0f;

        //Now, we start the main loop
        Integer currentNode = 0;
        //Iterate until all nodes have been visited
        while (unvisited.size() > 0) {
            //Now we need to select the next node to visit.
            //This is done by finding the node with the shortest distance to the start node out of the nodes
            //not contained in the unvisited list.
            float minDistance = Float.MAX_VALUE;
            for (int i = 0; i < unvisited.size(); i++) {
                if (pathMap.get(unvisited.get(i)).weight < minDistance) {
                    minDistance = pathMap.get(unvisited.get(i)).weight;
                    currentNode = unvisited.get(i);
                }
            }
            //For each edge connecting to the current node we are visiting
            for (NodeConnection edge : connections.get(currentNode)) {
                //If the weight of the current node + the weight of the connection is less than what the node
                //currently has, update it in the pathMap hashmap
                if (edge.getWeight() + pathMap.get(currentNode).weight < pathMap.get(edge.getDestNode()).weight) {
                    pathMap.get(edge.getDestNode()).weight = edge.getWeight() + pathMap.get(currentNode).weight;
                    pathMap.get(edge.getDestNode()).previousId = currentNode;
                }
            }
            //Now move this node to the visited nodes
            unvisited.remove(unvisited.indexOf(currentNode));
        }
        //Now, we should have all paths calculated. We want to iterate back from the destination node to get the path
        List<Integer> path = new ArrayList<>();
        currentNode = endNode;
        path.add(0, currentNode);
        boolean startNodeReached = false;
        while (!startNodeReached) {
            path.add(0, pathMap.get(currentNode).previousId);
            currentNode = path.get(0);
            if (currentNode == startNode) {
                startNodeReached = true;
            }
        }
        //Now we have the list path. The path list contains integers indicating node id's.
        //For example, [0, 5, 2, 4]

        //Now, generate the path connection list from those integers
        List<NodeConnection> pathNodeConnection = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            //Find the node connection object
            for (NodeConnection connection : connections.get(path.get(i))) {
                if (connection.getDestNode() == path.get(i + 1)) {
                    pathNodeConnection.add(connection);
                    break;
                }
            }
        }
        return(pathNodeConnection);
    }

    public static void calculatePaths() {
        //Recalculates paths, connections to HQ e.t.c
        for (Structure building : MainGame.buildings.values()) {
            building.connectedToHq = NodeGraph.checkHqConnection(building.id);
        }
        //Now do the same for routers
        for (Router router : MainGame.routers.values()) {
            router.connectedToHq = NodeGraph.checkHqConnection(router.id);
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

        //Now calculate all the cached paths for consumers and servers
        for (Consumer consumer : MainGame.consumers.values()) {
            if (consumer.connectedToHq) {
                consumer.calculatePaths();
            }
        }
        for (Server server : MainGame.servers.values()) {
            if (server.connectedToHq) {
                server.calculatePaths();
            }
        }
    }

    public static void removeBrokenPackets(Router removedRouter) {
        //Flag any incoming packets to the removed router for removal
        for (Packet packet : removedRouter.arrivingPackets) {
            packet.remove();
        }
        //Flag any stored packets for removal
        for (Packet packet : removedRouter.storedPackets) {
            packet.remove();
        }
    }
}
