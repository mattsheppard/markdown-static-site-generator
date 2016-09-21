package com.kstruct.markdown.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SimpleFile extends NavigationNode {
    
    public SimpleFile(Path path, Path root, Optional<NavigationNode> parent) {
        super(path, root, parent);
    }

    public List<NavigationNode> getChildren() {
        // No children for a file
        return new ArrayList<NavigationNode>();
    }
}
