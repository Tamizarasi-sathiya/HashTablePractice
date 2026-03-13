import java.util.*;

class PlagiarismDetector {

    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    private HashMap<String, List<String>> documentNgrams = new HashMap<>();

    private int N = 5; // size of n-gram (5 words)

    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }

        System.out.println("Indexed " + ngrams.size() + " n-grams from " + docId);
    }

    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            Set<String> docs = ngramIndex.get(gram);

            if (docs != null) {

                for (String d : docs) {

                    if (!d.equals(docId)) {
                        matchCount.put(d, matchCount.getOrDefault(d, 0) + 1);
                    }
                }
            }
        }

        System.out.println("Analyzing " + docId);
        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            String otherDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Matches with " + otherDoc + ": " + matches);
            System.out.printf("Similarity: %.2f%%\n", similarity);

            if (similarity > 60) {
                System.out.println("⚠ PLAGIARISM DETECTED");
            } else if (similarity > 10) {
                System.out.println("Suspicious similarity");
            }

            System.out.println();
        }
    }
}

public class PS04 {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "Artificial intelligence is transforming the world of technology and education systems today";

        String essay2 = "Artificial intelligence is transforming the world of technology and business systems today";

        String essay3 = "Machine learning and data science are important parts of modern computing";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay3);

        detector.analyzeDocument("essay_092.txt");
    }
}