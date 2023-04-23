package game.scenes.maingame;

public class NodeConnection {
    //This is a special class used to hold weighted edge connections

    //The node that this edge connects to
    private int destNode;

    //The weight of this edge
    private float weight;

    public int getDestNode() {
        return(destNode);
    }

    public float getWeight() {
        return (weight);
    }

    public void setDestNode(int destinationNode) {
        destNode = destinationNode;
    }

    public void setWeight(float newWeight) {
        weight = newWeight;
    }

    public NodeConnection(int destinationNode, float edgeWeight) {
        destNode = destinationNode;
        weight = edgeWeight;
    }
}
