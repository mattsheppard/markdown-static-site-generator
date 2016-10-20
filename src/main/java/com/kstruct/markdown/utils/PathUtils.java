package com.kstruct.markdown.utils;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class PathUtils {

    public static String titleForPath(Path p, Path inputRoot) {
        if (MarkdownUtils.isMarkdownIndexPage(p)) {
        		// Index pages are special - we title them after the directory they're in
        		p = p.getParent();
        		if (p.equals(inputRoot)) {
        		    // The top level page is even special-er - it has no title
        		    // because we don't want it to just have the enclosing directory name.
        		    return "";
        		}
        }
        String title = p.getFileName().toString().replaceAll(Pattern.quote(MarkdownUtils.MARKDOWN_FILE_EXTENSION) + "$", "");
        title = title.replace("-", " ");
        title = title.replace("_", " ");
        title = toTitleCase(title);
        return title;
    }

    private static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

}
