package com.kstruct.markdown.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.kstruct.markdown.utils.Markdown;

import lombok.Getter;
import lombok.Setter;

public abstract class NavigationNode {

    @Getter
    private Optional<NavigationNode> parent;

    @Getter
    private Path relativePath;

    public NavigationNode(Path path, Path root, Optional<NavigationNode> parent) {
        this.parent = parent;

        this.relativePath = root.relativize(path);
    }

    public String getTitle() {
        return relativePath.getFileName().toString();
    }

    public String getOutputPath() {
        return relativePath.toString();
    }

    public abstract List<NavigationNode> getChildren();

    public Boolean getHasHtmlPagesBelow() {
        if (getOutputPath().endsWith(Markdown.HTML_OUTPUT_FILE_EXTENSION)) {
            return true;
        } else {
            for (NavigationNode n : getChildren()) {
                if (n.getHasHtmlPagesBelow()) {
                    return true;
                }
            }
            return false;
        }
    }
}
