package vision.core;

/**
 * The abstract base class for the main user interface window
 */
public abstract class UI extends UIElement {

    /** The action component of this application */
    protected Action action;
    /** The data component of this application */
    protected Data data;

    /**
     * Initialize the main user interface window
     */
    public abstract void initialize();

}
