import java.util.*;

class State {
    char[] board;

    public State(char[] arr) {
        this.board = Arrays.copyOf(arr, arr.length);
    }

    public int getScore() {

        // TODO: return game theoretic value of the board
		int tiles1 = 0;
		int tiles2 = 0;
		
    	for (char c : board) {
    		if (c == '1') {
    			tiles1++;
    		} else if (c == '2') {
    			tiles2++;
    		}
    	}
    	
    	if (tiles1 == tiles2) {
    		return 0;
    	}
    	
    	return tiles1 > tiles2 ? 1: -1;
    }

    public boolean isTerminal() {

        // TODO: determine if the board is a terminal node or not and return boolean

        return (getSuccessors('1')[0].equals(getSuccessors('2')[0]));
    }

    public State[] getSuccessors(char player) {

        // TODO: get all successors and return them in proper order
        char[][] board = new char[4][4];
        for (int i = 0; i < 16; i++) {
            board[i / 4][i % 4] = getBoard().charAt(i);
        }

        List<State> successorsToBe = new ArrayList<State>();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {            	
                // Ignore non-empty tiles
                if (board[i][j] != '0') {
                    continue;
                }

                char[][] successor = new char[4][4];
                for (int h = 0; h < 16; h++) {
                    successor[h / 4][h % 4] = getBoard().charAt(h);
                }
                
                boolean alias = false;
                // Go through the neighboring tiles for a particular tile
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        // Ignore the current state
                        if (k == 0 && l == 0) {
                            continue;
                        }

                        // Index out-of-bounds
                        if (i + k < 0 || i + k > 3 || j + l < 0 || j + l > 3) {
                            continue;
                        }

                        // Check if current tile is player's, or is empty
                        if (board[i + k][j + l] == player || board[i + k][j + l] == '0') {
                            continue;
                        }

                        int m = 0;
                        int n = 0;
                        boolean flag = true;
                        while (board[i + k + m][j + l + n] != player) {
                            // Abandon this direction, go to the next
                            if (board[i + k + m][j + l + n] == '0') {
                                flag = false;
                                break;
                            }

                            // Continue in the same direction for the next iteration
                            m += k;
                            n += l;

                            // Index out-of-bounds
                            if (i + k + m < 0 || i + k + m > 3 || j + l + n < 0 || j + l + n > 3) {
                                flag = false;
                                break;
                            }
                        }

                        // at this point, if flag == true, then current state is valid successor state. Else abandon
                        if (!flag) {
                            continue;
                        }
                        
                        alias = true;
                        m = 0;
                        n = 0;
                        while (board[i + k + m][j + l + n] != player) {
                            successor[i + k + m][j + l + n] = player;

                            // Continue in the same direction for the next iteration
                            m += k;
                            n += l;
                        }
                    }
                }
                
                if (!alias) {
                	continue;
                }
                
