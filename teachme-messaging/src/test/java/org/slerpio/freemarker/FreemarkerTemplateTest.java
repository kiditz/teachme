package org.slerpio.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.slerp.core.Domain;
import com.slerpio.teachme.api.TemplateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * The test class to call freemarker
 * 
 * @author kiditz
 * @since Saturday 16 September 2017
 */
public class FreemarkerTemplateTest {
	static Logger log = LoggerFactory.getLogger(FreemarkerTemplateTest.class);

	@Test
	public void testFreemarker() throws IOException, TemplateException {
		Configuration config = new Configuration();
		config.setClassForTemplateLoading(this.getClass(), "/templates/");
		config.setDefaultEncoding(StandardCharsets.UTF_8.toString());
		config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		Template template = config.getTemplate("registration.html");
		StringWriter writer = new StringWriter();
		Domain userDomain = new Domain();
		userDomain.put("username", "kiditz_ganteng");
		userDomain.put("activationCode", "0ceb2e220");
		template.process(userDomain, writer);
		log.info("testFreemarker {}", writer.toString());
		writer.close();
	}
	@Test
	public void testTemplateConfig() throws IOException, TemplateException {
		Template template = new TemplateConfig().getTemplate("registration.html");
		StringWriter writer = new StringWriter();
		Domain userDomain = new Domain();
		userDomain.put("username", "kiditz_ganteng");
		userDomain.put("activationCode", "0ceb2e220");
		template.process(userDomain, writer);
		log.info("testTemplateConfig {}", writer.toString());
	}
}
