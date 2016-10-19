package com.kstruct.markdown.model;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

public class Directory extends NavigationNode {
    public Directory(Path path, Path root, Optional<NavigationNode> parent) {
        super(path, root, parent);
    }

    @Getter
    @Setter
    private List<NavigationNode> children = new ArrayList<NavigationNode>();

    public String getOutputPath() {
        return this.getRelativePath().resolve("index.html").toString();
    }

    @Override
    public Boolean isLeafHtmlPage() {
        return false;
    }

}
