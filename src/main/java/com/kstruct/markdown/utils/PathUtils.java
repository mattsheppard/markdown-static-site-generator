package com.kstruct.markdown.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import org.commonmark.node.Visitor;

import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.MarkdownProcessorResult;

public class PathUtils {

    public static String titleForPath(Path p, Path inputRoot) {
        Path markdownFile = p;
        if (Files.isDirectory(markdownFile)) {
            // If a directory has an index.md, we'll give it the metadata title from that index
            markdownFile = markdownFile.resolve("index.md");
        }

        if (Files.exists(markdownFile)) {
			// See if we can read some title metadata from it in preference to a generated title
			try {
				MarkdownProcessorResult result = new MarkdownProcessor().process(new String(Files.readAllBytes(markdownFile), StandardCharsets.UTF_8), Arrays.asList(new Visitor[]{}));
				if (result.getMetadata().containsKey("title")) {
					Optional<String> metadataTitle = result.getMetadata().get("title").stream().findFirst();
					if (metadataTitle.isPresent()) {
						return metadataTitle.get();
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        }
		
        // No metadata title - We'll try to infer one from the file system location
        
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
