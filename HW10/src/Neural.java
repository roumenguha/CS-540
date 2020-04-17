import java.util.*;
import java.io.*;

public class Neural {
    private static String eval = "./hw2_midterm_A_eval.txt";
    private static String test = "./hw2_midterm_A_test.txt";
    private static String train = "./hw2_midterm_A_train.txt";

    private static double[][] evalData;
    private static double[][] testData;
    private static double[][] trainData;

    private static double[] u = new double[3];
    private static double[] v = new double[3];
    private static double[] w = new double[9]; // weights
    private static double[] x = new double[2]; // inputs
    private static double y = 0; // true label
    private static double eta = 0; // step size
    private static int T = 0; // epochs

    public static void main(String args[]) {
        evalData = readData(eval);
        testData = readData(test);
        trainData = readData(train);

        int flag = Integer.parseInt(args[0]);

        if (flag == 100) {
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            x[0] = Double.parseDouble(args[10]);
            x[1] = Double.parseDouble(args[11]);

            setNodes();

            for (int i = 0; i < u.length; i++) {
                System.out.printf("%.5f %.5f", u[i], v[i]);
                if (i < u.length - 1)
                    System.out.print(" ");
            }
            System.out.println();
        }

        else if (flag == 200) {
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            x[0] = Double.parseDouble(args[10]);
            x[1] = Double.parseDouble(args[11]);
            y = Double.parseDouble(args[12]);

            setNodes();

            System.out.printf("%.5f %.5f %.5f\n", E(), dE_dv_C(), dE_du_C());
        }

        else if (flag == 300) {
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            x[0] = Double.parseDouble(args[10]);
            x[1] = Double.parseDouble(args[11]);
            y = Double.parseDouble(args[12]);

            setNodes();

            System.out.printf("%.5f %.5f %.5f %.5f\n", dE_dv_A(), dE_du_A(), dE_dv_B(), dE_du_B());
        }

        else if (flag == 400) {
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            x[0] = Double.parseDouble(args[10]);
            x[1] = Double.parseDouble(args[11]);
            y = Double.parseDouble(args[12]);

            setNodes();

            double[] dE_dw = dE_dw();

            System.out.printf("%.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f\n", dE_dw[0], dE_dw[1], dE_dw[2], dE_dw[3], dE_dw[4], dE_dw[5], dE_dw[6], dE_dw[7], dE_dw[8]);
        }

        else if (flag == 500) {
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            x[0] = Double.parseDouble(args[10]);
            x[1] = Double.parseDouble(args[11]);
            y = Double.parseDouble(args[12]);
            eta = Double.parseDouble(args[13]);

            setNodes();

            System.out.printf("%.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f", w[0], w[1], w[2], w[3], w[4], w[5], w[6], w[7], w[8]);
            System.out.println("\n" + String.format("%.5f", E()));

            SGD();

            System.out.printf("%.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f", w[0], w[1], w[2], w[3], w[4], w[5], w[6], w[7], w[8]);
            System.out.println("\n" + String.format("%.5f", E()));
        }

        else if (flag == 600) {
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            eta = Double.parseDouble(args[10]);

            for (int i = 0; i < trainData.length; i++) {
                x[0] = trainData[i][0];
                x[1] = trainData[i][1];
                y = trainData[i][2];

                System.out.printf("%.5f %.5f %.5f\n", x[0], x[1], y);

                setNodes();
                SGD();

                System.out.printf("%.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f\n", w[0], w[1], w[2], w[3], w[4], w[5], w[6], w[7], w[8]);

                double eval_error = 0;
                for (int k = 0; k < evalData.length; k++) {
                    eval_error += Math.pow(prediction(evalData[k][0], evalData[k][1]) - evalData[k][2], 2) / 2;
                }

                System.out.printf("%.5f\n", eval_error);
            }
        }

        else if (flag == 700) {
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            eta = Double.parseDouble(args[10]);
            T = Integer.parseInt(args[11]);

            for (int j = 0; j < T; j++) {
                for (int i = 0; i < trainData.length; i++) {
                    x[0] = trainData[i][0];
                    x[1] = trainData[i][1];
                    y = trainData[i][2];

                    setNodes();
                    SGD();
                }

                System.out.printf("%.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f\n", w[0], w[1], w[2], w[3], w[4], w[5], w[6], w[7], w[8]);

                double eval_error = 0;
                for (int k = 0; k < evalData.length; k++) {
                    eval_error += Math.pow(prediction(evalData[k][0], evalData[k][1]) - evalData[k][2], 2) / 2;
                }

                System.out.printf("%.5f\n", eval_error);
            }
        }

        else if (flag == 800) {
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            eta = Double.parseDouble(args[10]);
            T = Integer.parseInt(args[11]);

            double curr_eval_error = 0;
            double prev_eval_error = 0;

            int j;
            for (j = 1; j <= T; j++) {
                for (int i = 0; i < trainData.length; i++) {
                    x[0] = trainData[i][0];
                    x[1] = trainData[i][1];
                    y = trainData[i][2];

                    setNodes();
                    SGD();
                }

                curr_eval_error = 0;
                for (int k = 0; k < evalData.length; k++) {
                    curr_eval_error += Math.pow(prediction(evalData[k][0], evalData[k][1]) - evalData[k][2], 2) / 2;
                }

                if (curr_eval_error > prev_eval_error && j > 1)
                    break;

                prev_eval_error = curr_eval_error;
            }

            System.out.println(j);
            System.out.printf("%.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f %.5f\n", w[0], w[1], w[2], w[3], w[4], w[5], w[6], w[7], w[8]);
            System.out.printf("%.5f\n", curr_eval_error);

            double[] predictions = new double[testData.length];
            double correct_count = 0;
            for (int i = 0; i < testData.length; i++) {
                if (prediction(testData[i][0], testData[i][1]) >= 0.5) {
                    predictions[i] = 1;
                }

                if (predictions[i] == testData[i][2])
                    correct_count++;
            }

            System.out.println(String.format("%.5f", correct_count / testData.length));
        }
    }

