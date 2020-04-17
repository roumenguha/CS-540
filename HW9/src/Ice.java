import java.util.*;
import java.io.*;

public class Ice {

    private static double[][] data;
    private static double meanYear, varianceYear, standardDeviationYear;
    private static double mean, variance, standardDeviation;    // Sample statistics
    private static double[] Beta = new double[2];
    private static double eta;
    private static double T;

    public static void main(String[] args) {
        readData();
        calculateStatistics();

        int firstYear = (int) data[0][0];
        int lastYear = (int) data[data.length - 1][0];

        int flag = Integer.valueOf(args[0]);

        if (flag == 100) {
            for (int i = 0; i < data.length; i++) {
                System.out.println((int) data[i][0] + " " + (int) data[i][1]);
            }
        }

        else if (flag == 200) {
            System.out.println(data.length);
            System.out.println(String.format("%.2f", mean));
            System.out.println(String.format("%.2f", standardDeviation));
        }

        else if (flag == 300) {
            Beta[0] = Double.valueOf(args[1]);
            Beta[1] = Double.valueOf(args[2]);

            System.out.println(String.format("%.2f", MSE()));
        }

        else if (flag == 400) {
            Beta[0] = Double.valueOf(args[1]);
            Beta[1] = Double.valueOf(args[2]);

            System.out.println(String.format("%.2f", dMSE_dBeta0()));
            System.out.println(String.format("%.2f", dMSE_dBeta1()));
        }

        else if (flag == 500) {
            eta = Double.valueOf(args[1]);
            T = Double.valueOf(args[2]);

            Beta[0] = 0;
            Beta[1] = 0;

            double[] newBeta = new double[2];

            for (int t = 1; t <= T; t++) {
                newBeta[0] = Beta[0] - eta * dMSE_dBeta0();
                newBeta[1] = Beta[1] - eta * dMSE_dBeta1();

                Beta[0] = newBeta[0];
                Beta[1] = newBeta[1];

                System.out.println(t + " " + String.format("%.2f", Beta[0]) + " " + String.format("%.2f", Beta[1]) + " " + String.format("%.2f", MSE()));
            }
        }

        else if (flag == 600) {
            double numerator = 0, denominator = 0;

            for (int i = 0; i < data.length; i++) {
                numerator += (data[i][0] - meanYear) * (data[i][1] - mean);
                denominator += Math.pow(data[i][0] - meanYear, 2);
            }

            Beta[1] = numerator / denominator;
            Beta[0] = mean - Beta[1] * meanYear;

            System.out.println(String.format("%.2f", Beta[0]) + " " + String.format("%.2f", Beta[1]) + " " + String.format("%.2f", MSE()));
        }

        else if (flag == 700) {
            double year = Double.valueOf(args[1]);
            double numerator = 0, denominator = 0;

            for (int i = 0; i < data.length; i++) {
                numerator += (data[i][0] - meanYear) * (data[i][1] - mean);
                denominator += Math.pow(data[i][0] - meanYear, 2);
            }

            Beta[1] = numerator / denominator;
            Beta[0] = mean - Beta[1] * meanYear;

            System.out.println(String.format("%.2f", prediction(year)));
        }

        else if (flag == 800) {
            normalizeData();

            eta = Double.valueOf(args[1]);
            T = Double.valueOf(args[2]);

            Beta[0] = 0;
            Beta[1] = 0;

            double[] newBeta = new double[2];

            for (int t = 1; t <= T; t++) {
                newBeta[0] = Beta[0] - eta * dMSE_dBeta0();
                newBeta[1] = Beta[1] - eta * dMSE_dBeta1();

                Beta = newBeta;

                System.out.println(t + " " + String.format("%.2f", Beta[0]) + " " + String.format("%.2f", Beta[1]) + " " + String.format("%.2f", MSE()));
            }
        }

        else if (flag == 900) {
            normalizeData();

            eta = Double.valueOf(args[1]);
            T = Double.valueOf(args[2]);

            Beta[0] = 0;
            Beta[1] = 0;

            double[] newBeta = new double[2];

            Random rand = new Random();

            System.out.println("Last year = " + data[data.length - 1][0]);
            for (int t = 1; t <= T; t++) {
                int randNum = rand.nextInt(lastYear - firstYear + 1);

                newBeta[0] = Beta[0] - eta * SGD_dMSE_dBeta0(randNum);
                newBeta[1] = Beta[1] - eta * SGD_dMSE_dBeta1(randNum);

                Beta = newBeta;

                System.out.println(t + " " + String.format("%.2f", Beta[0]) + " " + String.format("%.2f", Beta[1]) + " " + String.format("%.2f", MSE()));
            }
        }
    }

