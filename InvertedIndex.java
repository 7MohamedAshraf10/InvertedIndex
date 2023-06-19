import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.HashSet;

/**
 *
 * The InvertedIndex class represents an inverted index data structure for
 * information retrieval.
 * It provides methods for building the index, calculating term frequency,
 * document frequency,
 * and cosine similarity, and performing TF-IDF calculations.
 */
public class InvertedIndex {
    private Map<String, List<Integer>> invertedIndex;
    private Map<Integer, Map<String, Integer>> documentTermFrequency;
    private List<String> documents;
    private HashSet<String> links = new HashSet<String>();

    /**
     * Constructs an empty InvertedIndex object with initialized data structures.
     */
    public InvertedIndex() {
        invertedIndex = new HashMap<>();
        documentTermFrequency = new HashMap<>();
        documents = new ArrayList<>();
    }

    /**
     * Retrieves the web page links by crawling the specified URL and its linked
     * pages.
     * 
     * @param URL the URL to crawl and retrieve page links
     */

    public void getPageLinks(String URL) {

        if (!links.contains(URL)) {
            try {
                // 4. (i) If not add it to the index
                if (links.add(URL)) {
                    System.out.println(URL);
                }

                // 2. Fetch the HTML code
                Document doc = Jsoup.connect(URL).get(); // jsoup jar to extract web data
                // 3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = doc.select("a[href]");

                // 4. For each extracted URL... go back to Step 3.
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }

    }

    /**
     * Reads the files from a specified directory and processes each file.
     */
    public void readFiles() {
        File directory = new File("H://Last Term/Information Retrieval/Assignment-2/Files");
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    documents.add(file.getName());
                    processFile(file);
                }
            }
        }
    }

    /**
     * Processes a file to calculate term frequency, document frequency,
     * and updates the inverted index.
     * 
     * @param file the file being processed
     */
    private void processFile(File file) {
        int documentId = documents.size() - 1;
        Map<String, Integer> termFrequency = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\W+");

                for (String word : words) {
                    word = word.toLowerCase();
                    termFrequency.put(word, termFrequency.getOrDefault(word, 0) + 1);

                    if (!invertedIndex.containsKey(word)) {
                        invertedIndex.put(word, new ArrayList<>());
                    }

                    List<Integer> postings = invertedIndex.get(word);
                    if (!postings.contains(documentId)) {
                        postings.add(documentId);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        documentTermFrequency.put(documentId, termFrequency);
    }

    /**
     * Prints the term frequency and document frequency for each term in the
     * inverted index.
     */
    public void printTermFrequencyAndDocFrequency() {
        System.out.println("Term Frequency and Document Frequency:");

        for (Map.Entry<String, List<Integer>> entry : invertedIndex.entrySet()) {
            String term = entry.getKey();
            List<Integer> postings = entry.getValue();

            System.out.println("Term: " + term);
            System.out.println("Term Frequency: " + postings.size());
            System.out.println("Document Frequency: " + calculateDocumentFrequency(postings));
            System.out.println();
        }
    }

    /**
     * Prints the query frequency and document frequency for each term in the query.
     * 
     * @param query the query string
     */
    public void printQueryFrequencyAndDocFrequency(String query) {
        System.out.println("Query Frequency and Document Frequency:");

        String[] queryTerms = query.toLowerCase().split("\\W+");
        Map<String, Integer> queryTermFrequency = new HashMap<>();

        for (String term : queryTerms) {
            queryTermFrequency.put(term, queryTermFrequency.getOrDefault(term, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : queryTermFrequency.entrySet()) {
            String term = entry.getKey();
            int queryFrequency = entry.getValue();

            System.out.println("Query: " + term);
            System.out.println("Query Frequency: " + queryFrequency);
            System.out.println("Document Frequency: "
                    + calculateDocumentFrequency(invertedIndex.getOrDefault(term, new ArrayList<>())));
            System.out.println();
        }
    }

    /**
     * Calculates the document frequency for a given list of postings.
     * 
     * @param postings the list of postings for a term
     * @return the document frequency
     */
    private int calculateDocumentFrequency(List<Integer> postings) {
        Set<Integer> uniqueDocuments = new HashSet<>(postings);
        return uniqueDocuments.size();
    }

    public double cosineSim;

    /**
     * Computes the cosine similarity between the query and indexed documents.
     * 
     * @param query the query string
     * @return a map of document names and their corresponding cosine similarity
     *         values
     */
    public Map<String, Double> computeCosineSimilarity(String query) {
        String[] queryTerms = query.toLowerCase().split("\\W+");
        Map<String, Integer> queryTermFrequency = new HashMap<>();
        Map<String, Double> cosineSimilarity = new HashMap<>();

        for (String term : queryTerms) {
            queryTermFrequency.put(term, queryTermFrequency.getOrDefault(term, 0) + 1);
        }

        double queryVectorLength = calculateVectorLength(queryTermFrequency);

        for (Map.Entry<Integer, Map<String, Integer>> entry : documentTermFrequency.entrySet()) {
            int documentId = entry.getKey();
            Map<String, Integer> document = entry.getValue();

            double dotProduct = 0.0;
            double documentVectorLength = calculateVectorLength(document);

            for (String term : queryTerms) {
                if (document.containsKey(term)) {
                    int queryTermFreq = queryTermFrequency.get(term);
                    int documentTermFreq = document.get(term);
                    dotProduct += (queryTermFreq * documentTermFreq);
                }
            }

            cosineSim = dotProduct / (queryVectorLength * documentVectorLength);
            cosineSimilarity.put(documents.get(documentId), cosineSim);

            // Print cosine similarity
            // System.out.println("Cosine similarity for document " +
            // documents.get(documentId) + ": " + cosineSim);
        }

        // Print files containing the query
        System.out.println("Files containing the query:");
        for (String term : queryTerms) {
            if (invertedIndex.containsKey(term)) {
                List<Integer> postings = invertedIndex.get(term);
                for (int docId : postings) {
                    System.out.println(documents.get(docId));
                }
            }
        }
        System.out.println();

        return cosineSimilarity;
    }

    /**
     * Calculates the vector length for a given term frequency map.
     * 
     * @param termFrequency the term frequency map
     * @return the vector length
     */
    private double calculateVectorLength(Map<String, Integer> termFrequency) {
        double vectorLength = 0.0;

        for (int termFreq : termFrequency.values()) {
            vectorLength += Math.pow(termFreq, 2);
        }

        return Math.sqrt(vectorLength);
    }

    /**
     * 
     * Ranks the documents based on their cosine similarity values in descending
     * order.
     * 
     * @param cosineSimilarity a map of document names and their corresponding
     *                         cosine similarity values
     * @return a list of ranked documents
     */
    public List<String> rankDocuments(Map<String, Double> cosineSimilarity) {
        List<Map.Entry<String, Double>> sortedList = new ArrayList<>(cosineSimilarity.entrySet());
        sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        List<String> rankedDocuments = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sortedList) {
            rankedDocuments.add(entry.getKey());
        }
        System.out.println("Ranked Documents:");
        for (String document : rankedDocuments) {
            System.out.println(document + " ( " + cosineSim + " )");
        }

        return rankedDocuments;
    }

    /*
     * public Map<String, Double> calculateTFIDF() {
     * Map<String, Double> tfidfMap = new HashMap<>();
     * 
     * // Calculate IDF (Inverse Document Frequency)
     * Map<String, Double> idfMap = new HashMap<>();
     * int totalDocuments = documents.size();
     * for (String term : invertedIndex.keySet()) {
     * List<Integer> postings = invertedIndex.get(term);
     * int documentFrequency = calculateDocumentFrequency(postings);
     * double idf = Math.log((double) totalDocuments / (documentFrequency + 1));
     * idfMap.put(term, idf);
     * }
     * 
     * // Calculate TF-IDF for each term in each document
     * for (Map.Entry<Integer, Map<String, Integer>> entry :
     * documentTermFrequency.entrySet()) {
     * int documentId = entry.getKey();
     * Map<String, Integer> termFrequency = entry.getValue();
     * 
     * for (Map.Entry<String, Integer> tfEntry : termFrequency.entrySet()) {
     * String term = tfEntry.getKey();
     * int tf = tfEntry.getValue();
     * double idf = idfMap.getOrDefault(term, 0.0);
     * double tfidf = tf * idf;
     * tfidfMap.put(documents.get(documentId) + ":" + term, tfidf);
     * }
     * }
     * 
     * return tfidfMap;
     * }
     */

    /**
     * 
     * Calculates the TF-IDF (Term Frequency-Inverse Document Frequency) for the
     * query terms.
     * 
     * @param query the query string
     * @return a map of query terms and their corresponding TF-IDF values
     */
    public Map<String, Double> calculateTFIDF(String query) {
        Map<String, Double> tfidfMap = new HashMap<>();

        // Calculate IDF (Inverse Document Frequency) for query terms
        String[] queryTerms = query.toLowerCase().split("\\W+");
        int totalDocuments = documents.size();
        Map<String, Integer> queryTermFrequency = new HashMap<>();
        for (String term : queryTerms) {
            queryTermFrequency.put(term, queryTermFrequency.getOrDefault(term, 0) + 1);
        }
        for (String term : queryTermFrequency.keySet()) {
            List<Integer> postings = invertedIndex.getOrDefault(term, new ArrayList<>());
            int documentFrequency = calculateDocumentFrequency(postings);
            double idf = Math.log((double) totalDocuments / (documentFrequency + 1));
            double tfidf = queryTermFrequency.get(term) * idf;
            tfidfMap.put(term, tfidf);
        }

        return tfidfMap;
    }

    /**
     * 
     * The main entry point of the program.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.readFiles();

        invertedIndex.printTermFrequencyAndDocFrequency();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter query: ");
        String query = scanner.nextLine();

        Map<String, Double> cosineSimilarity = invertedIndex.computeCosineSimilarity(query);
        List<String> rankedDocuments = invertedIndex.rankDocuments(cosineSimilarity);

        /*
         * System.out.println("Ranked Documents:");
         * for (String document : rankedDocuments) {
         * System.out.println(document);
         * }
         */
        invertedIndex.printQueryFrequencyAndDocFrequency(query);

        System.out.println(" ");

        /*
         * Map<String, Double> tfidfMap = invertedIndex.calculateTFIDF();
         * System.out.println("TF-IDF:");
         * for (Map.Entry<String, Double> entry : tfidfMap.entrySet()) {
         * String documentTerm = entry.getKey();
         * double tfidf = entry.getValue();
         * System.out.println(documentTerm + " => " + tfidf);
         * }
         */

        Map<String, Double> tfidfMap = invertedIndex.calculateTFIDF(query);
        System.out.println("TF-IDF for query:");
        for (Map.Entry<String, Double> entry : tfidfMap.entrySet()) {
            String term = entry.getKey();
            double tfidf = entry.getValue();
            System.out.println(term + " => " + tfidf);
        }

        System.out.println(" ");

        // Crawl web pages
        String urls = "https://pypi.org/project/atari-py/";
        System.out.println("Web Crawling part:");
        invertedIndex.getPageLinks(urls);

    }
}