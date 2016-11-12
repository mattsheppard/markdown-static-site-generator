package com.kstruct.markdown.templating;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kstruct.markdown.freemarker.utils.NioTemplateLoader;
import com.kstruct.markdown.model.NavigationNode;
import com.kstruct.markdown.model.TocEntry;
import com.kstruct.markdown.model.TocTree;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.Version;

public class TemplateProcessor {

	private String siteName;
	private Map<String, Object> extraConfig;
	private Template template;
    private NavigationNode navigationRoot;
	
	public TemplateProcessor(Path templatePath, NavigationNode navigationRoot, String siteName, Map<String, Object> extraConfig) {
        this.navigationRoot = navigationRoot;
		this.siteName = siteName;
		this.extraConfig = extraConfig;
		
		try {
			Configuration cfg = new Configuration(new Version("2.3.25"));
			cfg.setTemplateLoader(new NioTemplateLoader(templatePath.getParent()));
			template = cfg.getTemplate(templatePath.getFileName().toString());
		} catch (TemplateNotFoundException e) {
			throw new RuntimeException(e);
		} catch (MalformedTemplateNameException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String template(String content, String title, TocTree toc, Map<String, List<String>> metadata, String relativeUri, String relativeRootUri) {
		StringWriter result = new StringWriter();
		
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("content", content);
        dataModel.put("title", title);
        dataModel.put("toc", toc);
        dataModel.put("metadata", metadata);
        dataModel.put("relativeUri", relativeUri);
		dataModel.put("relativeRootUri", relativeRootUri);
        dataModel.put("siteName", siteName);
        dataModel.put("navigationRoot", navigationRoot);
        dataModel.put("currentNavigationNode", navigationRoot.findNodeFor(relativeUri).orElse(null));
		dataModel.put("extraConfig", extraConfig);
		
		try {
			template.process(dataModel, result);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return result.toString();
	}

}
