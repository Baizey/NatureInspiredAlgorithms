package gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

class NumberTextField extends TextField {

    private final ObjectProperty<BigDecimal> number = new SimpleObjectProperty<>();
    private final BigDecimal min, max;
    private final boolean useDecimals;

    NumberTextField(int value, int min, int max) {
        this(BigDecimal.valueOf(value), BigDecimal.valueOf(min), BigDecimal.valueOf(max), false);
    }

    NumberTextField(double value, double min, double max) {
        this(BigDecimal.valueOf(value), BigDecimal.valueOf(min), BigDecimal.valueOf(max), true);
    }

    private NumberTextField(BigDecimal value, BigDecimal min, BigDecimal max, boolean useDecimals) {
        super();
        initHandlers();
        this.useDecimals = useDecimals;
        number.set(value);
        setText(value);
        this.min = min;
        this.max = max;
        setAlignment(Pos.BASELINE_RIGHT);
        initHandlers();
    }

    int asInt() {
        return number.get().intValue();
    }

    double asDouble() {
        return number.get().doubleValue();
    }

    private void initHandlers() {
        setOnAction(arg0 -> parseAndFormatInput());
        focusedProperty().addListener((observable, oldValue, newValue) -> parseAndFormatInput());
    }

    private void parseAndFormatInput() {
        String text = getText();

        // Parse string, if invalid use 0
        BigDecimal value;
        if (text == null || (text = text.replaceAll(",", "")).length() == 0)
            text = "0";
        try { value = new BigDecimal(text); } catch(Exception ignored) { value = BigDecimal.ZERO; }

        // Numbers above/below limits set to limits
        if (value.compareTo(min) < 0)
            value = min;
        if (value.compareTo(max) > 0)
            value = max;

        // If supposed to be integer, round down
        if (!useDecimals)
            value = BigDecimal.valueOf(value.intValue());
        number.set(value);
        setText(value);
        selectAll();
    }

    private void setText(BigDecimal value) {
        String[] parts = value.toPlainString().split("\\.");
        parts[0] = new StringBuilder(
                String.join(",", new StringBuilder(parts[0]).reverse().toString().split("(?<=\\G.{3})")))
                .reverse().toString();
        setText(String.join(".", parts));
    }

}