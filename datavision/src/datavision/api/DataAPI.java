package datavision.api;

import datavision.core.AppData;
import datavision.core.AppUI;
import vision.core.Data;

public class DataAPI {

    private Data data;

    public DataAPI(Data data) {
        this.data = data;
    }

    public int getNumLabels() {
        return ((AppData)data).getNumLabels();
    }

    public int getTotalPoints() { return ((AppData)data).getTotalPoints(); }

}
