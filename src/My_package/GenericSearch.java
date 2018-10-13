package My_package;

import java.util.*;

public class GenericSearch {
//    HashSet<State> set = new HashSet<>();
    TreeSet<State> set = new TreeSet<>();
    public Triple genericSearch(Problem p, Comparator<Node> c, int maxLevelForID){
        int time = 0;
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
                ArrayList<Operator> path = new ArrayList<>();
                Node cur = u;
                while(cur.parent != null){
                    path.add(cur.operator);
                    cur = cur.parent;
                }

                Operator[] ret = new Operator[path.size()];
                for (int i = 0, j = path.size()-1; i < ret.length; i++)
                    ret[i] = path.get(j--);
//                System.err.println(u.depth);
                return new Triple(ret, expandedNodes, u.cost);
            }

            expandedNodes++;
            for (Operator o: p.operators) {
                State next = p.stateSpace.computeNextState(u.state, o);
                Node nextNode = new Node(next, u, o, u.cost + p.pathCost.cost(o), u.depth+1);
                if(nextNode.state.equals(u.state))
                    continue;
                if(set.contains(nextNode.state))
                    continue;
                if(nextNode.depth == maxLevelForID)
                    continue;
                nextNode.insertionTime = ++time;
                nodes.add(nextNode);
                set.add(nextNode.state);
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
