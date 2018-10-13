package My_package;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SaveWestros extends Problem implements GoalTest, StateSpace, PathCost {


    static final int BFS = 0, DFS = 1, UniformCost = 2, IterativeDeepening = 3, Greedy = 4, AStar = 5;
    int MaxM = 4;
    int MaxN = 4;
    int MaxGlass = 5;
    int MaxWhiteWalkers = 4;
    int MaxObstacles = 0;
    char[][] grid;


    static void print(char[][] g){
        for (int i = 0; i < g.length; i++) {
            System.out.println(g[i]);
        }
    }
    public static void main(String[] args) {
        SaveWestros s = new SaveWestros();
        s.genGrid();
        int cnt = 0;
        for (int k = 0; k < 1000; k++) {
            s.genGrid();
            GenericSearch.Triple resutl1 = s.search(s.grid, UniformCost, false);
            GenericSearch.Triple resutl2 = s.search(s.grid, AStar, false);
            if(resutl1 != null && resutl2 != null && resutl1.expandedNodes < resutl2.expandedNodes) {
                cnt++;
                System.err.println(Arrays.deepToString(s.grid));
                return;
            }
        }
        System.out.println(cnt);
    }

    static String get(int i){
        switch (i){
            case DFS: return "DFS";
            case BFS: return "BFS";
            case AStar: return "A Star";
            case IterativeDeepening: return "Iterative Deepening";
            case UniformCost: return "Uniform cost";
            case Greedy: return "Greedy";
        }
        return "Unknown";
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
//        s.grid = new char[][]{"..WW".toCharArray(), "..D.".toCharArray(), "...W".toCharArray(), "W...".toCharArray()};

        for (int i = 0; i < s.grid.length; i++) {
            for (int j = 0; j < s.grid[i].length; j++) {
                if(s.grid[i][j] == 'W'){
                    WW.add(new Point(i, j));
                }
            }
        }


        State initialState = new State(s.grid.length-1, s.grid[0].length-1, 0, WW);
        s.initialState = initialState;

        print(s.grid, s.initialState);

        GenericSearch.Triple result = null;
        Operator[] path = null;
        switch(strategy){
            case BFS:
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
            case Greedy:
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return a.getHeurestic1()-b.getHeurestic1();
                    }
                }, -1);
                break;
            case AStar:
                result = new GenericSearch().genericSearch(s, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return (a.cost+a.getHeurestic1())-(b.cost+b.getHeurestic1());
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

    void genGrid() {
        int n = (int) (Math.random() * (MaxN - 3) + 4);
        int m = (int) (Math.random() * (MaxM - 3) + 4);
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

    public SaveWestros(){

    }

    public SaveWestros(Operator[] o, State s, StateSpace ss, GoalTest gt, PathCost pc) {
        this.operators = o;
        this.goalTest = gt;
        this.pathCost = pc;
        this.initialState = s;
        this.stateSpace = ss;
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

    public boolean test(State s) {
        return s.whiteWalkers.isEmpty();
    }

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

    @Override
    public int cost(Operator o) {
        switch (o) {
            case KILL:
                return 1;
            case PICK:
                return 50;
            case UP:
                return 10;
            case DOWN:
                return 10;
            case RIGHT:
                return 10;
            case LEFT:
                return 10;
        }
        return 0;
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
