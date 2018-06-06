package vision.lib.ui.dialog;

import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class provides the basic framework for prompting the user to fill in some configuration values for a task
 *
 * @author Patrick Chan
 */
public abstract class ConfigurationDialog extends Dialog {

    /** The {@link Label} holding the title of this dialog */
    protected Label title;

    protected ConfigurationDialog() {

    }

    /**
     * Creates an instance of the ConfigurationDialog class that takes a {@link Stage} parameter for the owner of the
     * dialog and a {@link String} parameter for the title of the dialog.
     *
     * @param owner The {@link Stage} for this dialog
     * @param title The title for this dialog
     */
    protected ConfigurationDialog(Stage owner, String title) {
        this.setTitle(title);
        this.title = new Label(title);
        init(owner);
    }

    /**
     * Set the {@link Modality} and {@link Stage} for this dialog
     *
     * @param owner Specifies the {@link Stage} for this dialog, or null for a top-level, unowned dialog.
     */
    @Override
    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
    }

    /**
     * Show the dialog.
     */
    public void showDialog() {
        this.showAndWait();
    }

    /**
     * Check for whether the values of the configuration dialog is valid or not.  This should
     * be implemented in all subclasses.
     *
     * @return Whether the values of the configuration dialog is valid or not
     */
    public abstract boolean isValid();

}
