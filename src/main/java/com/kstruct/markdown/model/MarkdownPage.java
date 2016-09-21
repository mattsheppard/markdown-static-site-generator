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
import com.kstruct.markdown.utils.Markdown;

import lombok.Getter;
import lombok.Setter;

public class MarkdownPage extends NavigationNode {

	public MarkdownPage(Path path, Path root, Optional<NavigationNode> parent) {
		super(path, root, parent);
	}

	@Override
	public String getTitle() {
		return Markdown.titleForMarkdownFile(this.getRelativePath());
	}

	public String getOutputPath() {
		return Markdown.renamePathForMarkdownPage(this.getRelativePath()).toString();
	}

	public List<NavigationNode> getChildren() {
		// No children for a file
		return new ArrayList<NavigationNode>();
	}
}
