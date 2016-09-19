package com.kstruct.markdown.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class FileNode extends SiteModelNode {

    public FileNode(Path path, Path root, Optional<SiteModelNode> parent) {
        super(path, root, parent);
    }

    @Override
    public List<SiteModelNode> getChildren() {
        // Empty list, because Files don't have children
        return new ArrayList<SiteModelNode>();
    }

}
