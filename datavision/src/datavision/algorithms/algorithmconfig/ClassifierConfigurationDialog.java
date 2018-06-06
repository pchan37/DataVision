package datavision.algorithms.algorithmconfig;

import datavision.algorithms.algorithmconfig.AlgorithmConfigurationDialog;
import datavision.api.DataAPI;
import javafx.stage.Stage;

import java.util.Map;

public class ClassifierConfigurationDialog extends AlgorithmConfigurationDialog {

    public ClassifierConfigurationDialog(Stage owner, DataAPI dataAPI, String title) {
        super(owner, dataAPI, title);
    }

}
