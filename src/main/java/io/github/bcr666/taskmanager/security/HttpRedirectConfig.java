package io.github.bcr666.taskmanager.security;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.catalina.connector.Connector;

@Configuration
public class HttpRedirectConfig {
	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		Connector httpConnector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		httpConnector.setScheme("http");
		httpConnector.setPort(8080);
		httpConnector.setSecure(false);
		httpConnector.setRedirectPort(8443);
		factory.addAdditionalTomcatConnectors(httpConnector);
		return factory;
	}
}