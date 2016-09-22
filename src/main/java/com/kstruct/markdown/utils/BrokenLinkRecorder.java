package com.kstruct.markdown.utils;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
            return !Files.exists(linkTarget);
        }).map((linkTarget) -> {
            return linkTarget.toString();
        }).collect(Collectors.toSet());
        
        return brokenLinks;
    }
    
}
