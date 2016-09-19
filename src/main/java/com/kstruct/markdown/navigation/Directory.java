package com.kstruct.markdown.navigation;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;

public class Directory implements NavigationNode {
    @Getter
    private String title;
    @Getter
    private URI uri;
    
    @Getter
    private File sourceFile;

    private NavigationNode parent;
    public Optional<NavigationNode> getParent() {
        return Optional.of(parent);
    }

    public List<NavigationNode> getChildren() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
