package datavision.algorithms.algorithmconfig;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import vision.lib.exceptions.numericalexception.MustBeIntegerException;
import vision.lib.exceptions.numericalexception.MustBeNonNegativeException;
import vision.lib.exceptions.numericalexception.MustBeNonZeroException;
import vision.lib.ui.dialog.TextInputControlValidator;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * A test suite for {@link ClassifierConfigurationDialog} to ensure that configuration values are being
 * properly validated.
 *
 * Note: Utilizes JavaFXThreadingRule from Andy Till to allow the instantiation of JavaFX components
 *
 * @author Patrick Chan
 */
public class ClassifierConfigurationDialogTest {

    @Rule public JavaFXThreadingRule javaFXThreadingRule = new JavaFXThreadingRule();

    /**
     * Tests that {@link ClassifierConfigurationDialog} is able to accept boundary values.
     *
     * The chosen boundary value for {@link ClassifierConfigurationDialog#maxIteration} is 1
     * because it has to be a positive integer and 1 is the smallest positive integer.  The
     * chosen boundary value for {@link ClassifierConfigurationDialog#updateInterval} is 1
     * because it has to be a positive integer and 1 is the smallest positive integer.  The
     * chosen boundary value for {@link ClassifierConfigurationDialog#runContinuously} is
     * unselected because it defaults to unselected.
     */
    @Test
    public void testBoundaryValues() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField maxIterationInput = new TextField("1");
        TextField updateIntervalInput = new TextField("1");
        CheckBox runContinuouslyInput = new CheckBox();
        TextInputControlValidator.validateInputIsPositiveInteger(maxIterationInput);
        TextInputControlValidator.validateInputIsPositiveInteger(updateIntervalInput);
        assertTrue(!runContinuouslyInput.isSelected());
    }

    /**
     * Tests that {@link ClassifierConfigurationDialog#maxIteration} must be an integer
     */
    @Test(expected = MustBeIntegerException.class)
    public void testMaxIterationsShouldBeInteger() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField maxIterationsInput = new TextField("a");
        TextInputControlValidator.validateInputIsPositiveInteger(maxIterationsInput);
    }

    /**
     * Tests that {@link ClassifierConfigurationDialog#maxIteration} cannot be negative
     */
    @Test(expected = MustBeNonNegativeException.class)
    public void testMaxIterationsCannotBeNegative() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField maxIterationsInput = new TextField("-3");
        TextInputControlValidator.validateInputIsPositiveInteger(maxIterationsInput);
    }

    /**
     * Tests that {@link ClassifierConfigurationDialog#maxIteration} cannot be zero
     */
    @Test(expected = MustBeNonZeroException.class)
    public void testMaxIterationCannotBeZero() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField maxIterationsInput = new TextField("0");
        TextInputControlValidator.validateInputIsPositiveInteger(maxIterationsInput);
    }

    /**
     * Tests that {@link ClassifierConfigurationDialog#updateInterval} must be an integer
     */
    @Test(expected = MustBeIntegerException.class)
    public void testUpdateIntervalShouldBeInteger() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField updateIntervalInput = new TextField("a");
        TextInputControlValidator.validateInputIsPositiveInteger(updateIntervalInput);
    }

    /**
     * Tests that {@link ClassifierConfigurationDialog#updateInterval} cannot be negative
     */
    @Test(expected = MustBeNonNegativeException.class)
    public void testUpdateIntervalCannotBeNegative() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField updateIntervalInput = new TextField("-3");
        TextInputControlValidator.validateInputIsPositiveInteger(updateIntervalInput);
    }

    /**
     * Tests that {@link ClassifierConfigurationDialog#updateInterval} cannot be zero
     */
    @Test(expected = MustBeNonZeroException.class)
    public void testUpdateIntervalCannotBeZero() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField updateIntervalInput = new TextField("0");
        TextInputControlValidator.validateInputIsPositiveInteger(updateIntervalInput);
    }

    /**
     * Tests that {@link ClassifierConfigurationDialog#runContinuously} can be true
     */
    @Test
    public void testContinuousRunIsChecked() {
        CheckBox continuousRunInput = new CheckBox();
        continuousRunInput.setSelected(true);
        assertTrue(continuousRunInput.isSelected());
    }

    /**
     * Tests that {@link ClassifierConfigurationDialog#runContinuously} can be false
     */
    @Test
    public void testContinuousRunIsNotChecked() {
        CheckBox continuousRunInput = new CheckBox();
        continuousRunInput.setSelected(false);
        assertTrue(!continuousRunInput.isSelected());
    }

    /**
     * A JUnit {@link Rule} for running tests on the JavaFX thread and performing
     * JavaFX initialisation.  To include in your test case, add the following code:
     *
     * <pre>
     * {@literal @}Rule
     * public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();
     * </pre>
     *
     * @author Andy Till
     *
     */
    public class JavaFXThreadingRule implements TestRule {

        /**
         * Flag for setting up the JavaFX, we only need to do this once for all tests.
         */
        private boolean jfxIsSetup;

        @Override
        public Statement apply(Statement statement, Description description) {

            return new OnJFXThreadStatement(statement);
        }

        private class OnJFXThreadStatement extends Statement {

            private final Statement statement;

            public OnJFXThreadStatement(Statement aStatement) {
                statement = aStatement;
            }

            private Throwable rethrownException = null;

            @Override
            public void evaluate() throws Throwable {

                if(!jfxIsSetup) {
                    setupJavaFX();

                    jfxIsSetup = true;
                }

                final CountDownLatch countDownLatch = new CountDownLatch(1);

                Platform.runLater(() -> {
                    try {
                        statement.evaluate();
                    } catch (Throwable e) {
                        rethrownException = e;
                    }
                    countDownLatch.countDown();
                });

                countDownLatch.await();

                // if an exception was thrown by the statement during evaluation,
                // then re-throw it to fail the test
                if(rethrownException != null) {
                    throw rethrownException;
                }
            }

            protected void setupJavaFX() throws InterruptedException {

                long timeMillis = System.currentTimeMillis();

                final CountDownLatch latch = new CountDownLatch(1);

                SwingUtilities.invokeLater(() -> {
                    // initializes JavaFX environment
                    new JFXPanel();

                    latch.countDown();
                });

                System.out.println("javafx initialising...");
                latch.await();
                System.out.println("javafx is initialised in " + (System.currentTimeMillis() - timeMillis) + "ms");
            }

        }
    }


}