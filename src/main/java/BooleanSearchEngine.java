import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> map = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        if (pdfsDir.isDirectory()) {
            for (File pdfFile : Objects.requireNonNull(pdfsDir.listFiles())) {
                if (pdfFile.isFile()) {
                    try (var doc = new PdfDocument(new PdfReader(pdfFile.getPath()))) {
                        for (int n = 1; n <= doc.getNumberOfPages(); n++) {
                            Map<String, Integer> freqs = new HashMap<>();
                            PdfPage pageOfFile = doc.getPage(n);
                            var text = PdfTextExtractor.getTextFromPage(pageOfFile);
                            var words = text.split("\\P{IsAlphabetic}+");

                            for (var word : words) {
                                if (word.isEmpty()) {
                                    continue;
                                }
                                word = word.toLowerCase();
                                freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                            }

                            for (Map.Entry<String, Integer> freqsSet : freqs.entrySet()) {
                                PageEntry pageEntry = new PageEntry(pdfFile.getName(), n, freqsSet.getValue());
                                if (!map.containsKey(freqsSet.getKey())) {
                                    map.put(freqsSet.getKey(), new ArrayList<>());
                                }
                                map.get(freqsSet.getKey()).add(pageEntry);
                            }
                        }
                    }
                }
            }
            for (Map.Entry<String, List<PageEntry>> set : map.entrySet()) {
                Collections.sort(map.get(set.getKey()));
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<String> wordList = new LinkedList<>(Arrays.asList(word.trim().toLowerCase().split("\\s++")));
        return map.getOrDefault(wordList.get(0), Collections.emptyList());
    }
}
