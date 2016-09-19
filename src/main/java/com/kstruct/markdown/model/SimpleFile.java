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

public class SimpleFile extends FileNode {
    
    public SimpleFile(Path path, Path root, Optional<SiteModelNode> parent) {
        super(path, root, parent);
    }

    public List<SiteModelNode> getChildren() {
        // No children for a file
        return new ArrayList<SiteModelNode>();
    }

    @Override
    public Path getTargetPath(Path targetRoot) {
        return targetRoot.resolve(getRelativeSourcePath());
    }

    @Override
    public Optional<byte[]> getOutputContent() {
        try {
            return Optional.of(Files.readAllBytes(getRelativeSourcePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
