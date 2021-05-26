package msys.client.optimization;

import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public interface IData {
    void setDataModel(DataTable model, int row);
    Vector<String> getKeys();
    Vector<Integer> getIDs();
    Double getValue(int column);
    void setValue(String key, Double val);
}
