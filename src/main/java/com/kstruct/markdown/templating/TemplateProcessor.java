package com.kstruct.markdown.templating;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.kstruct.markdown.freemarker.utils.NioTemplateLoader;
import com.kstruct.markdown.model.SiteModelNode;

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
	
	public TemplateProcessor(Path templatePath, SiteModelNode navigationRoot, String siteName, Map<String, Object> extraConfig) {
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

	public String template(String content) {
		StringWriter result = new StringWriter();
		
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("content", content);
		dataModel.put("siteName", siteName);
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
