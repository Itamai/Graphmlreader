public class Node implements GraphInterface {

    private int id;
    private double betweennessCentrality;

    public Node(int id) {
        this.id = id;
        this.betweennessCentrality = 0;
    }

    @Override
    public int getId() {
        return id;
    }

    public double getBetweennessCentrality(){
        return betweennessCentrality;
    }

    /**
     * Updating the betweenness centrality of the node by summing up the current value with the value in the parameter
     * @param value value to add to the sum
     */
    public void adjustBetweennessCentrality(double value){
        betweennessCentrality += value;
    }
}
