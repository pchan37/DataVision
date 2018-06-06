package vision.lib.ui.dialog;

import vision.lib.exceptions.numericalexception.MustBeIntegerException;
import vision.lib.exceptions.numericalexception.MustBeNonNegativeException;
import vision.lib.exceptions.numericalexception.MustBeNonZeroException;
import vision.lib.exceptions.numericalexception.NotInRangeException;
import javafx.scene.control.TextInputControl;

/**
 * A class that provides methods for validating a {@link TextInputControl}
 */
public class TextInputControlValidator {

    /**
     * Validates that the given {@link TextInputControl} does not contain a negative value as input
     *
     * @param input The {@link TextInputControl} containing the input to be validated
     * @throws MustBeNonNegativeException When the input contains a negative value as input
     */
    public static void validateInputIsNonNegative(TextInputControl input) throws MustBeNonNegativeException {
        if (input.getText().startsWith("-")) {
            throw new MustBeNonNegativeException();
        }
    }

    /**
     * Validates that the given {@link TextInputControl} does not contain a zero value as input
     *
     * @param input The {@link TextInputControl} containing the input to be validated
     * @throws MustBeNonZeroException When the input contains a zero value as input
     */
    public static void validateInputIsNonZero(TextInputControl input) throws MustBeNonZeroException {
        if (input.getText().equals("0")) {
            throw new MustBeNonZeroException();
        }
    }

    /**
     * Validates that the given {@link TextInputControl} does not contain non-integer value as input
     *
     * @param input The {@link TextInputControl} containing the input to be validated
     * @throws MustBeIntegerException When the input contains a non-integer value as input
     */
    public static void validateInputIsInteger(TextInputControl input) throws MustBeIntegerException {
        try {
            Integer.parseInt(input.getText());
        } catch (NumberFormatException e) {
            throw new MustBeIntegerException();
        }
    }

    /**
     * Validates that the given {@link TextInputControl} contains a value that is between two ints as input
     *
     * @param input The {@link TextInputControl} containing the input to be validated
     * @param minValue The minimum integer that is valid
     * @param maxValue The maximum integer that is valid
     * @throws MustBeIntegerException When the input contains a non-integer value as input
     * @throws NotInRangeException When the input is not within the specified range
     */
    public static void validateInputIsWithinRange(TextInputControl input, int minValue, int maxValue) throws MustBeIntegerException, NotInRangeException {
        validateInputIsInteger(input);
        if (Integer.parseInt(input.getText()) < minValue) {
            throw new NotInRangeException(NotInRangeException.ErrorValue.BELOW_MINIMUM_VALUE);
        }

        if (Integer.parseInt(input.getText()) > maxValue) {
            throw new NotInRangeException(NotInRangeException.ErrorValue.ABOVE_MAXIMUM_VALUE);
        }
    }

    /**
     * Validates that the given {@link TextInputControl} contains a positive integer as input
     *
     * @param input The {@link TextInputControl} containing the input to be validated
     * @throws MustBeIntegerException When the input contains a non-integer value as input
     * @throws MustBeNonNegativeException When the input contains a negative value as input
     * @throws MustBeNonZeroException When the input contains a zero value as input
     */
    public static void validateInputIsPositiveInteger(TextInputControl input) throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        validateInputIsInteger(input);
        validateInputIsNonNegative(input);
        validateInputIsNonZero(input);
    }

    /**
     * Validates that the given {@link TextInputControl} contains a positive integer that is between a specified range
     * as input
     *
     * @param input The {@link TextInputControl} containing the input to be validated
     * @param minValue The minimum integer that is valid
     * @param maxValue The maximum integer that is valid
     * @throws MustBeIntegerException When the input contains a non-integer value as input
     * @throws MustBeNonNegativeException When the input contains a negative value as input
     * @throws MustBeNonZeroException When the input contains a zero value as input
     * @throws NotInRangeException When the input is not within the specified range
     */
    public static void validateInputIsPositiveIntegerAndWithinRange(TextInputControl input, int minValue, int maxValue) throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException, NotInRangeException {
        validateInputIsPositiveInteger(input);
        validateInputIsWithinRange(input, minValue, maxValue);
    }

}
