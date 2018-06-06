package vision.lib.ui.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import vision.utils.propertymanager.PropertyManager;
import vision.utils.settings.AppSettings;

/**
 * This class allows users of the Vision framework to displays a dialog informing the user of 
 * something they should know.  Portions of this class were inspired by Professor Banerjee.
 *
 * @author Patrick Chan
 */
public class InformativeDialog extends Dialog {
    
    protected Label message;

    public InformativeDialog() {

    }

    /**
     * Creates an instance of the ErrorDialog class that takes a {@link Stage} parameter which specifies the parent of
     * this dialog, a {@link String} parameter which specifies the title of this dialog, and a {@link String}
     * parameter which specifies the content of the dialog message.
     *
     * @param owner The {@link Stage} for this dialog
     * @param title The title of this dialog as a {@link String}
     * @param message The message for this dialog as a {@link String}
     */
    public InformativeDialog(Stage owner, String title, String message) {
        setTitle(title);
        setInformativeMessage(message);
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

        PropertyManager manager = PropertyManager.getManager();
        String closeButtonText = manager.getPropertyValue(AppSettings.OK_LABEL.name());
        Button closeButton = new Button(closeButtonText);
        VBox messagePane = new VBox();

        closeButton.setOnAction(e -> this.close());
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(message);
        messagePane.getChildren().add(closeButton);
        messagePane.setPadding(new Insets(80, 60, 80, 60));
        messagePane.setSpacing(20);

        Scene messageScene = new Scene(messagePane);
        this.setScene(messageScene);                                                  
    }       

    /**
     * Set the informative message
     *
     * @param message The informative message to be displayed in this dialog
     */
    private void setInformativeMessage(String message) {
        this.message = new Label(message);
    }
    
}
