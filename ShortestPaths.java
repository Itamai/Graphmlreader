import java.util.List;

public class ShortestPaths {

    private List<List<Node>> paths;
    private int distance;

    /**
     * Constructor for creating an ShortestPaths saving the paths and distance got from the parameters
     * @param paths all shortest paths
     * @param distance the distance for the shortest paths
     */
    public ShortestPaths(List<List<Node>> paths, int distance){
        this.paths = paths;
        this.distance = distance;
    }

    /**
     * Returns all paths stored in this object
     * @return all shortest paths
     */
    public List<List<Node>> getPaths(){
        return paths;
    }

    /**
     * Returns the distance those shortest paths needed
     * @return the distance value
     */
    public int getDistance(){
        return distance;
    }

}
