package vision.lib.ui.dialog;

import javafx.stage.Stage;

/**
 * The base class for all dialogs implemented in the Vision framework.
 *
 * @author Patrick Chan
 */
public abstract class Dialog extends Stage {    

    /**
     * Completely initialize the dialog.
     *
     * @param owner The {@link Stage} for this dialog
     */
    protected abstract void init(Stage owner);

}
