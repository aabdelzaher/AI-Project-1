package My_package;

public class Node {
    State state;
    Node parent;
    Operator operator;
    int cost;
    int depth;
    int insertionTime;

    public Node(State state, Node parent, Operator operator, int cost, int depth){
        this.state = state;
        this.parent = parent;
        this.depth = depth;
        this.cost = cost;
        this.operator = operator;
    }

    int getHeurestic1(){
        if(new SaveWestros().test(state))
            return 0;
//        return this.state.whiteWalkers.size()/4;
        if(this.state.whiteWalkers.size()/4 != 0 && this.state.whiteWalkers.size()/4 != 1) {
            System.err.println("BUGGGGGGGGG :O :O");
            while(true);
        }
//        return Math.min(this.state.whiteWalkers.size()/4, 1);
        return (int)(state.hashCode()%2);
//        return 1;
//        return 0;
//        return 0;
    }

    int getHeurestic2(){
        if(new SaveWestros().test(state))
            return 0;
        return 0;
    }
}
