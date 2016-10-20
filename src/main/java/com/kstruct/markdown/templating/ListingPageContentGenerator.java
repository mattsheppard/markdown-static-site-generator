package com.kstruct.markdown.templating;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kstruct.markdown.utils.MarkdownUtils;

import lombok.Getter;

public class ListingPageContentGenerator {

    public ListingPageContent getListingPageContent(Path path) {
        ListingPageContent result = new ListingPageContent();
        try {
            result.categories = Files.list(path.getParent())
                .filter(p -> Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
            
            result.pages = Files.list(path.getParent())
                .filter(p -> !Files.isDirectory(p))
                .filter(p -> !p.endsWith(MarkdownUtils.DIRECTORY_INDEX_FILE_NAME + MarkdownUtils.MARKDOWN_FILE_EXTENSION))
                .filter(p -> p.toString().endsWith(MarkdownUtils.MARKDOWN_FILE_EXTENSION))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
            
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static class ListingPageContent {
        @Getter
        private List<String> categories = new ArrayList<>();
        @Getter
        private List<String> pages = new ArrayList<>();
    }
}
