package vision.core;

import vision.lib.exceptions.InvalidDataFormatException;
import vision.lib.exceptions.LoadErrorException;
import javafx.stage.Stage;
import vision.lib.exceptions.SaveErrorException;

/**
 * The abstract base class for the action component of this application
 */
public abstract class Action {

    /** The data component of this application */
    protected Data data;
    /** The UI component of this application */
    protected UI ui;

    /**
     * The handler for when the user clicks on the new button
     */
    public abstract void handleNewRequest();

    /**
     * The handler for when the user clicks on the load button
     *
     * @throws InvalidDataFormatException When the data the user is trying to load is invalid
     * @throws LoadErrorException When there is an error loading from a file
     */
    public abstract void handleLoadRequest() throws InvalidDataFormatException, LoadErrorException;

    /**
     * The handler for when the user clicks on the save button
     *
     * @throws InvalidDataFormatException When the data the user is trying to save is invalid
     * @throws SaveErrorException When there is an error saving to a file
     */
    public abstract void handleSaveRequest() throws InvalidDataFormatException, SaveErrorException;

    /**
     * The handler for when the user clicks on the print button
     */
    public abstract void handlePrintRequest();

    /**
     * The handler for when the user clicks on the exit button
     *
     * @throws InvalidDataFormatException When there is data to be saved and the user wants to save, but it is in an invalid format
     * @throws SaveErrorException When there is data to be saved and the user wants to save, but there is an error saving to a file
     */
    public abstract void handleExitRequest() throws InvalidDataFormatException, SaveErrorException;

}
