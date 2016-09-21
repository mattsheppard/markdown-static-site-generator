package com.kstruct.markdown.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

public abstract class NavigationNode {

    @Getter
    private Optional<NavigationNode> parent;

    @Getter
    private Path relativePath;

    public Boolean getHasHtmlPagesBelow() {
        if (getOutputPath().endsWith(".html")) {
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
}
