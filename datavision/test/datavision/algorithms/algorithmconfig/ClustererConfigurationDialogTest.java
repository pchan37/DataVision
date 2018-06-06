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
import vision.lib.exceptions.numericalexception.NotInRangeException;
import vision.lib.ui.dialog.TextInputControlValidator;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * A test suite for {@link ClustererConfigurationDialog} to ensure that configuration values are being
 * properly validated.
 *
 * Note: Utilizes JavaFXThreadingRule from Andy Till to allow the instantiation of JavaFX components
 *
 * @author Patrick Chan
 */
public class ClustererConfigurationDialogTest {

    @Rule public JavaFXThreadingRule javaFXThreadingRule = new JavaFXThreadingRule();

    /**
     * Tests that {@link ClustererConfigurationDialog} is able to accept boundary values.
     *
     * The chosen boundary value for {@link ClustererConfigurationDialog#maxIteration} is 1
     * because it has to be a positive integer and 1 is the smallest positive integer.  The
     * chosen boundary value for {@link ClustererConfigurationDialog#updateInterval} is 1
     * because it has to be a positive integer and 1 is the smallest positive integer.  The
     * chosen boundary value for {@link ClustererConfigurationDialog#runContinuously} is
     * unselected because it defaults to unselected.
     */
    @Test
    public void testBoundaryValues() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException, NotInRangeException {
        TextField maxIterationsInput = new TextField("1");
        TextField updateIntervalsInput = new TextField("1");
        TextField numOfClustersInput = new TextField("2");
        TextInputControlValidator.validateInputIsPositiveInteger(maxIterationsInput);
        TextInputControlValidator.validateInputIsPositiveInteger(updateIntervalsInput);
        TextInputControlValidator.validateInputIsPositiveIntegerAndWithinRange(numOfClustersInput, 2, 4);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#maxIteration} must be an integer
     */
    @Test(expected = MustBeIntegerException.class)
    public void testMaxIterationsShouldBeInteger() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField maxIterationsInput = new TextField("a");
        TextInputControlValidator.validateInputIsPositiveInteger(maxIterationsInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#maxIteration} cannot be negative
     */
    @Test(expected = MustBeNonNegativeException.class)
    public void testMaxIterationsCannotBeNegative() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField maxIterationsInput = new TextField("-3");
        TextInputControlValidator.validateInputIsPositiveInteger(maxIterationsInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#maxIteration} cannot be zero
     */
    @Test(expected = MustBeNonZeroException.class)
    public void testMaxIterationCannotBeZero() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField maxIterationsInput = new TextField("0");
        TextInputControlValidator.validateInputIsPositiveInteger(maxIterationsInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#updateInterval} must be an integer
     */
    @Test(expected = MustBeIntegerException.class)
    public void testUpdateIntervalShouldBeInteger() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField updateIntervalInput = new TextField("a");
        TextInputControlValidator.validateInputIsPositiveInteger(updateIntervalInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#updateInterval} cannot be negative
     */
    @Test(expected = MustBeNonNegativeException.class)
    public void testUpdateIntervalCannotBeNegative() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField updateIntervalInput = new TextField("-3");
        TextInputControlValidator.validateInputIsPositiveInteger(updateIntervalInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#updateInterval} cannot be zero
     */
    @Test(expected = MustBeNonZeroException.class)
    public void testUpdateIntervalCannotBeZero() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField updateIntervalInput = new TextField("0");
        TextInputControlValidator.validateInputIsPositiveInteger(updateIntervalInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#runContinuously} can be true
     */
    @Test
    public void testContinuousRunIsChecked() {
        CheckBox continuousRunInput = new CheckBox();
        continuousRunInput.setSelected(true);
        assertTrue(continuousRunInput.isSelected());
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#runContinuously} can be false
     */
    @Test
    public void testContinuousRunIsNotChecked() {
        CheckBox continuousRunInput = new CheckBox();
        continuousRunInput.setSelected(false);
        assertTrue(!continuousRunInput.isSelected());
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#numOfClusters} must be an integer
     */
    @Test(expected = MustBeIntegerException.class)
    public void testNumOfClustersShouldBeInteger() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField numOfClusterInput = new TextField("a");
        TextInputControlValidator.validateInputIsPositiveInteger(numOfClusterInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#numOfClusters} cannot be negative
     */
    @Test(expected = MustBeNonNegativeException.class)
    public void testNumOfClusterCannotBeNegative() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField numOfClusterInput = new TextField("-3");
        TextInputControlValidator.validateInputIsPositiveInteger(numOfClusterInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#numOfClusters} cannot be zero
     */
    @Test(expected = MustBeNonZeroException.class)
    public void testUNumOfClusterCannotBeZero() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException {
        TextField numOfClusterInput = new TextField("0");
        TextInputControlValidator.validateInputIsPositiveInteger(numOfClusterInput);
    }

    /**
     * Tests that {@link ClustererConfigurationDialog#numOfClusters} is between 2 and 4 (technically
     * should be min of 4 and num of data points)
     */
    @Test(expected = NotInRangeException.class)
    public void testUNumOfClusterMustBeInRange() throws MustBeIntegerException, MustBeNonNegativeException, MustBeNonZeroException, NotInRangeException {
        TextField numOfClusterInput = new TextField("5");
        TextInputControlValidator.validateInputIsPositiveIntegerAndWithinRange(numOfClusterInput, 2, 4);
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