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
        return PathUtils.titleForPath(relativePath.getFileName(), root);
    }

    public String getOutputPath() {
        return relativePath.toString();
    }

    public abstract List<NavigationNode> getChildren();

    public Boolean getHasHtmlPagesBelow() {
        for (NavigationNode n : getChildren()) {
            if (n.hasHtmlPagesBelowOrIsHtmlPageItself()) {
                return true;
            }
        }
        return false;
    }
    
    private Boolean hasHtmlPagesBelowOrIsHtmlPageItself() {
        if (isLeafHtmlPage()) {
            return true;
        } else {
            for (NavigationNode n : getChildren()) {
                if (n.hasHtmlPagesBelowOrIsHtmlPageItself()) {
                    return true;
                }
            }
            return false; 
        }
    }
    
    public abstract Boolean isLeafHtmlPage();
    
    public Boolean isParentOfPageAt(String relativeUri) {
        Path pageToConsider = root.relativize(root.resolve(relativeUri));
        Path currentNodeOutputPath = root.relativize(root.resolve(this.getOutputPath()));
        if (currentNodeOutputPath.endsWith(MarkdownUtils.DIRECTORY_INDEX_FILE_NAME + MarkdownUtils.HTML_OUTPUT_FILE_EXTENSION)) {
            currentNodeOutputPath = currentNodeOutputPath.resolveSibling("");
        }
        
        Boolean result = pageToConsider.startsWith(currentNodeOutputPath) 
            || currentNodeOutputPath.getParent() == null /* Root is always open */;
        return result;
    }
    
    public Boolean isPageAt(String relativeUri) {
        Path pageToConsider = root.relativize(root.resolve(relativeUri));
        Path currentNodeOutputPath = root.relativize(root.resolve(this.getOutputPath()));
        
        Boolean result = pageToConsider.equals(currentNodeOutputPath);
        return result;
    }

    public Optional<NavigationNode> findNodeFor(String relativeUri) {
        
        if (this.isPageAt(relativeUri)) {
            return Optional.of(this);
        } else {
            for (NavigationNode child : this.getChildren()) {
                Optional<NavigationNode> result = child.findNodeFor(relativeUri);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }
}