                successor[i][j] = player;
                char[] succ = new char[16];
                for (int h = 0; h < 16; h++) {
                    succ[h] = successor[h / 4][h % 4];
                }
                successorsToBe.add(new State(succ));
            }
        }
        
        if (successorsToBe.size() == 0) {
        	successorsToBe.add(new State(this.board));
        }
        
        State[] successors = successorsToBe.toArray(new State[0]);
        return successors;
    }

    public void printState(int option, char player) {
    	
        // TODO: print a State based on option (flag)
    	if (option == 1) {
    		State[] successors = getSuccessors(player);
    		
    		for (State successor : successors) {
    			System.out.println(successor.getBoard());
    		}
    		
    		return;
    	}
    	
    	else if (option == 2) {
    		if (isTerminal()) {
    			System.out.println(getScore());
    		}
    		else {
    			System.out.println("non-terminal");
    		}
    	}

    	else if (option == 3) {
    		System.out.println(Minimax.run(this, player));
    		System.out.println(Minimax.getNumberOfCalls());
    	}
    	
    	else if (option == 4) {
    		State[] successors = getSuccessors(player);
    		char nextPlayer = player == '1' ? '2' : '1';
    		int desiredResult = player == '1' ? 1 : -1;
    		
    		if (isTerminal()) {
    			return;
    		}
    		
    		for (State successor : successors) {
    			int result = Minimax.run(successor, nextPlayer);
    			if (result == desiredResult) {
    				System.out.println(successor.getBoard());
    				return;
    			}
    		}
    		
    		System.out.println(successors[0].getBoard());
    	}
    	
    	else if (option == 5) {
    		System.out.println(Minimax.run_with_pruning(this, player));
    		System.out.println(Minimax.getNumberOfCalls());    		
    	}
    	
    	else if (option == 6) {
    		State[] successors = getSuccessors(player);
    		char nextPlayer = player == '1' ? '2' : '1';
    		int desiredResult = player == '1' ? 1 : -1;
    		
    		if (isTerminal()) {
    			return;
    		}
    		
    		for (State successor : successors) {
    			int result = Minimax.run_with_pruning(successor, nextPlayer);
    			if (result == desiredResult) {
    				System.out.println(successor.getBoard());
    				return;
    			}
    		}
    		
    		System.out.println(successors[0].getBoard());
    	}
    }
    
    public void printBoard() {
        String board = this.getBoard();

        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0)
                System.out.println("-----------------");

            if (board.charAt(i) != '0') {
                System.out.print("| " + board.charAt(i) + " ");
            } else {
                System.out.print("|   ");
            }

            if (i % 4 == 3)
                System.out.println("|");
        }
        System.out.println("-----------------");
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
    private static int maxCount = 0;
    private static int minCount = 0;

    private static int max_value(State curr_state) {
        Minimax.maxCount++;
        // TODO: implement Max-Value of the Minimax algorithm

        if (curr_state.isTerminal()) {
            return curr_state.getScore();
        }

        int alpha = Integer.MIN_VALUE;
        State[] successors = curr_state.getSuccessors('1');        
        for (State successor : successors) {
            alpha = Math.max(alpha, min_value(successor));
        }

        return alpha;
    }

    private static int min_value(State curr_state) {
        Minimax.minCount++;
        // TODO: implement Min-Value of the Minimax algorithm

        if (curr_state.isTerminal()) {
            return curr_state.getScore();
        }

        int beta = Integer.MAX_VALUE;
        State[] successors = curr_state.getSuccessors('2');
        for (State successor : successors) {
            beta = Math.min(beta, max_value(successor));
        }

        return beta;
    }

    private static int max_value_with_pruning(State curr_state, int alpha, int beta) {
        Minimax.maxCount++;
        // TODO: implement Max-Value of the alpha-beta pruning algorithm

        if (curr_state.isTerminal()) {
            return curr_state.getScore();
        }

        State[] successors = curr_state.getSuccessors('1');
        for (State successor : successors) {
            alpha = Math.max(alpha, min_value_with_pruning(successor, alpha, beta));
            if (alpha >= beta)
                return beta;
        }

        return alpha;
    }

    private static int min_value_with_pruning(State curr_state, int alpha, int beta) {
        Minimax.minCount++;
        // TODO: implement Min-Value of the alpha-beta pruning algorithm
        
        if (curr_state.isTerminal()) {
            return curr_state.getScore();
        }

        State[] successors = curr_state.getSuccessors('2');
        for (State successor : successors) {
            beta = Math.min(beta, max_value_with_pruning(successor, alpha, beta));
            if (alpha >= beta)
                return alpha;
        }

        return beta;
    }

    public static int run(State curr_state, char player) {

        // TODO: run the Minimax algorithm and return the game theoretic value
    	
        if (player == '1') {
            return max_value(curr_state);
        } else {
            return min_value(curr_state);
        }
    }

    public static int run_with_pruning(State curr_state, char player) {

        // TODO: run the alpha-beta pruning algorithm and return the game theoretic value
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        if (player == '1') {
            return max_value_with_pruning(curr_state, alpha, beta);
        } else {
            return min_value_with_pruning(curr_state, alpha, beta);
        }
    }
    
    public static int getNumberOfCalls() {
    	return Minimax.maxCount + Minimax.minCount;
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
