package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kstruct.markdown.model.Directory;
import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.model.SimpleFile;
import com.kstruct.markdown.model.NavigationNode;

public class BuildNavigationStructure {

    private Path inputDirectory;
    public BuildNavigationStructure(Path inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public NavigationNode build() {
        return buildSiteModel(inputDirectory, inputDirectory, Optional.empty());
    }

    private NavigationNode buildSiteModel(Path path, Path root, Optional<NavigationNode> parent) {
        if (Files.isDirectory(path)) {
            // Create the directroy, then recurse down.
            try {
                Directory directory = new Directory(path, root, parent);

                List<NavigationNode> children = Files
                    .list(path)
                    .sorted()    // Alphabetical order is convenient - ensure it
                    .map(childPath -> {
                        return buildSiteModel(childPath, root, Optional.of(directory));
                    })
                    .collect(Collectors.toList());
                
                directory.setChildren(children);
                
                return directory;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (path.getFileName().toString().endsWith(MarkdownPage.MARKDOWN_FILE_EXTENSION)) {
            return new MarkdownPage(path, root, parent);
        } else {
            return new SimpleFile(path, root, parent);
        }
    }

}
