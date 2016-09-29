package com.kstruct.markdown.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.kstruct.markdown.utils.MarkdownUtils;
import com.kstruct.markdown.utils.PathUtils;

import lombok.Getter;
import lombok.Setter;

public abstract class NavigationNode {

    @Getter
    private Optional<NavigationNode> parent;

    @Getter
    private Path relativePath;

    private Path root;

    public NavigationNode(Path path, Path root, Optional<NavigationNode> parent) {
        this.parent = parent;
        
        this.root = root;
        this.relativePath = root.relativize(path);
    }

    public String getTitle() {
        return PathUtils.titleForPath(relativePath.getFileName());
    }

    public String getOutputPath() {
        return relativePath.toString();
    }

    public abstract List<NavigationNode> getChildren();

    public Boolean getHasHtmlPagesBelow() {
        if (getOutputPath().endsWith(MarkdownUtils.HTML_OUTPUT_FILE_EXTENSION)) {
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
    
    public Boolean isParentOfPageAt(String relativeUri) {
        Path activePage = root.relativize(root.resolve(relativeUri));
        Path outputPath = root.relativize(root.resolve(this.getOutputPath()));
        
        Boolean result = activePage.startsWith(outputPath);
        return result;
    }
    
    public Boolean isPageAt(String relativeUri) {
        Path activePage = root.relativize(root.resolve(relativeUri));
        Path outputPath = root.relativize(root.resolve(this.getOutputPath()));
        
        Boolean result = activePage.equals(outputPath);
        return result;
    }
}
