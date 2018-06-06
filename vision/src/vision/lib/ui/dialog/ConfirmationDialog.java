package vision.lib.ui.dialog;

import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class allows users of the Vision framework to displays a dialog asking the user wants 
 * to proceed with an action (Yes), stop an action (No), or cancel an action (Cancel).  Portions
 * of this class were inspired by Professor Banerjee.
 *
 * @author Patrick Chan
 */
public class ConfirmationDialog extends Dialog {

    /** A {@link Label} for this dialog's message */
    protected Label message;
    /** Stores which {@link Option} the user has selected */
    protected Option selectedOption;

    /**
     * Enum values for the different options available for a {@link ConfirmationDialog}
     *
     * @author Patrick Chan
     */
    public enum Option {

        YES("Yes"),
        NO("No"),
        CANCEL("Cancel");

        /** The more user-friendly name for each option */
        @SuppressWarnings("unused")
        private String option;

        /**
         * Creates a Option enum value and associate it with a more user-friendly name as well
         * @param option
         */
        Option(String option) {
            this.option = option;
        }

        /**
         * Returns {@link Option} in {@link String} form
         *
         * @return The {@link String} representation of the {@link Option}
         */
        public String toString() {
            return option;
        }

        /**
         * Returns the {@link Option} given the {@link String} form
         *
         * @return The {@link Option} given the {@link String} form
         */
        public static Option fromString(String value) {
            for (Option enumValue : Option.values()) {
                if (enumValue.name().equalsIgnoreCase(value))
                    return enumValue;
            }
            return null;
        }
        
    }

    /**
     * Creates an instance of the ConfirmationDialog class that takes a {@link Stage} parameter which specifies the
     * parent of this dialog, a {@link String} parameter which specifies the title of this dialog, and a {@link String}
     * parameter which specifies the content of the dialog message.
     *
     * @param owner The {@link Stage} for this dialog
     * @param title The title of this dialog as a {@link String}
     * @param message The message for this dialog as a {@link String}
     */
    public ConfirmationDialog(Stage owner, String title, String message) {
        setTitle(title);
        setConfirmationMessage(message);
        init(owner);
    }        

    /**
     * Completely initialize the dialog.
     *
     * @param owner The {@link Stage} for this dialog
     */
    @Override
    protected void init(Stage owner) {
        // Ensure that messages are blocked from reaching other windows
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        List<Button> buttons = Arrays.asList(new Button(Option.YES.toString()),
                                             new Button(Option.NO.toString()),
                                             new Button(Option.CANCEL.toString()));
        buttons.forEach(button -> button.setOnAction((ActionEvent event) -> {
                    this.selectedOption = Option.fromString(((Button) event.getSource()).getText());
                    this.close();
                }));

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(buttons);

        VBox messagePane = new VBox(message, buttonBox);
        messagePane.setAlignment(Pos.CENTER);
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);

        this.setScene(new Scene(messagePane));
    }

    /**
     * Return the option that the user selected
     *
     * @return The option that the user selected
     */
    public ConfirmationDialog.Option getSelectedOption() {
        return this.selectedOption;
    }

    /**
     * Set the message for this dialog
     *
     * @param message The confirmation message to be displayed in this dialog
     */
    private void setConfirmationMessage(String message) {
        this.message = new Label(message);
    }
    
}
