package vision.lib.ui.dialog;

import javafx.scene.control.Label;
import javafx.stage.Stage;
    
/**
 * This class allows users of the Vision framework to displays a dialog informing the user of an
 * error. Portions of this class were inspired by Professor Banerjee.
 *
 * @author Patrick Chan
 */
public class ErrorDialog extends InformativeDialog {

    /**
     * Creates an instance of the ErrorDialog class that takes a {@link Stage} parameter which specifies the parent of
     * this dialog, a {@link String} parameter which specifies the title of this dialog, and a {@link String}
     * parameter which specifies the content of the dialog message.
     *
     * @param owner The {@link Stage} for this dialog
     * @param title The title of this dialog as a {@link String}
     * @param message The message for this dialog as a {@link String}
     */
    public ErrorDialog(Stage owner, String title, String message) {
        setTitle(title);
        setErrorMessage(message);
        init(owner);
    }

    /**
     * Set the error message
     *
     * @param message The error message to be displayed in this dialog
     */
    private void setErrorMessage(String message) {
        this.message = new Label(message);

    }
    
}
