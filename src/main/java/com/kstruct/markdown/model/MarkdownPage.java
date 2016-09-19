package com.kstruct.markdown.model;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.kstruct.markdown.templating.TemplateProcessor;

import lombok.Getter;
import lombok.Setter;

public class MarkdownPage extends FileNode {

    public static final String MARKDOWN_FILE_EXTENSION = ".md";

    public static final String HTML_OUTPUT_FILE_EXTENSION = ".html";

    @Setter
    private static TemplateProcessor templateProcessor;
    
    public MarkdownPage(Path path, Path root, Optional<SiteModelNode> parent) {
        super(path, root, parent);
    }

    public List<SiteModelNode> getChildren() {
        // No children for a file
        return new ArrayList<SiteModelNode>();
    }

    @Override
    public Path getTargetPath(Path targetRoot) {
        String relativeTargetPath = getRelativeSourcePath().toString().replaceAll(Pattern.quote(MARKDOWN_FILE_EXTENSION) + "$", HTML_OUTPUT_FILE_EXTENSION);
        return targetRoot.resolve(relativeTargetPath);
    }

    @Override
    public Optional<byte[]> getOutputContent() {
        // TODO Auto-generated method stub
        return null;
    }

}
