package com.kstruct.markdown.utils;

import java.nio.file.Path;
import java.util.regex.Pattern;

import com.kstruct.markdown.model.MarkdownPage;

public class MarkdownUtils {
    public static final String MARKDOWN_FILE_EXTENSION = ".md";

    public static final String HTML_OUTPUT_FILE_EXTENSION = ".html";

    public static final String DIRECTORY_INDEX_FILE_NAME = "index";

    public static boolean isMarkdownPage(Path p) {
        return p.getFileName().toString().endsWith(MARKDOWN_FILE_EXTENSION);
    }

    public static boolean isMarkdownIndexPage(Path p) {
        return p.getFileName().toString().endsWith(DIRECTORY_INDEX_FILE_NAME + MARKDOWN_FILE_EXTENSION);
    }

    public static Path renamePathForMarkdownPage(Path path) {
        String filename = path.getFileName().toString();
        Path result = path.resolveSibling(renameFilenameForMarkdownPage(filename));
        return result;
    }

    public static String renameFilenameForMarkdownPage(String filename) {
        return filename.replaceAll(Pattern.quote(MARKDOWN_FILE_EXTENSION) + "(#|$)", HTML_OUTPUT_FILE_EXTENSION + "$1");
    }

}
