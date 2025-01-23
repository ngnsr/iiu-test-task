package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    List<Document> documents = new ArrayList<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if(document.getId() == null || document.getId().isBlank()) {
            document.id = UUID.randomUUID().toString();
        }
        documents.add(document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documents.stream().filter(document -> {
            // we assume that title should start with titlePrefix
            if(matchesTitlePrefix(document.title, request.titlePrefixes)) return true;
            if(request.containsContents != null && !request.containsContents.isEmpty()
                    && containsAnySubstring(document.content, request.containsContents)) return true;
            if(request.authorIds != null && !request.authorIds.isEmpty()
                    && request.authorIds.contains(document.getId())) return true;

            if (isWithingDateRange(document.created, request.createdFrom, request.createdTo)) return true;
            return false;
            }
        ).toList(); // return unmodifiable list
        //).collect(Collectors.toList(); // return modifiable List
    }

    /**
     * Checks if the title starts with any of the given prefixes.
     *
     * @param title - the title to check.
     * @param titlePrefixes - the list of prefixes to match.
     * @return true if the title starts with any of the prefixes; false otherwise.
     */
    private boolean matchesTitlePrefix(String title, List<String> titlePrefixes) {
        return titlePrefixes != null && !titlePrefixes.isEmpty() && titlePrefixes.stream().anyMatch(title::startsWith);
    }

    /**
     * Checks if the document creation date satisfies the search date range conditions.
     *
     * @param created - the creation date of the document.
     * @param createdFrom - the start date of the search range (inclusive).
     * @param createdTo - the end date of the search range (inclusive).
     * @return true if the creation date is within the specified range; false otherwise.
     */
    private boolean isWithingDateRange(Instant created, Instant createdFrom, Instant createdTo) {
        if (createdFrom != null && created.compareTo(createdFrom) < 0) {
            return false;
        }
        if (createdTo != null && created.compareTo(createdTo) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the given string contains any of the substrings from the list.
     *
     * @param string - the string to search within.
     * @param possibleStrings - the list of substrings to check for.
     * @return true if any substring is found in the string; false otherwise.
     */
    private boolean containsAnySubstring(String string, List<String> possibleStrings) {
        for (String s : possibleStrings) {
            if (string.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return documents.stream().filter(d -> d.getId().equals(id)).findFirst();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}