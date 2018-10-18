package My_package;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SaveWestros extends Problem implements GoalTest, StateSpace, PathCost {


    static final int BFS = 0, DFS = 1, UniformCost = 2, IterativeDeepening = 3, Greedy1 = 4, Greedy2 = 5, AStar1 = 6, AStar2 = 7;
    int MaxM = 10;  // This controls the maximum possible height of the grid
    int MaxN = 10;  // This controls the maximum possible width of the grid
    int MaxGlass = 5;   // The maximum number of dragon glass that Jon can hold at a time
    int MaxWhiteWalkers = 12;   // This controls the maximum possible number of white walkers in the grid
    int MaxObstacles = 10;  // This controls the maximum possible number of obstacles in the grid
    char[][] grid;


    public SaveWestros(){}

    public SaveWestros(Operator[] o, State s, StateSpace ss, GoalTest gt, PathCost pc) {
        this.operators = o;
        this.goalTest = gt;
        this.pathCost = pc;
        this.initialState = s;
        this.stateSpace = ss;
    }


    public static void main(String[] args) {
        SaveWestros s = new SaveWestros();
        s.genGrid();

        // Running the search algorithm for the same grid using all different strategies
        for (int i = 0; i <= AStar2; i++) {
            if(i != BFS)
            System.out.println(get(i) + ": ");
            s.search(s.grid, i, false);
            System.out.println("============================");
        }

    }

    GenericSearch.Triple search(char[][] grid, int strategy, boolean visualize){
        Operator[] operators = {Operator.UP, Operator.DOWN, Operator.RIGHT, Operator.LEFT, Operator.KILL, Operator.PICK};
        ArrayList<Point> WW = new ArrayList<>();
        SaveWestros s = new SaveWestros(operators, null, null, null, null);
        s.grid = grid;
        // The state space, pathCost, goalTest are implemented as interfaces. SaveWestros implements all three interfaces
        // Therefore, the 'SaveWestros' instance is set as the StateSpace, PathCost and GoalTest
        s.stateSpace = s;
        s.pathCost = s;
        s.goalTest = s;


        // Calculating positions of white walkers as it is used in the initial state
        for (int i = 0; i < s.grid.length; i++) {
            for (int j = 0; j < s.grid[i].length; j++) {
                if(s.grid[i][j] == 'W'){
                    WW.add(new Point(i, j));
                }
            }
        }


        State initialState = new State(s.grid.length-1, s.grid[0].length-1, 0, WW);
        s.initialState = initialState;

        // Printing the initial grid
        print(s.grid, s.initialState);

        GenericSearch.Triple result = null;
        Operator[] path = null;
        // According to the strategy the generic search is called with a different comparator (queueing function)
        switch(strategy){
            case BFS:
                // The earlier the node is inserted, the higher it priority
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return a.insertionTime-b.insertionTime;
                    }
                }, -1);
                break;
            case DFS:
                // The later the node is inserted, the higher it priority
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return b.insertionTime-a.insertionTime;
                    }
                }, -1);
                break;
            case UniformCost:
                // The lower the node cost, the higher it priority
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return a.cost-b.cost;
                    }
                }, -1);
                break;
            case IterativeDeepening:
                // Calculating maximum number of distinct states as calling dfs with higher level
                // than the maximum number of state (deepest level) is not needed.
                int maxNodes = grid.length*grid[0].length*(1<<WW.size())*(MaxGlass+1);
                for (int i = 1; i < maxNodes+10; i++) {
                    // Same comparator as DFS
                    result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                        @Override
                        public int compare(Node a, Node b) {
                            return b.insertionTime-a.insertionTime;
                        }
                    }, i);
                    if(result != null)
                        break;
                }
                break;
            case Greedy1:
                // Comparing by the heuristic function 1
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return getHeuristic1(a)- getHeuristic1(b);
                    }
                }, -1);
                break;

            case Greedy2:
                // Comparing by the heuristic function 2
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return getHeuristic2(a)- getHeuristic2(b);
                    }
                }, -1);
                break;

            case AStar1:
                // Comparing by the heuristic function 1 plus the cost
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        // in case of having similar priorities (getHeuristic + cost) the nodes are sorted by their hashcode
                        // This is just some sort of order as sorting alphabetically in the lecture
                        if((a.cost+ getHeuristic1(a)) == (b.cost+ getHeuristic1(b)))
                            return a.hashCode()-b.hashCode();
                        return (a.cost+ getHeuristic1(a))-(b.cost+ getHeuristic1(b));
                    }
                }, -1);
                break;
            case AStar2:
                // Comparing by the heuristic function 2 plus the cost
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        // in case of having similar priorities (getHeuristic + cost) the nodes are sorted by their hashcode
                        // This is just some sort of order as sorting alphabetically in the lecture
                        if((a.cost+ getHeuristic2(a)) == (b.cost+ getHeuristic2(b)))
                            return a.hashCode() - b.hashCode();
                        return (a.cost+ getHeuristic2(a))-(b.cost+ getHeuristic2(b));
                    }
                }, -1);
                break;

        }

        if(result != null) {    // the solution exists
            path = result.path;
            System.out.println("Expanded Nodes: " + result.expandedNodes);
            System.out.println("Cost: " + result.cost);
            System.out.println("Path: " + Arrays.toString(path));
            if(visualize)   // printing the grid after each step to the solution
                s.print(path, s.initialState);
        }else{
            System.out.println("No Solution");
        }

        return result;
    }

    /**
     * The heurestic functions used in A* and Greedy search techniques
     */
    int getHeuristic1(Node n){
        if(new SaveWestros().test(n.state))
            return 0;

        return ((n.state.whiteWalkers.size() + 2) / 3);
    }

    int getHeuristic2(Node n){
        if(new SaveWestros().test(n.state))
            return 0;
        int ans = 0;
        boolean[][] vis = new boolean[grid.length][grid[0].length];
        ArrayList<Point> WhiteWalkers = new ArrayList<>(n.state.whiteWalkers);
        for (int i = 0; i < WhiteWalkers.size(); i++) {
            Point pos = WhiteWalkers.get(i);
            int[] movesX = {1, 1, 2, 0, 0, -1, -1, -2};
            int[] movesY = {1, -1, 0, 2, -2, 1, -1, 0};
            if(!vis[pos.x][pos.y]){
                vis[pos.x][pos.y] = true;
                ans += ((dfs(pos.x, pos.y, vis, movesX, movesY)+2) / 3);
            }
        }
        return ans;
    }

    int dfs(int x, int y, boolean[][] vis, int[] dx, int[] dy){
        int ans = 1;
        for (int k = 0; k < dx.length; k++) {
            int newX = x+dx[k];
            int newY = y+dy[k];

            if(newX >= 0 && newY >= 0 && newX < grid.length && newY < grid[0].length && grid[newX][newY] == 'W' && !vis[newX][newY]){
                vis[newX][newY] = true;
                ans += dfs(newX, newY, vis, dx, dy);
            }
        }

        return ans;
    }


    /**
     * Generating a random grid
     */
    void genGrid() {
        int n = (int) (Math.random() * (MaxN - 3) + 3); // height
        int m = (int) (Math.random() * (MaxM - 3) + 3); // width
        int ww = (int) (Math.random() * (MaxWhiteWalkers - 2) + 3); // number of white walkers
        int o = (int) (Math.random() * (MaxObstacles - 2) + 3); // number of obstacles
        int dragonStoneR, dragonStoneC; // position of dragonstone
        while (true) {
            dragonStoneR = (int) (Math.random() * n);
            dragonStoneC = (int) (Math.random() * m);
            if (dragonStoneC != m - 1 || dragonStoneR != n - 1)
                break;
        }

        grid = new char[n][m];
        // . -> empty
        // D -> Dragon Stone
        // J -> John Snow
        // W -> White Walker
        // O -> obstacle

        for (int i = 0; i < grid.length; i++)
            Arrays.fill(grid[i], '.');

        grid[n - 1][m - 1] = 'J';
        grid[dragonStoneR][dragonStoneC] = 'D';
        // placing the white walkers
        for (int i = 0; i < ww; i++) {
            int r = (int) (Math.random() * n);
            int c = (int) (Math.random() * m);
            if (grid[r][c] == '.')
                grid[r][c] = 'W';
        }

        // placing the obstacles
        for (int i = 0; i < o; i++) {
            int r = (int) (Math.random() * n);
            int c = (int) (Math.random() * m);
            if (grid[r][c] == '.')
                grid[r][c] = 'O';
        }
        grid[n - 1][m - 1] = '.';
    }

    /**
     * Checking if the state 's' is a goal state
     */
    public boolean test(State s) {
        return s.whiteWalkers.isEmpty();
    }

    /**
     * Computing the next state that results from applying an operator 'o' on some state 's'.
     */
    int[] dx = {0, 0, 1, -1};
    int[] dy = {-1, 1, 0, 0};

    @Override
    public State computeNextState(State s, Operator o) {
        State nextState = new State(s.row, s.col, s.dragonGlass, s.whiteWalkers);
        switch (o) {
            case KILL:
                if(s.dragonGlass == 0)
                    break;
                ArrayList<Point> new_whiteWalkers = new ArrayList<>(s.whiteWalkers);
                boolean killed = false;
                // looping over all adjacent cells and check for white walkers
                for (int k = 0; k < dx.length; k++) {
                    int new_X = s.row + dx[k];
                    int new_Y = s.col + dy[k];

                    if (notObstacle(new_X, new_Y) && grid[new_X][new_Y] == 'W') {
                        if (new_whiteWalkers.contains(new Point(new_X, new_Y))) {
                            new_whiteWalkers.remove(new Point(new_X, new_Y));
                            killed = true;
                        }
                    }
                }
                nextState.whiteWalkers = new_whiteWalkers;
                if (killed) // the dragonglass is used
                    nextState.dragonGlass--;
                break;
            case PICK:
                if (s.dragonGlass < MaxGlass && grid[s.row][s.col] == 'D')
                    nextState.dragonGlass += 1;
                break;
            case UP:    // before each move the cell to which Jon will move is checked to be notObstacle
                if (s.row > 0 && validMove(s.row-1, s.col, s.whiteWalkers))
                    nextState.row -= 1;
                break;
            case DOWN:
                if (s.row < (grid.length - 1) && validMove(s.row+1, s.col, s.whiteWalkers))
                    nextState.row += 1;
                break;
            case RIGHT:
                if (s.col < (grid[s.row].length - 1) && validMove(s.row, s.col+1, s.whiteWalkers))
                    nextState.col += 1;
                break;
            case LEFT:
                if (s.col > 0 && validMove(s.row, s.col-1, s.whiteWalkers))
                    nextState.col -= 1;
                break;
        }
        return nextState;
    }

    // The cell is in the grid and is not an obstacle
    boolean notObstacle(int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[x].length && grid[x][y] != 'O';
    }

    // The grid is empty or the place of dragonstone
    boolean validMove(int x, int y, ArrayList<Point> ww){
        return notObstacle(x, y) && (grid[x][y] == '.' || grid[x][y] == 'D' || (grid[x][y] == 'W' && !ww.contains(new Point(x, y))));
    }

    /**
     * Method returns the cost of applying some operator 'o'.
     * In this problem the cost is not dependant on the state; therefore, the state is not passed as a parameter.
     * All operators have 0 cost excpet the kill.
     */
    @Override
    public int cost(Operator o) {
        switch (o) {
            case KILL:
                return 1;
            case PICK:
                return 0;
            case UP:
                return 0;
            case DOWN:
                return 0;
            case RIGHT:
                return 0;
            case LEFT:
                return 0;
        }
        return 0;
    }


    /**
     * Methods responsible for printing the grid in a certain state
     */

    // printing a 2d-grid
    static void print(char[][] g){
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].length; j++) {
                System.out.print("[ " + g[i][j] + " ]");
            }
            System.out.println();
        }
    }

    // returning a string representing the strategy
    static String get(int i){
        switch (i){
            case DFS: return "DFS";
            case BFS: return "BFS";
            case AStar1: return "A Star 1";
            case AStar2: return "A Star 2";
            case IterativeDeepening: return "Iterative Deepening";
            case UniformCost: return "Uniform cost";
            case Greedy1: return "Greedy1";
            case Greedy2: return "Greedy2";
        }
        return "Unknown";
    }

    // Updating the grid g with the current state (some white walkers are dead for instance) then prints the updated grid
    static void print(char[][] g, State s){
        ArrayList<Point> ww = s.whiteWalkers;
        int jonRow = s.row;
        int jonCol = s.col;

        char[][] ret = new char[g.length][];
        for (int i = 0; i < g.length; i++)
            ret[i] = g[i].clone();

        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[i].length; j++) {
                if(ret[i][j] == 'W')
                    ret[i][j] = '.';
            }
        }

        for(Point p: ww)
            ret[p.x][p.y] = 'W';

        ret[jonRow][jonCol] = 'J';

        print(ret);

    }

    // Takes a path and prints the grid after each step of the path
    void print(Operator[] path, State s){
        State cur = s;
        for (int i = 0; i < path.length; i++) {
            System.out.println("=================");
            cur = computeNextState(cur, path[i]);
            print(grid, cur);
        }
    }
}
