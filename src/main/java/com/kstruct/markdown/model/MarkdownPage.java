package com.kstruct.markdown.model;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.kstruct.markdown.steps.ProcessAndWriteSingleMarkdownPage;
import com.kstruct.markdown.templating.MarkdownRenderer;
import com.kstruct.markdown.templating.TemplateProcessor;

import lombok.Getter;
import lombok.Setter;

public class MarkdownPage extends NavigationNode {

    public static final String MARKDOWN_FILE_EXTENSION = ".md";

    public static final String HTML_OUTPUT_FILE_EXTENSION = ".html";

    public MarkdownPage(Path path, Path root, Optional<NavigationNode> parent) {
        super(path, root, parent);
    }

    @Override
    public String getTitle() {
        String title = this.getRelativePath().getFileName().toString().replaceAll(Pattern.quote(MarkdownPage.MARKDOWN_FILE_EXTENSION) + "$", "").replace("-", " ");
        title = ProcessAndWriteSingleMarkdownPage.toTitleCase(title);
        return title;
    }

    public String getOutputPath() {
        return this.getRelativePath().toString().replaceAll(Pattern.quote(MarkdownPage.MARKDOWN_FILE_EXTENSION) + "$", MarkdownPage.HTML_OUTPUT_FILE_EXTENSION);
    }

    public List<NavigationNode> getChildren() {
        // No children for a file
        return new ArrayList<NavigationNode>();
    }
}
