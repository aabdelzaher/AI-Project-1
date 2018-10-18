package My_package;

import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class State implements Comparable<State> {
    public int row; // Jon Snow row position
    public int col; // Jon Snow column position
    public int dragonGlass;   // The number of dragon glass carried by Jon Snow
    public ArrayList<Point> whiteWalkers;  // The positions of the white walkers remaining in the grid so far

    public State(int x, int y, int DG, ArrayList<Point> WW){
        this.row = x;
        this.col = y;
        this.dragonGlass = DG;
        this.whiteWalkers = WW;
    }

    public boolean equals(Object o){
        State s2 = (State)o;
        return s2.row == row && s2.col == col && s2.dragonGlass == dragonGlass && s2.whiteWalkers.equals(whiteWalkers);
    }

    /**
     * Hashing the current state to be used in saving the state in a Hashset so no repeated states is visited
     */

    static int mod = BigInteger.probablePrime(30, new Random()).intValue(); // This is used for hashing
    @Override
    public int hashCode() {
        long hash = row*31 + this.col*31*31 % mod + dragonGlass*31*31*31;
        hash %= mod;
        int pow = 31*31*31*31;
        for (Point p: whiteWalkers) {
            int h = p.x*100 + p.y;
            hash += 1l*h*pow % mod;
            hash %= mod;
            pow = (int)((1l*pow*31)%mod);
        }

        return (int)(hash%mod);
    }

    /**
     * compareTo for comparing between two states so states can be saved in treeset so no repeated states is visited.
     * This is similar to the hashing, It is only implemented as in case of large number of states this is better as
     * the larger the number of states the more collision happens in the hashing
     */
    @Override
    public int compareTo(State state) {
        if(row != state.row)
            return row - state.row;
        if(col != state.col)
            return col - state.col;
        if(dragonGlass != state.dragonGlass)
            return dragonGlass - state.dragonGlass;
        if(whiteWalkers.size() != state.whiteWalkers.size())
            return whiteWalkers.size() - state.whiteWalkers.size();
        for (int i = 0; i < whiteWalkers.size(); i++) {
            if(whiteWalkers.get(i).x != state.whiteWalkers.get(i).x)
                return whiteWalkers.get(i).x - state.whiteWalkers.get(i).x;
            if(whiteWalkers.get(i).y != state.whiteWalkers.get(i).y)
                return whiteWalkers.get(i).y - state.whiteWalkers.get(i).y;

        }

        return 0;
    }
}
