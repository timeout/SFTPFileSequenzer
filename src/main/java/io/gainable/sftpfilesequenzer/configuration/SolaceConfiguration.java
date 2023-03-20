package io.gainable.sftpfilesequenzer.configuration;

import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolaceConfiguration {

    @Value("${solace.java.host}")
    private String solaceHost;

    @Value("${solace.java.msgVpn}")
    private String solaceMsgVpn;

    @Value("${solace.java.clientUsername}")
    private String solaceUsername;

    @Value("${solace.java.clientPassword}")
    private String solacePassword;

    @Bean
    public JCSMPFactory jcsmpFactory() {
        JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, solaceHost);
        properties.setProperty(JCSMPProperties.VPN_NAME, solaceMsgVpn);
        properties.setProperty(JCSMPProperties.USERNAME, solaceUsername);
        properties.setProperty(JCSMPProperties.PASSWORD, solacePassword);

        return JCSMPFactory.onlyInstance();
    }
}

