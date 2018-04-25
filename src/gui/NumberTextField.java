package gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Textfield implementation that accepts formatted number and stores them in a
 * BigDecimal property The user input is formatted when the focus is lost or the
 * user hits RETURN.
 *
 * @author Thomas Bolz
 */
public class NumberTextField extends TextField {

    private final NumberFormat nf;
    private ObjectProperty<BigDecimal> number = new SimpleObjectProperty<>();
    BigDecimal min, max;

    public final BigDecimal getNumber() { return number.get(); }
    public final long getNumberAsLong() { return number.get().longValue(); }
    public final int getNumberAsInt() { return number.get().intValue(); }
    public final double getNumberAsDouble() { return number.get().doubleValue(); }

    public final void setNumber(int value) { setNumber(BigDecimal.valueOf(value)); }
    public final void setNumber(double value) { setNumber(BigDecimal.valueOf(value)); }
    public final void setNumber(long value) { setNumber(BigDecimal.valueOf(value)); }
    public final void setNumber(BigDecimal value) {
        setAlignment(Pos.BASELINE_RIGHT);
        number.set(value);
    }

    public ObjectProperty<BigDecimal> numberProperty() {
        return number;
    }

    public NumberTextField(int value, int min, int max) {
        this(BigDecimal.valueOf(value), BigDecimal.valueOf(min), BigDecimal.valueOf(max));
    }
    public NumberTextField(double value, double min, double max) {
        this(BigDecimal.valueOf(value), BigDecimal.valueOf(min), BigDecimal.valueOf(max));
    }
    public NumberTextField(long value, long min, long max) {
        this(BigDecimal.valueOf(value), BigDecimal.valueOf(min), BigDecimal.valueOf(max));
    }
    public NumberTextField(BigDecimal value, BigDecimal min, BigDecimal max) {
        this(value, NumberFormat.getInstance());
        this.min = min;
        this.max = max;
        initHandlers();
    }

    public NumberTextField(BigDecimal value, NumberFormat nf) {
        super();
        this.nf = nf;
        initHandlers();
        setNumber(value);
    }

    private void initHandlers() {
        // try to parse when focus is lost or RETURN is hit
        setOnAction(arg0 -> parseAndFormatInput());

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) parseAndFormatInput();
        });

        // Set text in field if BigDecimal property is changed from outside.
        numberProperty().addListener((obserable, oldValue, newValue) -> setText(nf.format(newValue)));
    }

    /**
     * Tries to parse the user input to a number according to the provided
     * NumberFormat
     */
    private void parseAndFormatInput() {
        try {
            String input = getText();
            if (input == null || input.length() == 0) {
                return;
            }
            Number parsedNumber = nf.parse(input);
            BigDecimal newValue = new BigDecimal(parsedNumber.toString());
            if(newValue.compareTo(min) < 0 || newValue.compareTo(max) > 0)
                return;
            setNumber(newValue);
            selectAll();
        } catch (ParseException ex) {
            // If parsing fails keep old number
            setText(nf.format(number.get()));
        }
    }
}