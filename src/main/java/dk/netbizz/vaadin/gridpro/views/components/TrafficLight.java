package dk.netbizz.vaadin.gridpro.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import dk.netbizz.vaadin.gridpro.themes.RadioButtonTheme;

import java.util.Arrays;
import java.util.List;

// https://codepen.io/jupre/pen/GRRmgrx

public class TrafficLight extends Component {

    // Select if Green should be Low or High
    public static final List<String> TRAFFICLIGHT_NORMAL = Arrays.asList("Low", "Medium", "High");
    public static final List<String> TRAFFICLIGHT_REVERSE = Arrays.asList("High", "Medium", "Low");

    public static RadioButtonGroup<String> createRadioButtonTrafficlight() {
        return createRadioButtonGroup("", TRAFFICLIGHT_NORMAL, RadioButtonTheme.TRAFFICLIGHT);
    }


    public static RadioButtonGroup<String> createRadioButtonGroup(String label, List<String> greenYellowRedValues, String... themeNames) {
        RadioButtonGroup<String> group = new RadioButtonGroup(label);
        group.addThemeNames(themeNames);
        group.setItems(greenYellowRedValues);

        group.getChildren().forEach(component -> {
            for (String themeName : themeNames) {
                component.getElement().getThemeList().add(themeName);
            }
        });

        List<Component> rbComponents = group.getChildren().toList();
        Component rbGreen = rbComponents.getFirst();
        rbGreen.getElement().getThemeList().add(RadioButtonTheme.TRAFFICLIGHT_GREEN);
        rbGreen.getElement().setProperty("value", "Green");

        Component rbYellow = rbComponents.get(1);
        rbYellow.getElement().getThemeList().add(RadioButtonTheme.TRAFFICLIGHT_YELLOW);
        rbYellow.getElement().setProperty("value", "Yellow");

        Component rbRed = rbComponents.get(2);
        rbRed.getElement().getThemeList().add(RadioButtonTheme.TRAFFICLIGHT_RED);
        rbRed.getElement().setProperty("value", "Red");

        return group;
    }

}
