package dk.netbizz.vaadin.config.domain;

import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public dk.netbizz.vaadin.signal.domain.SignalHost signalHost() {
        return new dk.netbizz.vaadin.signal.domain.SignalHost();
    }

}
