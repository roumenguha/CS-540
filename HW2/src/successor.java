import java.util.*;

public class successor {
    public static class JugState {
        int[] Capacity = new int[]{0,0,0};
        int[] Content = new int[]{0,0,0};

        public JugState()
        {
        }
        public JugState(int A,int B, int C)
        {
            this.Capacity[0] = A;
            this.Capacity[1] = B;
            this.Capacity[2] = C;
        }
        public JugState(int A,int B, int C, int a, int b, int c)
        {
            this.Capacity[0] = A;
            this.Capacity[1] = B;
            this.Capacity[2] = C;
            this.Content[0] = a;
            this.Content[1] = b;
            this.Content[2] = c;
        }
        public JugState(JugState copyFrom)
        {
            this.Capacity[0] = copyFrom.Capacity[0];
            this.Capacity[1] = copyFrom.Capacity[1];
            this.Capacity[2] = copyFrom.Capacity[2];
            this.Content[0] = copyFrom.Content[0];
            this.Content[1] = copyFrom.Content[1];
            this.Content[2] = copyFrom.Content[2];
        }
 
        public void printContent()
        {
            System.out.println(this.Content[0] + " " + this.Content[1] + " " + this.Content[2]);
        }
 
        public ArrayList<JugState> getNextStates(){
            ArrayList<JugState> successors = new ArrayList<>();

            // TODO: add all successors to the list
            // For each jug
            for (int i = 0; i < Content.length; i++) {
                // First, fill a jug completely
                if (Content[i] < Capacity[i]) {
                    JugState a = new JugState(this);
                    a.Content[i] = a.Capacity[i];
                    successors.add(a);
                }

                // Second, empty a jug completely
                if (Content[i] > 0) {
                    JugState b = new JugState(this);
                    b.Content[i] = 0;
                    successors.add(b);
                }

                // Last, pour from one jug to another
                if (Content[i] > 0) {
                    // For each receiver jug
                    for (int j = 0; j < Content.length; j++) {
                        if (i == j)
                            continue;

                        JugState c = new JugState(this);
                        if (Content[j] < Capacity[j]) {
                            int pour = Math.min(Content[i], Capacity[j] - Content[j]); // until the former is empty or the latter is full
                            c.Content[i] -= pour;
                            c.Content[j] += pour;
                            successors.add(c);
                        }
                    }
                }
            }

            return successors;
        }
    }

    public static void main(String[] args) {
        if (args.length != 6)
        {
            System.out.println("Usage: java successor [A] [B] [C] [a] [b] [c]");
            return;
        }

        // parse command line arguments
        JugState a = new JugState();
        a.Capacity[0] = Integer.parseInt(args[0]);
        a.Capacity[1] = Integer.parseInt(args[1]);
        a.Capacity[2] = Integer.parseInt(args[2]);
        a.Content[0] = Integer.parseInt(args[3]);
        a.Content[1] = Integer.parseInt(args[4]);
        a.Content[2] = Integer.parseInt(args[5]);

        // Implement this function
        ArrayList<JugState> asist = a.getNextStates();

        // Print out generated successors
        for (int i = 0; i < asist.size(); i++)
        {
            asist.get(i).printContent();
        }

        return;
    }
}

