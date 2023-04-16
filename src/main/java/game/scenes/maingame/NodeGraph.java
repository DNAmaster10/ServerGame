package game.scenes.maingame;

import game.scenes.MainGame;

import java.util.*;

public class NodeGraph {
    //This class is responsible for the path finding for packets
    //and holding the information for the node graph

    //The connections variable is a hash map where each key is a source node

    public static HashMap<Integer, List<NodeConnection>> connections = new HashMap<>();

    public static void addConnection(int sourceNodeId, int destNodeId, int weight) {
        boolean containsEdge = false;
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
        if (!checkNode(nodeId)) {
            return;
        }
        connections.remove(nodeId);
        for (Map.Entry<Integer, List<NodeConnection>> set : connections.entrySet()) {
            for (int i = 0; i < set.getValue().size(); i++) {
                if(set.getValue().get(i).getDestNode() == nodeId) {
                    set.getValue().remove(i);
                    if (set.getValue().size() == 0) {
                        connections.remove(set.getKey());
                    }
                    break;
                }
            }
        }
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
        // Initialize a priority queue and a map to keep track of distances to nodes
        PriorityQueue<NodeConnection> pq = new PriorityQueue<>(Comparator.comparingInt(NodeConnection::getWeight));
        Map<Integer, Integer> distances = new HashMap<>();

        // Add the start node to the queue and set its distance to zero
        pq.offer(new NodeConnection(startNode, 0));
        distances.put(startNode, 0);

        // Loop until the queue is empty or we have found the shortest path to the end node
        while (!pq.isEmpty()) {
            // Get the node with the smallest distance from the start node
            NodeConnection current = pq.poll();

            // If we have found the shortest path to the end node, return the path
            if (current.getDestNode() == endNode) {
                List<NodeConnection> path = new ArrayList<>();
                int node = endNode;
                while (node != startNode) {
                    for (NodeConnection conn : connections.get(node)) {
                        if (distances.get(node) - conn.getWeight() == distances.get(conn.getDestNode())) {
                            path.add(0, conn);
                            node = conn.getDestNode();
                            break;
                        }
                    }
                }
                return path;
            }

            // Loop through the node's outgoing edges and update their distances if necessary
            for (NodeConnection conn : connections.get(current.getDestNode())) {
                int newDistance = distances.get(current.getDestNode()) + conn.getWeight();
                if (!distances.containsKey(conn.getDestNode()) || newDistance < distances.get(conn.getDestNode())) {
                    distances.put(conn.getDestNode(), newDistance);
                    pq.offer(new NodeConnection(conn.getDestNode(), newDistance));
                }
            }
        }

        // If we get here, there is no path from the start node to the end node
        return null;
    }
}