    private static void readData() {
    	
    	int[] numbers =    {1855, 118,
                            1856, 151,
                            1857, 121,
                            1858, 96,
                            1859, 110,
                            1860, 117,
                            1861, 132,
                            1862, 104,
                            1863, 125,
                            1864, 118,
                            1865, 125,
                            1866, 123,
                            1867, 110,
                            1868, 127,
                            1869, 131,
                            1870, 99,
                            1871, 126,
                            1872, 144,
                            1873, 136,
                            1874, 126,
                            1875, 91,
                            1876, 130,
                            1877, 62,
                            1878, 112,
                            1879, 99,
                            1880, 161,
                            1881, 78,
                            1882, 124,
                            1883, 119,
                            1884, 124,
                            1885, 128,
                            1886, 131,
                            1887, 113,
                            1888, 88,
                            1889, 75,
                            1890, 111,
                            1891, 97,
                            1892, 112,
                            1893, 101,
                            1894, 101,
                            1895, 91,
                            1896, 110,
                            1897, 100,
                            1898, 130,
                            1899, 111,
                            1900, 107,
                            1901, 105,
                            1902, 89,
                            1903, 126,
                            1904, 108,
                            1905, 97,
                            1906, 94,
                            1907, 83,
                            1908, 106,
                            1909, 98,
                            1910, 101,
                            1911, 108,
                            1912, 99,
                            1913, 88,
                            1914, 115,
                            1915, 102,
                            1916, 116,
                            1917, 115,
                            1918, 82,
                            1919, 110,
                            1920, 81,
                            1921, 96,
                            1922, 125,
                            1923, 104,
                            1924, 105,
                            1925, 124,
                            1926, 103,
                            1927, 106,
                            1928, 96,
                            1929, 107,
                            1930, 98,
                            1931, 65,
                            1932, 115,
                            1933, 91,
                            1934, 94,
                            1935, 101,
                            1936, 121,
                            1937, 105,
                            1938, 97,
                            1939, 105,
                            1940, 96,
                            1941, 82,
                            1942, 116,
                            1943, 114,
                            1944, 92,
                            1945, 98,
                            1946, 101,
                            1947, 104,
                            1948, 96,
                            1949, 109,
                            1950, 122,
                            1951, 114,
                            1952, 81,
                            1953, 85,
                            1954, 92,
                            1955, 114,
                            1956, 111,
                            1957, 95,
                            1958, 126,
                            1959, 105,
                            1960, 108,
                            1961, 117,
                            1962, 112,
                            1963, 113,
                            1964, 120,
                            1965, 65,
                            1966, 98,
                            1967, 91,
                            1968, 108,
                            1969, 113,
                            1970, 110,
                            1971, 105,
                            1972, 97,
                            1973, 105,
                            1974, 107,
                            1975, 88,
                            1976, 115,
                            1977, 123,
                            1978, 118,
                            1979, 99,
                            1980, 93,
                            1981, 96,
                            1982, 54,
                            1983, 111,
                            1984, 85,
                            1985, 107,
                            1986, 89,
                            1987, 87,
                            1988, 97,
                            1989, 93,
                            1990, 88,
                            1991, 99,
                            1992, 108,
                            1993, 94,
                            1994, 74,
                            1995, 119,
                            1996, 102,
                            1997, 47,
                            1998, 82,
                            1999, 53,
                            2000, 115,
                            2001, 21,
                            2002, 89,
                            2003, 80,
                            2004, 101,
                            2005, 95,
                            2006, 66,
                            2007, 106,
                            2008, 97,
                            2009, 87,
                            2010, 109,
                            2011, 57,
                            2012, 87,
                            2013, 117,
                            2014, 91,
                            2015, 62,
                            2016, 65};
    	
        data = new double[numbers.length / 2] [2];

        for (int i = 0; i < numbers.length; i++) {
            data[i / 2][i % 2] = (double) numbers[i];
        }
    }

    private static void calculateStatistics() {
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i][1];
        }

        mean = sum / data.length;

        double squaredError = 0;
        for (int i = 0; i < data.length; i++) {
            squaredError += Math.pow(data[i][1] - mean, 2);
        }

        variance = squaredError / (data.length - 1);

        standardDeviation = Math.sqrt(variance);

        ////////////////////////////////////////////////////////////////////////////////

        sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i][0];
        }

        meanYear = sum / data.length;

        squaredError = 0;
        for (int i = 0; i < data.length; i++) {
            squaredError += Math.pow(data[i][0] - meanYear, 2);
        }

        varianceYear = squaredError / (data.length - 1);

        standardDeviationYear = Math.sqrt(varianceYear);
    }

    private static void normalizeData() {
        for (int i = 0; i < data.length; i++) {
            data[i][0] = (data[i][0] - meanYear) / standardDeviationYear;
        }
    }

    private static double MSE() {
        double result = 0;

        for (int i = 0; i < data.length; i++) {
            result += Math.pow(Beta[0] + Beta[1] * data[i][0] - data[i][1], 2);
        }

        return result / data.length;
    }

    private static double dMSE_dBeta0() {
        double result = 0;

        for (int i = 0; i < data.length; i++) {
            result += (Beta[0] + Beta[1] * data[i][0] - data[i][1]);
        }

        return result * 2 / data.length;
    }

    private static double dMSE_dBeta1() {
        double result = 0;

        for (int i = 0; i < data.length; i++) {
            result += (Beta[0] + Beta[1] * data[i][0] - data[i][1]) * data[i][0];
        }

        return result * 2 / data.length;
    }

    private static double SGD_dMSE_dBeta0(int random) {
        return 2 * (Beta[0] + Beta[1] * data[random][0] - data[random][1]);
    }

    private static double SGD_dMSE_dBeta1(int random) {
        return 2 * (Beta[0] + Beta[1] * data[random][0] - data[random][1]) * data[random][0];
    }

    private static double prediction(double year) {
        return Beta[0] + Beta[1] * year;
    }

}
