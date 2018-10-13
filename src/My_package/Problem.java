package My_package;

public abstract class Problem {
    Operator[] operators;
    State initialState;
    GoalTest goalTest;
    StateSpace stateSpace;
    PathCost pathCost;
}
