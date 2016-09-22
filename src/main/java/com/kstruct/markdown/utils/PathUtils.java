package com.kstruct.markdown.utils;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class PathUtils {

    public static String titleForPath(Path p) {
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
