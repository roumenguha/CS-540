import java.util.*;

class State {
    char[] board;

    public State(char[] arr) {
        this.board = Arrays.copyOf(arr, arr.length);
    }

    public int getScore() {

        // TO DO: return game theoretic value of the board

        return score;
    }
    
    public boolean isTerminal() {
    	
        // TO DO: determine if the board is a terminal node or not and return boolean

        return is_terminal;
    }

    public State[] getSuccessors(char player) {

        // TO DO: get all successors and return them in proper order

        return successors;
    }
 
    public void printState(int option, char player) {

        // TO DO: print a State based on option (flag)

    }

    public String getBoard() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            builder.append(this.board[i]);
        }
        return builder.toString().trim();
    }

    public boolean equals(State src) {
        for (int i = 0; i < 16; i++) {
            if (this.board[i] != src.board[i])
                return false;
        }
        return true;
    }
}

class Minimax {
	private static int max_value(State curr_state) {
		
        // TO DO: implement Max-Value of the Minimax algorithm

	}
	
	private static int min_value(State curr_state) {
		
        // TO DO: implement Min-Value of the Minimax algorithm

	}
	
	private static int max_value_with_pruning(State curr_state, int alpha, int beta) {
	    
        // TO DO: implement Max-Value of the alpha-beta pruning algorithm

	}
	
	private static int min_value_with_pruning(State curr_state, int alpha, int beta) {
	    
        // TO DO: implement Min-Value of the alpha-beta pruning algorithm

	}
	
	public static int run(State curr_state, char player) {

        // TO DO: run the Minimax algorithm and return the game theoretic value

	}
	
	public static int run_with_pruning(State curr_state, char player) {
	    
        // TO DO: run the alpha-beta pruning algorithm and return the game theoretic value

	}
}

public class Reversi {
    public static void main(String args[]) {
        if (args.length != 3) {
            System.out.println("Invalid Number of Input Arguments");
            return;
        }
        int flag = Integer.valueOf(args[0]);
        char[] board = new char[16];
        for (int i = 0; i < 16; i++) {
            board[i] = args[2].charAt(i);
        }
        int option = flag / 100;
        char player = args[1].charAt(0);
        if ((player != '1' && player != '2') || args[1].length() != 1) {
            System.out.println("Invalid Player Input");
            return;
        }
        State init = new State(board);
        init.printState(option, player);
    }
}
