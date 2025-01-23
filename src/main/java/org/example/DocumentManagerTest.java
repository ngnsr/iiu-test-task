package org.example;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class DocumentManagerTest {
    public static void main(String[] args) {
        DocumentManager documentManager = new DocumentManager();

        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Test Title")
                .content("Test Content")
                .created(Instant.now())
                .author(new DocumentManager.Author("1", "Author"))
                .build();

        documentManager.save(doc1);

        assert doc1.getId() != null && !doc1.getId().isBlank() : "Test 1 Failed: ID should be generated";


        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("Prefix Test Title")
                .content("Some content")
                .created(Instant.now())
                .author(new DocumentManager.Author("1", "Author"))
                .build();

        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Prefix"))
                .build();

        List<DocumentManager.Document> searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc2) : "Test 2 Failed: Search by title prefix failed";


        DocumentManager.Document doc3 = DocumentManager.Document.builder()
                .title("Title")
                .content("This content includes a keyword")
                .created(Instant.now())
                .author(new DocumentManager.Author("2", "Another Author"))
                .build();

        documentManager.save(doc3);

        request = DocumentManager.SearchRequest.builder()
                .containsContents(List.of("keyword"))
                .build();

        searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc3) : "Test 3 Failed: Search by content failed";


        DocumentManager.Document doc4 = DocumentManager.Document.builder()
                .title("Another Title")
                .content("Another content")
                .created(Instant.now())
                .author(new DocumentManager.Author("3", "Different Author"))
                .build();

        documentManager.save(doc4);

        request = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("3"))
                .build();

        searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc4) : "Test 4 Failed: Search by author ID failed";


        Instant now = Instant.now();
        DocumentManager.Document doc5 = DocumentManager.Document.builder()
                .title("Title within range")
                .content("Content")
                .created(now.minusSeconds(1000))
                .author(new DocumentManager.Author("4", "Some Author"))
                .build();

        documentManager.save(doc5);

        DocumentManager.Document doc6 = DocumentManager.Document.builder()
                .title("Title out of range")
                .content("Content")
                .created(now.plusSeconds(1000))
                .author(new DocumentManager.Author("4", "Some Author"))
                .build();

        documentManager.save(doc6);

        request = DocumentManager.SearchRequest.builder()
                .createdFrom(now.minusSeconds(500))
                .createdTo(now.plusSeconds(500))
                .build();

        searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc5) : "Test 5 Failed: Search by date range failed";

        Optional<DocumentManager.Document> foundDoc = documentManager.findById(doc1.getId());
        assert foundDoc.isPresent() && foundDoc.get().equals(doc1) : "Test 6 Failed: Find by ID failed";

        Optional<DocumentManager.Document> nonExistentDoc = documentManager.findById("non-existent-id");
        assert nonExistentDoc.isEmpty() : "Test 7 Failed: Find by non-existent ID should return empty";

        DocumentManager.Document doc7 = DocumentManager.Document.builder()
                .title("Advanced Test Title")
                .content("Test content")
                .created(now.minusSeconds(200))
                .author(new DocumentManager.Author("5", "Advanced Author"))
                .build();

        documentManager.save(doc7);

        request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Advanced", "Test"))
                .build();

        searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc7) : "Test 8 Failed: Search by multiple title prefixes failed";

        DocumentManager.Document doc8 = DocumentManager.Document.builder()
                .title("Another Content Test")
                .content("This content contains keyword1 and keyword2")
                .created(now.minusSeconds(300))
                .author(new DocumentManager.Author("6", "Content Author"))
                .build();

        documentManager.save(doc8);

        request = DocumentManager.SearchRequest.builder()
                .containsContents(List.of("keyword1", "keyword2"))
                .build();

        searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc8) : "Test 9 Failed: Search by multiple content keywords failed";

        DocumentManager.Document doc9 = DocumentManager.Document.builder()
                .title("Empty Search Test")
                .content("No match")
                .created(now.minusSeconds(500))
                .author(new DocumentManager.Author("7", "Empty Search Author"))
                .build();

        documentManager.save(doc9);

        request = DocumentManager.SearchRequest.builder()
                .build();

        searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc9) : "Test 10 Failed: Search with empty parameters failed";

        DocumentManager.Document doc10 = DocumentManager.Document.builder()
                .title("Test without upper date range")
                .content("Content")
                .created(now.minusSeconds(100))
                .author(new DocumentManager.Author("8", "Date Range Author"))
                .build();

        documentManager.save(doc10);

        request = DocumentManager.SearchRequest.builder()
                .createdFrom(now.minusSeconds(200))
                .build();

        searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc10) : "Test 11 Failed: Search by date with no upper bound failed";

        DocumentManager.Document doc11 = DocumentManager.Document.builder()
                .title("Test without lower date range")
                .content("Content")
                .created(now.plusSeconds(100))
                .author(new DocumentManager.Author("9", "Another Date Author"))
                .build();

        documentManager.save(doc11);

        request = DocumentManager.SearchRequest.builder()
                .createdTo(now.plusSeconds(200))
                .build();

        searchResult = documentManager.search(request);

        assert searchResult.size() == 1 && searchResult.getFirst().equals(doc11) : "Test 12 Failed: Search by date with no lower bound failed";

        System.out.println("All tests passed!");
    }
}