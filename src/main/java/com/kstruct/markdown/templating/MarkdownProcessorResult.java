package com.kstruct.markdown.templating;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kstruct.markdown.model.TocEntry;
import com.kstruct.markdown.model.TocTree;
import com.kstruct.markdown.utils.MarkdownTocGenerator;

import lombok.Data;

@Data
public class MarkdownProcessorResult {
	private final String renderedContent;

	private final TocTree toc;

	private final Map<String,List<String>> metadata;

	private final Set<String> linkTargets = new HashSet<>();
}
