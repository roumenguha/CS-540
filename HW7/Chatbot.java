
import java.util.*;
import java.io.*;

public class Chatbot{
    private static String corpusName = "./WARC201709_wid.txt";
    private static String vocabularyName = "./vocabulary.txt";
    private static int[] wordTypeCounts;
    private static double[] wordTypeProbabalities;

    private static ArrayList<Integer> readCorpus(){
        ArrayList<Integer> corpus = new ArrayList<Integer>();
        try {
            File f = new File(corpusName);
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                if (sc.hasNextInt()) {
                    int i = sc.nextInt();
                    corpus.add(i);
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
        return corpus;
    }

    private static ArrayList<String> readVocabulary() {
        ArrayList<String> vocabulary = new ArrayList<String>();
        try {
            File f = new File(vocabularyName);
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                vocabulary.add(sc.nextLine());
            }
            sc.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("File Not Found.");
        }
        return vocabulary;
    }

    static public void main(String[] args){
        ArrayList<Integer> corpus = readCorpus();
        ArrayList<String> vocabulary = readVocabulary();

        wordTypeCounts = new int[vocabulary.size() + 1]; // Add 1 to account for OOV
        wordTypeProbabalities = new double[wordTypeCounts.length];

        // Build an array to count word types
        for (int word : corpus) {
            wordTypeCounts[word]++;
            wordTypeProbabalities[word] += 1.0 / (double)corpus.size();
        }

        int flag = Integer.valueOf(args[0]);

        if (flag == 100) {
            int w = Integer.valueOf(args[1]);

            System.out.println(wordTypeCounts[w]);
            System.out.println(String.format("%.7f", wordTypeProbabalities[w]));
        }
        else if (flag == 200) {
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            double r = n1 / (double)n2;

            ArrayList<Triple<Integer, Double, Double>> unigramProbability = new ArrayList<Triple<Integer, Double, Double>>();
            double lowerBound = 0;
            for (int i = 0; i < wordTypeCounts.length; i++) {
                // if probability is non-zero, create a triple and add to list with appropriate bounds
                if (wordTypeProbabalities[i] >= 1e-8) {
                    unigramProbability.add(new Triple<Integer, Double, Double>(i, lowerBound, lowerBound += wordTypeProbabalities[i]));
                }
            }

            int generatedIndex = binarySearch(unigramProbability, r, (int) (r * unigramProbability.size()));
            Triple<Integer, Double, Double> selectedTriple = unigramProbability.get(generatedIndex);
            System.out.println(selectedTriple.getIndex());
            System.out.println(String.format("%.7f", selectedTriple.getLeft()));
            System.out.println(String.format("%.7f", selectedTriple.getRight()));
        }
        else if (flag == 300) {
            int h = Integer.valueOf(args[1]);
            int w = Integer.valueOf(args[2]);
            int count = 0;
            ArrayList<Integer> words_after_h = new ArrayList<Integer>();

            for (int i = 0; i < corpus.size(); i++) {
                if (corpus.get(i) == h) {
                    words_after_h.add(corpus.get(i + 1));
                    if (corpus.get(i + 1) == w) {
                        count++;
                    }
                }
            }

            // output
            System.out.println(count);
            System.out.println(words_after_h.size());
            System.out.println(String.format("%.7f", count / (double)words_after_h.size()));
        }
        else if (flag == 400) {
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h = Integer.valueOf(args[3]);
            double r = n1 / (double)n2;

            int count = 0;
            int[] wordTypeCountsAfterH = new int[wordTypeCounts.length];

            for (int i = 0; i < corpus.size(); i++) {
                if (corpus.get(i) == h) {
                    wordTypeCountsAfterH[corpus.get(i + 1)]++;
                    count++;
                }
            }

            ArrayList<Triple<Integer, Double, Double>> bigramProbability = new ArrayList<Triple<Integer, Double, Double>>();
            double lowerBound = 0;
            for (int i = 0; i < wordTypeCountsAfterH.length; i++) {
                // if count is non-zero, create a triple and add to list with appropriate bounds
                if (wordTypeCountsAfterH[i] > 0) {
                    double p = wordTypeCountsAfterH[i] / (double)count;
                    bigramProbability.add(new Triple<Integer, Double, Double>(i, lowerBound, lowerBound += p));
                }
            }

            int generatedIndex = binarySearch(bigramProbability, r, (int) (r * bigramProbability.size()));
            Triple<Integer, Double, Double> selectedTriple = bigramProbability.get(generatedIndex);
            System.out.println(selectedTriple.getIndex());
            System.out.println(String.format("%.7f", selectedTriple.getLeft()));
            System.out.println(String.format("%.7f", selectedTriple.getRight()));
        }
        else if (flag == 500) {
            int h1 = Integer.valueOf(args[1]);
            int h2 = Integer.valueOf(args[2]);
            int w = Integer.valueOf(args[3]);
            int count = 0;
            ArrayList<Integer> words_after_h1h2 = new ArrayList<Integer>();

            for (int i = 0; i < corpus.size(); i++) {
                if (corpus.get(i) == h1 && corpus.get(i + 1) == h2) {
                    words_after_h1h2.add(corpus.get(i + 1));
                    if (corpus.get(i + 2) == w) {
                        count++;
                    }
                }
            }

            // output
            System.out.println(count);
            System.out.println(words_after_h1h2.size());
            if (words_after_h1h2.size() == 0)
                System.out.println("undefined");
            else
                System.out.println(String.format("%.7f", count / (double)words_after_h1h2.size()));
        }
        else if (flag == 600) {
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h1 = Integer.valueOf(args[3]);
            int h2 = Integer.valueOf(args[4]);
            double r = n1 / (double)n2;

            int count = 0;
            int[] wordTypeCountsAfterH1H2 = new int[wordTypeCounts.length];

            for (int i = 0; i < corpus.size(); i++) {
                if (corpus.get(i) == h1 && corpus.get(i + 1) == h2) {
                    wordTypeCountsAfterH1H2[corpus.get(i + 2)]++;
                    count++;
                }
            }

            ArrayList<Triple<Integer, Double, Double>> trigramProbability = new ArrayList<Triple<Integer, Double, Double>>();
            double lowerBound = 0;
            for (int i = 0; i < wordTypeCountsAfterH1H2.length; i++) {
                // if count is non-zero, create a triple and add to list with appropriate bounds
                if (wordTypeCountsAfterH1H2[i] > 0) {
                    double p = wordTypeCountsAfterH1H2[i] / (double)count;
                    trigramProbability.add(new Triple<Integer, Double, Double>(i, lowerBound, lowerBound += p));
                }
            }

            if (trigramProbability.size() == 0) {
                System.out.println("undefined");
            }
            else {
                int generatedIndex = binarySearch(trigramProbability, r, (int) (r * trigramProbability.size()));
                Triple<Integer, Double, Double> selectedTriple = trigramProbability.get(generatedIndex);
                System.out.println(selectedTriple.getIndex());
                System.out.println(String.format("%.7f", selectedTriple.getLeft()));
                System.out.println(String.format("%.7f", selectedTriple.getRight()));
            }
        }
        else if (flag == 700) {
            int seed = Integer.valueOf(args[1]);
            int t = Integer.valueOf(args[2]);
            int h1 = 0, h2 = 0;

            Random rng = new Random();
            if (seed != -1) rng.setSeed(seed);

            if (t == 0) {
                // TODO Generate first word using r
                double r = rng.nextDouble();

                ArrayList<Triple<Integer, Double, Double>> unigramProbability = new ArrayList<Triple<Integer, Double, Double>>();
                double lowerBound = 0;
                for (int i = 0; i < wordTypeCounts.length; i++) {
                    // if probability is non-zero, create a triple and add to list with appropriate bounds
                    if (wordTypeProbabalities[i] >= 1e-8) {
                        unigramProbability.add(new Triple<Integer, Double, Double>(i, lowerBound, lowerBound += wordTypeProbabalities[i]));
                    }
                }

                int generatedIndex = binarySearch(unigramProbability, r, (int) (r * unigramProbability.size()));
                Triple<Integer, Double, Double> selectedTriple = unigramProbability.get(generatedIndex);
                h1 = selectedTriple.getIndex();
                System.out.println(h1);

                if(h1 == 9 || h1 == 10 || h1 == 12){
                    return;
                }

                // TODO Generate second word using r
                r = rng.nextDouble();
                int[] wordTypeCountsAfterH = new int[wordTypeCounts.length];
                int count = 0;
                ArrayList<Triple<Integer, Double, Double>> bigramProbability = new ArrayList<Triple<Integer, Double, Double>>();
                lowerBound = 0;

                for (int i = 0; i < corpus.size(); i++) {
                    if (corpus.get(i) == h1) {
                        wordTypeCountsAfterH[corpus.get(i + 1)]++;
                        count++;
                    }
                }

                for (int i = 0; i < wordTypeCountsAfterH.length; i++) {
                    // if count is non-zero, create a triple and add to list with appropriate bounds
                    if (wordTypeCountsAfterH[i] > 0) {
                        double p = wordTypeCountsAfterH[i] / (double)count;
                        bigramProbability.add(new Triple<Integer, Double, Double>(i, lowerBound, lowerBound += p));
                    }
                }

                generatedIndex = binarySearch(bigramProbability, r, (int) (r * bigramProbability.size()));
                selectedTriple = bigramProbability.get(generatedIndex);
                h2 = selectedTriple.getIndex();

                System.out.println(h2);
            }
            else if (t == 1){
                h1 = Integer.valueOf(args[3]);
                // TODO Generate second word using r
                double r = rng.nextDouble();
                int count = 0;
                int[] wordTypeCountsAfterH = new int[wordTypeCounts.length];

                for (int i = 0; i < corpus.size(); i++) {
                    if (corpus.get(i) == h1) {
                        wordTypeCountsAfterH[corpus.get(i + 1)]++;
                        count++;
                    }
                }

                ArrayList<Triple<Integer, Double, Double>> bigramProbability = new ArrayList<Triple<Integer, Double, Double>>();
                double lowerBound = 0;
                for (int i = 0; i < wordTypeCountsAfterH.length; i++) {
                    // if count is non-zero, create a triple and add to list with appropriate bounds
                    if (wordTypeCountsAfterH[i] > 0) {
                        double p = wordTypeCountsAfterH[i] / (double)count;
                        bigramProbability.add(new Triple<Integer, Double, Double>(i, lowerBound, lowerBound += p));
                    }
                }

                int generatedIndex = binarySearch(bigramProbability, r, (int) (r * bigramProbability.size()));
                Triple<Integer, Double, Double> selectedTriple = bigramProbability.get(generatedIndex);
                h2 = selectedTriple.getIndex();

                System.out.println(h2);
            }
            else if (t == 2){
                h1 = Integer.valueOf(args[3]);
                h2 = Integer.valueOf(args[4]);
            }

            while (h2 != 9 && h2 != 10 && h2 != 12) {
                double r = rng.nextDouble();
                int w  = 0;
                // TODO Generate new word using h1,h2
                int count = 0;
                int[] wordTypeCountsAfterH1H2 = new int[wordTypeCounts.length];

                for (int i = 0; i < corpus.size(); i++) {
                    if (corpus.get(i) == h1 && corpus.get(i + 1) == h2) {
                        wordTypeCountsAfterH1H2[corpus.get(i + 2)]++;
                        count++;
                    }
                }

                ArrayList<Triple<Integer, Double, Double>> trigramProbability = new ArrayList<Triple<Integer, Double, Double>>();
                double lowerBound = 0;
                for (int i = 0; i < wordTypeCountsAfterH1H2.length; i++) {
                    // if count is non-zero, create a triple and add to list with appropriate bounds
                    if (wordTypeCountsAfterH1H2[i] > 0) {
                        double p = wordTypeCountsAfterH1H2[i] / (double)count;
                        trigramProbability.add(new Triple<Integer, Double, Double>(i, lowerBound, lowerBound += p));
                    }
                }

                if (trigramProbability.size() == 0) {
                    System.out.println("undefined");
                }
                else {
                    int generatedIndex = binarySearch(trigramProbability, r, (int) (r * trigramProbability.size()));
                    Triple<Integer, Double, Double> selectedTriple = trigramProbability.get(generatedIndex);
                    w = selectedTriple.getIndex();
                }

                System.out.println(w);
                h1 = h2;
                h2 = w;
            }
        }

        return;
    }

    public static int binarySearch(ArrayList<Triple<Integer, Double, Double>> sortedList, double r, int mid) {
        int low = 0;
        int high = sortedList.size() - 1;
        while (low <= high) {
             if (Double.compare(sortedList.get(mid).getRight(), r) < 0) {
                low = mid + 1;
            } else if (Double.compare(sortedList.get(mid).getLeft(), r) > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
            mid = (low + high) / 2;
        }

        return Integer.MAX_VALUE;
    }

}

class Triple<I, L, R> {

    private final I index;
    private final L left;
    private final R right;

    public Triple(I index, L left, R right) {
        if(index == null){
            throw new IllegalArgumentException("Index value is not effective.");
        }
        if(left == null){
            throw new IllegalArgumentException("Left value is not effective.");
        }
        if(right == null){
            throw new IllegalArgumentException("Right value is not effective.");
        }
        this.index = index;
        this.left = left;
        this.right = right;
    }

    public I getIndex() {
        return this.index;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((index == null) ? 0 : index.hashCode());
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Triple<Object, Object, Object> other = (Triple<Object, Object, Object>) obj;
        if (index == null) {
            if (other.index != null)
                return false;
        } else if (!index.equals(other.index))
            return false;
        if (left == null) {
            if (other.left != null)
                return false;
        } else if (!left.equals(other.left))
            return false;
        if (right == null) {
            if (other.right != null)
                return false;
        } else if (!right.equals(other.right))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "<" + left + "," + index + "," + right + ">";
    }

}