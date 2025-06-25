package dk.netbizz.vaadin.signal.domain;


import com.vaadin.signals.ValueSignal;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class SignalHost {

    private static dk.netbizz.vaadin.signal.domain.SignalHost instance;
    private Map<String, ValueSignal<Integer>> signalMap = new HashMap<>();

    private SignalHost() {
    }

    @Bean
    public static synchronized SignalHost signalHostInstance() {
        if (instance == null) {
            instance = new SignalHost();
        }
        return instance;
    }

    public void addSignal(String name, ValueSignal<Integer> signal) {
        signalMap.put(name, signal);
    }

    public ValueSignal<Integer> getSignal(String key) {
        return signalMap.get(key);
    }

}