    private static void setNodes() {
        u[0] = w[0] + w[1]*x[0] + w[2]*x[1];
        v[0] = ReLU_activation(u[0]);

        u[1] = w[3] + w[4]*x[0] + w[5]*x[1];
        v[1] = ReLU_activation(u[1]);

        u[2] = w[6] + w[7]*v[0] + w[8]*v[1];
        v[2] = sigmoid_activation(u[2]);
    }

    private static double E() {
        return Math.pow(v[2] - y, 2) / 2;
    }

    private static double dE_dv_C() {
        return v[2] - y;
    }

    private static double dE_du_C() {
        return dE_dv_C()*v[2]*(1 - v[2]);
    }

    private static double dE_dv_A() {
        return w[7]*dE_du_C();
    }

    private static double dE_dv_B() {
        return w[8]*dE_du_C();
    }

    private static double dE_du_A() {
        return dE_dv_A()*dv_A_du_A();
    }

    private static double dE_du_B() {
        return dE_dv_B()*dv_B_du_B();
    }

    private static double dv_A_du_A() {
        return u[0] >= 0 ? 1 : 0;
    }

    private static double dv_B_du_B() {
        return u[1] >= 0 ? 1 : 0;
    }

    private static double[] dE_dw() {
        double[] dE_dw = new double[w.length];

        dE_dw[0] = dE_du_A();
        dE_dw[1] = x[0]*dE_du_A();
        dE_dw[2] = x[1]*dE_du_A();
        dE_dw[3] = dE_du_B();
        dE_dw[4] = x[0]*dE_du_B();
        dE_dw[5] = x[1]*dE_du_B();
        dE_dw[6] = dE_du_C();
        dE_dw[7] = v[0]*dE_du_C();
        dE_dw[8] = v[1]*dE_du_C();

        return dE_dw;
    }

    // perform one iteration of SGD
    private static void SGD() {
        double[] w_new = new double[w.length];
        double[] dE_dw = dE_dw();

        for (int i = 0; i < w_new.length; i++) {
            w_new[i] = w[i] - eta*dE_dw[i];
        }

        w = w_new;
        setNodes();

        return;
    }

    private static double prediction(double x_1, double x_2) {
        double u_A = w[0] + w[1]*x_1 + w[2]*x_2;
        double v_A = ReLU_activation(u_A);

        double u_B = w[3] + w[4]*x_1 + w[5]*x_2;
        double v_B = ReLU_activation(u_B);

        double u_C = w[6] + w[7]*v_A + w[8]*v_B;
        double v_C = sigmoid_activation(u_C);

        return v_C;
    }

    private static double ReLU_activation(double z) {
        return Math.max(z, 0);
    }

    private static double sigmoid_activation(double z) {
        return 1 / (1 + Math.exp(-z));
    }

    private static double[][] readData(String fileName){
        ArrayList<Double> data = new ArrayList<Double>();
        try {
            File f = new File(fileName);
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                if (sc.hasNextDouble()) {
                    double i = sc.nextDouble();
                    data.add(i);
                }
                else {
                    sc.next();
                }
            }
            sc.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("File Not Found.");
        }

        double[][] array = new double[data.size() / 3][3];
        for (int i = 0; i < data.size(); i++) {
            array[i / 3][i % 3] = data.get(i);
        }
        return array;
    }

}
