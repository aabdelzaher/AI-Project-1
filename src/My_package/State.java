package My_package;

import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class State implements Comparable<State> {
    public int row;
    public int col;
    public int dragonGlass;   //the number of dragon glass carried by the player
    public ArrayList<Point> whiteWalkers;  //the positions of the white walkers remaining in the grid so far
    static int mod = BigInteger.probablePrime(30, new Random()).intValue();
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
