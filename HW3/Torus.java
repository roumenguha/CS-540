///////////////////////////////////////////////////////////////////////////////
// Title:            Sliding 8-puzzle
// Semester:         CS 540 Spring 2018
//
// Author:           Roumen Guha
// Email:            guha4@wisc.edu
// CS Login:         roumen
// Worked with:      Dan Feng Xiao

import java.util.*;

class State {
    int[] board;
    State parentPt;
    int depth;

    public State(int[] arr) {
        this.board = Arrays.copyOf(arr, arr.length);
        this.parentPt = null;
        this.depth = 0;
    }

    public State[] getSuccessors() {
        State[] successors = new State[4];

        // TODO: get all four successors and return them in sorted order
        // If we view the 9 numbers
        // as a 9-digit integer, then there is a natural order among states. Whenever you push successors into
        // the stack, push them from small 9-digit to large 9-digit. This means that when we later pop them out,
        // the largest 9-digit successor will be goal-checked before the other successors. This order will be used
        // throughout this program, so that the output is well-defined.

        // Locate the empty tile
        int emptyTilePos = -1;
        for (int i = 0; i < board.length; i++) {
            if (this.board[i] == 0) {
                emptyTilePos = i;
                break;
            }
        }

        // Ending indices of emptyTilePos for next successor states
        int[] successorTiles = new int[4];
        successorTiles[0] = (emptyTilePos + 6) % 9; // Tile above
        successorTiles[1] = (emptyTilePos - (emptyTilePos % 3)) + ((emptyTilePos + 1) % 3); // Tile right
        successorTiles[2] = (emptyTilePos - (emptyTilePos % 3)) + ((emptyTilePos + 2) % 3); // Tile left
        successorTiles[3] = (emptyTilePos + 3) % 9; // Tile below

        // Generate successorStates
        for (int i = 0; i < successorTiles.length; i++) {
            State currState = new State(board);
            currState.board[emptyTilePos] = currState.board[successorTiles[i]];
            currState.board[successorTiles[i]] = 0;

            currState.parentPt = this;
            currState.depth = this.depth + 1;

            // Sort states in ascending order
            int j = 0;
            for (j = 0; j < i; j++) {
                if (currState.isLessThan(successors[j].board)) {
                    // Shift states appropriately
                    for (int k = i; k > j; k--) {
                        successors[k] = successors[k-1];
                    }
                    break;
                }
            }
            successors[j] = currState;
        }

        return successors;
    }

    private boolean isLessThan(int[] src) {
        for (int i = 0; i < 9; i++) {
            if (this.board[i] < src[i]) {
                return true;
            } else if (this.board[i] > src[i]) {
                return false;
            }
        }
        return false;
    }

    public void printState(int option) {
        String p = this.getBoard();

        // TODO: print a torus State based on option (flag)
        if (option == 3)
            p += " parent " + this.parentPt.getBoard();

        System.out.println(p);
    }

    public String getBoard() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            builder.append(this.board[i]).append(" ");
        }
        return builder.toString().trim();
    }

    public boolean isGoalState() {
        for (int i = 0; i < 9; i++) {
            if (this.board[i] != (i + 1) % 9)
                return false;
        }
        return true;
    }

    public boolean equals(State src) {
        for (int i = 0; i < 9; i++) {
            if (this.board[i] != src.board[i])
                return false;
        }
        return true;
    }
}

public class Torus {

    public static void main(String args[]) {
        if (args.length < 10) {
            System.out.println("Invalid Input");
            return;
        }

        int flag = Integer.valueOf(args[0]);
        int[] board = new int[9];

        for (int i = 0; i < 9; i++) {
            board[i] = Integer.valueOf(args[i + 1]);
        }

        int option = flag / 100;
        int cutoff = flag % 100;

        if (option == 5) {
            cutoff = 0;
        }

        if (option == 1) {
            State init = new State(board);
            State[] successors = init.getSuccessors();

            for (State successor : successors) {
                successor.printState(option);
            }

        } else {
            State init = new State(board);
            Stack<State> stack = new Stack<>();
            List<State> prefix = new ArrayList<>();
            int goalChecked = 0;
            int maxStackSize = Integer.MIN_VALUE;
            boolean goalFound = false;

            init.parentPt = new State(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0});

            while (true) {
                stack.push(init);

                while (!stack.isEmpty()) {
                    //TODO: perform iterative deepening; implement prefix list
                    State s = stack.pop();

                    if (option < 4) {
                        s.printState(option);
                    }

                    for (int i = 0; i < prefix.size(); i++) {

                        if (s.parentPt.equals(prefix.get(i))) {
                            // Remove everything after p, then add s
                            while (prefix.size() > (i + 1)) {
                                prefix.remove(i + 1);
                            }

                            break;
                        }
                    }

                    prefix.add(s);

                    goalChecked++;
                    if (s.isGoalState()) {
                        goalFound = true;
                        break;
                    }

                    if (option == 4 && s.depth == cutoff) {
                        for (int i = 0; i < prefix.size(); i++) {
                            prefix.get(i).printState(option);
                        }

                        break;
                    }

                    if (s.depth < cutoff) {
                        State[] successors = s.getSuccessors();
                        for (int i = 0; i < successors.length; i++) {

                            // Check first if the current successor has been visited (i.e. is in the prefix list)
                            boolean present = false;
                            for (int j = 0; j < prefix.size(); j++) {
                                if (successors[i].equals(prefix.get(j))) {
                                    present = true;
                                }
                            }

                            // If not present, go ahead and add it. Also remember to correct the maximum stack size we've seen
                            if (!present) {
                                stack.push(successors[i]);
                                if (stack.size() > maxStackSize) {
                                    maxStackSize = stack.size();
                                }
                            }
                        }
                    }
                }

                //TODO: (e) perform the necessary steps to start a new iteration
                if (option != 5) {
                    break;
                }

                cutoff++;

                if (goalFound) {
                    for (int i = 0; i < prefix.size(); i++) {
                        prefix.get(i).printState(option);
                    }

                    System.out.println("Goal-check " + goalChecked);
                    System.out.println("Max-stack-size " + maxStackSize);

                    break;
                }

                stack.clear();
                prefix.clear();
            }
        }
    }
}
