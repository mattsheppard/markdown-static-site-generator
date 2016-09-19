package com.kstruct.markdown.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

public abstract class SiteModelNode {
    
    @Getter
    private Optional<SiteModelNode> parent;

    @Getter
    private Path relativeSourcePath;

    @Getter
    private Path rootSourcePath;

    public SiteModelNode(Path path, Path root, Optional<SiteModelNode> parent) {
        this.parent = parent;
        
        this.relativeSourcePath = root.relativize(path);
        this.rootSourcePath = path.relativize(root);
        
        System.out.println("relativeSourcePath:" + relativeSourcePath);
        System.out.println("rootSourcePath:" + rootSourcePath);
    }
    
    public abstract List<SiteModelNode> getChildren();
    
    public abstract Optional<byte[]> getOutputContent();
    
    public abstract Path getTargetPath(Path targetRoot);
}
