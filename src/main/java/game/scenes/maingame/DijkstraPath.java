package game.scenes.maingame;

public class DijkstraPath {
    //This is a special object used for calculating the shortest path between nodes
    public int previousId;
    public float weight;
    public DijkstraPath() {
        weight = Float.MAX_VALUE;
    }
}
