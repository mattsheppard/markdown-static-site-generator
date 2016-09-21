package com.kstruct.markdown.templating;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class MarkdownProcessorResult {
	private final String renderedContent;
	
	private final Set<String> linkTargets = new HashSet<>();
}
