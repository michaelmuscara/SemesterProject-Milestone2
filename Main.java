import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        String directoryPath = "C:\\Users\\mpack\\Documents\\Programming Workshop Lab\\Milestone 2\\";
        Article stopWords = new Article(directoryPath + "stopwords.txt");

        ArrayList<Article> baseballArticles = loadArticles(directoryPath + "baseball");
        ArrayList<Article> footballArticles = loadArticles(directoryPath + "football");
        ArrayList<Article> basketballArticles = loadArticles(directoryPath + "basketball");
        Article positive = new Article(directoryPath + "positive-words.txt");
        Article negative = new Article(directoryPath + "negative-words.txt");


        Scanner scanner = new Scanner(System.in);


        while (true) {
            System.out.print("Enter an article topic (baseball, football, basketball): ");
            String topic = scanner.nextLine();

            if (topic.equals("baseball")) {
                processArticles(baseballArticles, stopWords, positive, negative);
                break;
            } else if (topic.equals("football")) {
                processArticles(footballArticles, stopWords, positive, negative);
                break;
            } else if (topic.equals("basketball")) {
                processArticles(basketballArticles, stopWords, positive, negative);
                break;
            } else {
                System.out.println("Invalid Input!");
            }

        }


    }

    public static ArrayList<Article> loadArticles(String directoryPath) throws IOException {
        File folder = new File(directoryPath);
        ArrayList<Article> articles = new ArrayList<>();

        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    articles.add(new Article(file.getPath()));
                }
            }
        }
        else {
            System.out.println("No files found in directory: " + directoryPath);
        }
        return articles;
    }

    public static void processArticles(ArrayList<Article> articles, Article stopWords, Article positive, Article negative) {
        Scanner scanner = new Scanner(System.in);
        int indexUnique = 0;
        int numUnique = articles.get(0).getUniqueWordCount();
        for (int i = 0; i < articles.size(); i++) {
            if (articles.get(i).getUniqueWordCount() > numUnique) {
                indexUnique = i;
                numUnique = articles.get(i).getUniqueWordCount();
            }
        }
        indexUnique++;
        for (int i = 0; i <articles.size(); i++) {
            Article article = articles.get(i);
            System.out.println("The article " + (i + 1) + " has " + article.getWordCount() + " words.");
            article.removeWord(stopWords);
            System.out.println("After removing the stop words, the article has " + article.getWordCount() + " words.");
            System.out.println("The article " + (i + 1) + " has the following word frequencies:");
            article.getWordFrequency();
            System.out.println();
        }

        System.out.print("What frequency of words do you want to see? ");
        int num = scanner.nextInt();
        System.out.println();
        for (int i = 0; i<articles.size();i++) {
            Article article = articles.get(i);

            System.out.println("Article " + (i+1) +":");
            article.getAttitude(positive, negative);
            System.out.println("Article " + (i+1) + "'s words above the threshold (" + num + ")");
            article.getMostRepeated(num);
            System.out.println();
        }
        System.out.println("The article " + indexUnique + " has the richest vocabulary with " + numUnique + " unique words!");
    }
}


    class Article {

        private File file;
        private Scanner reader;
        private ArrayList<String> data;

        public Article(String filePath) throws IOException {
            this.file = new File(filePath);
            this.reader = new Scanner(this.file);
            this.data = new ArrayList<>();
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                line = line.replaceAll("[^a-zA-Z]", " ");
                line = line.toLowerCase();
                String[] words = line.split("\\s+");

                for (String word : words) {
                    if (!word.isEmpty()) {
                        this.data.add(word);
                    }
                }
            }
            reader.close();
        }

        public ArrayList<String> getData() {
            return data;
        }

        public int getWordCount() {
            return this.data.size();
        }

        public ArrayList<String> removeWord(Article article) {

            for (int i = this.data.size() - 1; i >= 0; i--) {
                if (article.getData().contains(this.data.get(i))) {
                    this.data.remove(i);
                }
            }
            return this.data;
        }

        public void getWordFrequency() {

            ArrayList<String> uniqueWord = new ArrayList<>();
            ArrayList<Integer> frequency = new ArrayList<>();
            ArrayList<String> result = new ArrayList<>();


            for (String word : data) {
                int index = uniqueWord.indexOf(word);
                if (index != -1) {
                    frequency.set(index, frequency.get(index) + 1);
                } else {
                    uniqueWord.add(word);
                    frequency.add(1);
                }
            }

            FrequencySort s1 = new FrequencySort(uniqueWord, frequency);
            s1.sort();
        }

        public int getUniqueWordCount() {
            ArrayList<String> uniqueWord = new ArrayList<>();
            for (String word : data) {
                int index = uniqueWord.indexOf(word);
                if (index == -1) {
                    uniqueWord.add(word);
                }
            }

            return uniqueWord.size();
        }

        public void getMostRepeated(int num) {
            ArrayList<String> uniqueWord = new ArrayList<>();
            ArrayList<Integer> frequency = new ArrayList<>();

            for (String word : data) {
                int index = uniqueWord.indexOf(word);
                if (index != -1) {
                    frequency.set(index, frequency.get(index) + 1);
                } else {
                    uniqueWord.add(word);
                    frequency.add(1);
                }
            }

            for(int i = frequency.size() - 1; i>=0; i--) {
                if (frequency.get(i) < num) {
                    uniqueWord.remove(i);
                    frequency.remove(i);
                }
            }
            FrequencySort s1 = new FrequencySort(uniqueWord, frequency);
            s1.sort();
        }

        public void getAttitude(Article positive, Article negative) {
            int numPos = 0;
            int numNeg = 0;

            //counting positive and negative
            for (String word : data) {
                if (positive.getData().contains(word)) {
                    numPos++;
                }
                else if(negative.getData().contains(word)) {
                    numNeg++;
                }
            }

            System.out.println("Positive Count: " + numPos);
            System.out.println("Negative Count: " + numNeg);
            if(numPos > numNeg) {
                System.out.println("Overall Sentiment: Positive");
            }
            else if(numNeg > numPos) {
                System.out.println("Overall Sentiment: Negative");
            }
            else{
                System.out.println("Overall Sentiment: Neutral");
            }
        }

    }



    class FrequencySort {

        private ArrayList<String> unique;
        private ArrayList<Integer> freq;

        public FrequencySort(ArrayList<String> uniqueWords, ArrayList<Integer> frequency) {
            this.unique = uniqueWords;
            this.freq = frequency;
        }

        public void sort() {
            for (int i = 0; i < freq.size() - 1; i++) {
                for (int j = 0; j < freq.size() - i-1; j++) {
                    if (freq.get(j) < freq.get(j + 1)) {
                        int tempFreq = freq.get(j);
                        freq.set(j, freq.get(j + 1));
                        freq.set(j + 1, tempFreq);

                        String tempWord = unique.get(j);
                        unique.set(j, unique.get(j + 1));
                        unique.set(j + 1, tempWord);
                    }
                }
            }

            if(freq.isEmpty()) {
                System.out.println("No frequency found!");
            }
            else {
                for (int i = 0; i < freq.size(); i++) {
                    System.out.println(unique.get(i) + ": " + freq.get(i));
                }
            }

        }

    }


