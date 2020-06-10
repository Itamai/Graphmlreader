public class Edge implements GraphInterface  {

    private Node sourceNode;
    private Node targetNode;

    private int id;
    private int weight;

    public Edge(Node sourceNode, Node targetNode, int id, int Weight) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.id = id;
        this.weight = Weight;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

}
