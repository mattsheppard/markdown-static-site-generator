package com.kstruct.markdown.utils;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BrokenLinkRecorder {

    private final Path markdownDocumentationRoot;
    
    @Getter
    private final Map<Path, Set<String>> allBrokenLinks = new ConcurrentHashMap<>();
    
    public void recordBrokenMarkdownLinks(Path markdownFilePath, Set<String> linkTargets) {
        Set<String> brokenLinks = findBrokenMarkdownLinks(markdownFilePath, linkTargets);
        
        if (!brokenLinks.isEmpty()) {
            allBrokenLinks.put(markdownFilePath, brokenLinks);
        }
    }
    
    private Set<String> findBrokenMarkdownLinks(Path markdownFilePath, Set<String> linkTargets) {
        Set<String> brokenLinks = linkTargets.stream()
        .filter((linkTarget) -> {
            return !URI.create(linkTarget).isAbsolute();
        }).map((linkTarget) -> {
            URI uri = URI.create(linkTarget);
            return markdownDocumentationRoot.resolve(markdownFilePath.resolveSibling(uri.getPath()));
        }).filter((linkTarget) -> {
            return !linkExists(linkTarget);
        }).map((linkTarget) -> {
            return linkTarget.toString();
        }).collect(Collectors.toSet());
        
        return brokenLinks;
    }

    static boolean linkExists(Path markdownFilePath) {
        if (Files.exists(markdownFilePath))
            return true;

        // Since we are generating ".html" pages, we also check for ".html"
        // if .md does not exist.
        if (markdownFilePath.getFileName().toString().endsWith(".md")) {
            return Optional.of(markdownFilePath.getFileName())
                .map(Path::toString)
                .map(s -> s.substring(0, s.length() - ".md".length()))
                .map(noExtension -> noExtension + ".html")
                .map(markdownFilePath::resolveSibling)
                .map(Files::exists)
                .orElse(false);
        }

        // Would this ever happen?
        return false;
    }
}
