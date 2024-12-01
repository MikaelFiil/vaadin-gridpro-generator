package com.flowingcode.vaadin.addons.simpletimer;

//Copyright (C) 2019 - 2020 Flowing Code
//Licensed under the Apache License, Version 2.0

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.math.BigDecimal;

public class SimpleTimerDemo extends Div {

    private final SimpleTimer timer = new SimpleTimer();

    private boolean countUpMode;
    private BigDecimal time = new BigDecimal(60);

    public SimpleTimerDemo() {
        setSizeFull();
        timer.setWidth("100px");
        timer.setHeight("50px");
        timer.getStyle().set("font-size", "40px");
        timer.setStartTime(60);

        final Span timerTitle = new Span("Simple Count Up Timer");

        final TextField startTime =
                new TextField("Start Time", e -> {
                    try {
                        time = new BigDecimal(e.getValue());
                        update();
                    } catch (final Exception ex) {
                        Notification.show("Please enter a number");
                    }
                });
        final Checkbox countUp = new Checkbox("Count Up", false);
        countUp.addValueChangeListener(
                e -> {
                    countUpMode = countUp.getValue();
                    if (countUpMode) {
                        startTime.setLabel("End Time");
                        timerTitle.setText("Simple Count Up Timer");
                    } else {
                        startTime.setLabel("Start Time");
                        timerTitle.setText("Simple Countdown Timer");
                    }
                    update();
                });
        final Button start = new Button("Start/Stop", e -> timer.start());
        final Button stop = new Button("Stop", e -> timer.pause());
        final Button reset =
                new Button(
                        "Reset",
                        e -> {
                            timer.reset();
                        });
        final Button running =
                new Button(
                        "Current Time",
                        e ->
                                timer
                                        .getCurrentTimeAsync()
                                        .thenAccept(
                                                time ->
                                                        Notification.show(
                                                                time.toPlainString()
                                                                        + (timer.isRunning() ? "" : " (Not Running)"))));
        final Checkbox fractions = new Checkbox("Fractions", true);
        fractions.addValueChangeListener(e -> timer.setFractions(e.getValue()));
        final Checkbox minutes = new Checkbox("Minutes", e -> timer.setMinutes(e.getValue()));
        final Checkbox hours = new Checkbox("Hours", e -> timer.setHours(e.getValue()));
        final Checkbox doubleDigitHours =
                new Checkbox("Double digit hours", e -> timer.setDoubleDigitHours(e.getValue()));
        final Checkbox visible =
                new Checkbox(
                        "Visible",
                        e -> {
                            if (e.isFromClient()) {
                                timer.setVisible(!timer.isVisible());
                            }
                        });
        visible.setValue(true);

        timer.addTimerEndEvent(e -> Notification.show("Timer Ended"));

        final HorizontalLayout topLayout = new HorizontalLayout(timerTitle, timer);
        topLayout.setAlignItems(Alignment.CENTER);

        final HorizontalLayout options =
                new HorizontalLayout(countUp, fractions, minutes, hours, visible, doubleDigitHours);
        options.setAlignItems(Alignment.CENTER);
        options.getStyle().set("flex-wrap", "wrap");

        final HorizontalLayout bottomLayout = new HorizontalLayout(start, stop, reset, running);
        bottomLayout.setAlignItems(Alignment.BASELINE);

        add(new VerticalLayout(topLayout, startTime, options, bottomLayout));
    }

    private void update() {
        if (countUpMode) {
            timer.setEndTime(time);
        } else {
            timer.setStartTime(time);
        }
    }
}

