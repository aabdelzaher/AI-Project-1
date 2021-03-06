package My_package;

public class Node {
    State state;
    Node parent;
    Operator operator;
    int cost;
    int depth;
    int insertionTime;  // used in applying dfs and bfs

    public Node(State state, Node parent, Operator operator, int cost, int depth){
        this.state = state;
        this.parent = parent;
        this.depth = depth;
        this.cost = cost;
        this.operator = operator;
    }
}
