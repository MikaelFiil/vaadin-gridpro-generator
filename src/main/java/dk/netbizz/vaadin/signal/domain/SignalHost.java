package dk.netbizz.vaadin.signal.domain;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import com.vaadin.signals.SignalFactory;
import com.vaadin.signals.ValueSignal;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@VaadinSessionScope
@Service
public class SignalHost {

    public static final String COMPANY_ID = "companyId";
    public static final String DEPARTMENT_ID = "departmentId";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String ITEM_ID = "itemId";

    private Map<String, ValueSignal<Integer>> signalMap = Collections.synchronizedMap(new HashMap<>());

    public SignalHost() {
        addSignal(COMPANY_ID, new ValueSignal<>(Integer.class));
        addSignal(DEPARTMENT_ID, new ValueSignal<>(Integer.class));
        addSignal(EMPLOYEE_ID, new ValueSignal<>(Integer.class));
        addSignal(ITEM_ID, new ValueSignal<>(Integer.class));
    }

    public void addSignal(String name, ValueSignal<Integer> signal) {
        signalMap.put(name, signal);
    }

    public ValueSignal<Integer> getSignal(String key) {
        return signalMap.get(key);
    }

}
