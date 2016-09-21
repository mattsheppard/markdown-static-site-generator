package com.kstruct.markdown.utils;

import java.nio.file.Path;
import java.util.regex.Pattern;

import com.kstruct.markdown.model.MarkdownPage;

public class Markdown {
	public static final String MARKDOWN_FILE_EXTENSION = ".md";

	public static final String HTML_OUTPUT_FILE_EXTENSION = ".html";

	public static boolean isMarkdownPage(Path p) {
		return p.getFileName().toString().endsWith(MARKDOWN_FILE_EXTENSION);
	}

	public static String titleForMarkdownFile(Path p) {
		String title = p.getFileName().toString().replaceAll(Pattern.quote(MARKDOWN_FILE_EXTENSION) + "$", "");
		title = title.replace("-", " ");
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

	public static Path renamePathForMarkdownPage(Path path) {
		String filename = path.getFileName().toString();
		Path result = path.resolveSibling(renameFilenameForMarkdownPage(filename));
		return result;
	}

	public static String renameFilenameForMarkdownPage(String filename) {
		return filename.replaceAll(Pattern.quote(MARKDOWN_FILE_EXTENSION) + "$", HTML_OUTPUT_FILE_EXTENSION);
	}

}
