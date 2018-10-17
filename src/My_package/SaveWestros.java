package My_package;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SaveWestros extends Problem implements GoalTest, StateSpace, PathCost {


    static final int BFS = 0, DFS = 1, UniformCost = 2, IterativeDeepening = 3, Greedy1 = 4, Greedy2 = 5, AStar1 = 6, AStar2 = 7;
    int MaxM = 10;
    int MaxN = 10;
    int MaxGlass = 5;
    int MaxWhiteWalkers = 12;
    int MaxObstacles = 10;
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
        int cnt = 0;
//        for (int k = 0; k < 1000; k++) {
//            s.genGrid();
//            GenericSearch.Triple resutl1 = s.search(s.grid, UniformCost, false);
//            GenericSearch.Triple resutl2 = s.search(s.grid, AStar, false);
//            if(resutl1 != null && resutl2 != null && resutl1.expandedNodes < resutl2.expandedNodes) {
//                cnt++;
//                System.err.println(Arrays.deepToString(s.grid));
//                return;
//            }
//        }
//        System.out.println(cnt);
        for (int i = 0; i <= AStar2; i++) {
            if(i == IterativeDeepening)
                continue;
            if(i != BFS)
            System.out.println(get(i) + ": ");
            s.search(s.grid, i, false);
            System.out.println("============================");
        }
        System.out.println(get(IterativeDeepening)+": ");
        s.search(s.grid, IterativeDeepening, false);
        System.out.println("============================");
//        s.search(s.grid, AStar1, true);

    }

    GenericSearch.Triple search(char[][] grid, int strategy, boolean visualize){
        Operator[] operators = {Operator.UP, Operator.DOWN, Operator.RIGHT, Operator.LEFT, Operator.KILL, Operator.PICK};
        ArrayList<Point> WW = new ArrayList<>();
        SaveWestros s = new SaveWestros(operators, null, null, null, null);
        s.grid = grid;
        s.stateSpace = s;
        s.pathCost = s;
        s.goalTest = s;

        /**
         * [[O, W, W], [W, ., D], [., ., .]]
         */

        /**
         * [[D, ., ., .], [., W, ., W], [., ., O, O], [., ., ., .]]
         */

        /**
         * ....
         * ...D
         * ....
         * WWWJ
         */


        /**
         * D.OW.
         * ...O.
         * ...W.
         * ....W
         * W....
         * WO..J
         */

//        s.grid = new char[][]{"OWW".toCharArray(), "W.D".toCharArray(), "...".toCharArray()};

        for (int i = 0; i < s.grid.length; i++) {
            for (int j = 0; j < s.grid[i].length; j++) {
                if(s.grid[i][j] == 'W'){
                    WW.add(new Point(i, j));
                }
            }
        }


        State initialState = new State(s.grid.length-1, s.grid[0].length-1, 0, WW);
        s.initialState = initialState;

        if(strategy == BFS)print(s.grid, s.initialState);

        GenericSearch.Triple result = null;
        Operator[] path = null;
        switch(strategy){
            case BFS:
                System.out.println();
                System.out.println("BFS:");
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return a.insertionTime-b.insertionTime;
                    }
                }, -1);
                break;
            case DFS:
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return b.insertionTime-a.insertionTime;
                    }
                }, -1);
                break;
            case UniformCost:
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return a.cost-b.cost;
                    }
                }, -1);
                break;
            case IterativeDeepening:
                for (int i = 1;; i++) {
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
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return getHeuristic1(a)- getHeuristic1(b);
                    }
                }, -1);
                break;

            case Greedy2:
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return getHeuristic2(a)- getHeuristic2(b);
                    }
                }, -1);
                break;

            case AStar1:
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return (a.cost+ getHeuristic1(a))-(b.cost+ getHeuristic1(b));
                    }
                }, -1);
                break;
            case AStar2:
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return (a.cost+ getHeuristic2(a))-(b.cost+ getHeuristic2(b));
                    }
                }, -1);
                break;

        }

        if(result != null) {
            path = result.path;
            System.out.println("Expanded Nodes: " + result.expandedNodes);
            System.out.println("Cost: " + result.cost);
            System.out.println("Path: " + Arrays.toString(path));
            if(visualize)
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
        int n = (int) (Math.random() * (MaxN - 3) + 3);
        int m = (int) (Math.random() * (MaxM - 3) + 3);
        int ww = (int) (Math.random() * (MaxWhiteWalkers - 2) + 3);
        int o = (int) (Math.random() * (MaxObstacles - 2) + 3);
        int dragonStoneR, dragonStoneC;
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
        for (int i = 0; i < ww; i++) {
            int r = (int) (Math.random() * n);
            int c = (int) (Math.random() * m);
            if (grid[r][c] == '.')
                grid[r][c] = 'W';
        }

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
                for (int k = 0; k < dx.length; k++) {
                    int new_X = s.row + dx[k];
                    int new_Y = s.col + dy[k];

                    if (valid(new_X, new_Y) && grid[new_X][new_Y] == 'W') {
                        if (new_whiteWalkers.contains(new Point(new_X, new_Y))) {
                            new_whiteWalkers.remove(new Point(new_X, new_Y));
                            killed = true;
                        }
                    }
                }
                nextState.whiteWalkers = new_whiteWalkers;
                if (killed)
                    nextState.dragonGlass--;
                break;
            case PICK:
                if (s.dragonGlass < MaxGlass && grid[s.row][s.col] == 'D')
                    nextState.dragonGlass += 1;
                break;
            case UP:
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

    boolean valid(int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[x].length && grid[x][y] != 'O';
    }

    boolean validMove(int x, int y, ArrayList<Point> ww){
        return valid(x, y) && (grid[x][y] == '.' || grid[x][y] == 'D' || (grid[x][y] == 'W' && !ww.contains(new Point(x, y))));
    }

    /**
     * Method returns the cost of applying some operator 'o'.
     * In this problem the cost is not dependant on the state; therefore, the state is not passed as a parameter.
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

    static void print(char[][] g){
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].length; j++) {
                System.out.print("[ " + g[i][j] + " ]");
            }
            System.out.println();
        }
    }

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

    void print(Operator[] path, State s){
        State cur = s;
        for (int i = 0; i < path.length; i++) {
            System.out.println("=================");
            cur = computeNextState(cur, path[i]);
            print(grid, cur);
        }
    }
}
