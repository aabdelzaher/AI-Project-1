package My_package;

import java.util.*;

public class GenericSearch {
//    HashSet<State> set = new HashSet<>();
    TreeSet<State> set = new TreeSet<>();   // used for saving the states to prevent going to repeated states

    /**
     *
     * @param p The problem instance given to the search function
     * @param c The queueing function for the strategy used
     * @param maxLevelForID The cutoff level, it is only used in iterative deepening, other strategies should set this parameter to -1
     * @return Triple of the path used to get to the goal, the number of expanded nodes, and the cost to the goal.
     */
    public Triple genericSearch(Problem p, Comparator<Node> c, int maxLevelForID){
        int time = 0;   // this is used to save the insertion time to be used in DFS and BFS
        int expandedNodes = 0;
        PriorityQueue<Node> nodes = new PriorityQueue<Node>(c);
        Node initialNode = new Node(p.initialState, null, null, 0, 0);
        initialNode.insertionTime = ++time;
        nodes.add(initialNode);

        while(true){
            if(nodes.isEmpty())
                return null;
            Node u = nodes.poll();
            if(p.goalTest.test(u.state)) {
                // reconsturcting the path
                ArrayList<Operator> path = new ArrayList<>();
                Node cur = u;
                while(cur.parent != null){
                    path.add(cur.operator);
                    cur = cur.parent;
                }

                Operator[] ret = new Operator[path.size()];
                for (int i = 0, j = path.size()-1; i < ret.length; i++)
                    ret[i] = path.get(j--);

                return new Triple(ret, expandedNodes, u.cost);
            }

            expandedNodes++;
            // expanding the node
            for (Operator o: p.operators) {
                State next = p.stateSpace.computeNextState(u.state, o);
                Node nextNode = new Node(next, u, o, u.cost + p.pathCost.cost(o), u.depth+1);
                if(set.contains(nextNode.state))    // Do not visit repeated states
                    continue;
                if(nextNode.depth == maxLevelForID) // Cutoff level for Iterative Deepening
                    continue;
                nextNode.insertionTime = ++time;
                nodes.add(nextNode);
                set.add(nextNode.state);    // Saving the state so it is not visited again
            }
        }
    }


    static class Triple{
        Operator[] path;
        int expandedNodes;
        int cost;

        public Triple(Operator[] path, int expandedNodes, int cost) {
            this.path = path;
            this.expandedNodes = expandedNodes;
            this.cost = cost;
        }
    }
}
