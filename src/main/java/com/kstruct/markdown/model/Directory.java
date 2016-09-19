package com.kstruct.markdown.model;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

public class Directory extends SiteModelNode {
    public Directory(Path path, Path root, Optional<SiteModelNode> parent) {
        super(path, root, parent);
    }

    @Getter
    @Setter
    private List<SiteModelNode> children;

    @Override
    public Path getTargetPath(Path targetRoot) {
        return targetRoot.resolve(getRelativeSourcePath());
        // Maybe we'll want this to return index.md?
    }

    @Override
    public Optional<byte[]> getOutputContent() {
        return Optional.empty();
        // Maybe we'll want ot generate index pages if they're absent
    }
}
