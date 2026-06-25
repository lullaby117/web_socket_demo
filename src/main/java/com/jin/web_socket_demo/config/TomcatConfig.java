//package com.jin.web_socket_demo.config;
//
//import org.apache.coyote.http11.Http11NioProtocol;
//import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class TomcatConfig {
//
//    @Bean
//    public TomcatServletWebServerFactory webServerFactory() {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//
//        factory.addConnectorCustomizers(connector -> {
//            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//            protocol.setMaxThreads(200);
//            protocol.setMinSpareThreads(10);
//            protocol.setMaxConnections(10000);
//            protocol.setAcceptCount(100);
//            protocol.setConnectionTimeout(20000);
//        });
//        return factory;
//    }
//}
