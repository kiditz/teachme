package com.slerpio.teachme.api;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Component
public class TemplateConfig {
	public Template getTemplate(String filename) throws IOException {
		Configuration config = new Configuration();
		config.setClassForTemplateLoading(TemplateConfig.class, "/templates/");
		config.setDefaultEncoding(StandardCharsets.UTF_8.toString());
		return config.getTemplate(filename);
	}

	Logger log = LoggerFactory.getLogger(getClass());

	public String process(Domain object, String filename) {
		Template template;
		StringWriter out = new StringWriter();
		try {
			template = getTemplate(filename);
			template.process(object, out);
		} catch (Exception e) {
			log.error("Exception : {}", e);
		}
		return out.toString();
	}
}
