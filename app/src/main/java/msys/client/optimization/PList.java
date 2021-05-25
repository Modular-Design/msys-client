package msys.client.optimization;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

public class PList extends HBox implements IPlotCallback {
    private DataModel _data;

    private int _table = 0;
    //TODO check and do something if data gets cleared
    public PList(DataModel data){
        _data = data;
        _data.addPlotter(this);
        setSpacing(30);
        if (_data.size() > _table){
            for (int i = 0; i < _data.get(_table).size(); ++i){
                insertData(_data.get(_table).get(i));
            }
        }
    }

    void insertData(IData data){
        if (data instanceof Permutation){
            Permutation per = (Permutation)data;
            while(getChildren().size() <= per.step){
                VBox vBox = new VBox();
                vBox.setSpacing(10f);
                vBox.getChildren().add(new Label("step: " + getChildren().size()));
                getChildren().add(vBox);
            }
            ((VBox)getChildren().get((int) per.step)).getChildren().add(new PListElement(per));
            try {
                sortList((int) per.step);
            }catch (IllegalArgumentException e){
                VBox vBox = ((VBox)getChildren().get((int) per.step));
                int test = 0;
                sortList((int) per.step);
            }

        }
    }

    private void sortList(int step){
        PListComperator comp = new PListComperator();
        VBox vbox = ((VBox)getChildren().get(step));
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(vbox.getChildren());
        Collections.sort(workingCollection, comp);
        vbox.getChildren().setAll(workingCollection);
    }

    @Override
    public void callback_change(int table, int data, int key) {
        if (table != _table){
            return;
        }
        IData idata = _data.get(_table).get(data);
        if (idata instanceof Permutation){
            Permutation pdata = (Permutation) idata;
            //int counter  = 0;
            //for (int i = 0; i < pdata.step; ++i){
            //    counter += ((VBox)getChildren().get(i)).getChildren().size();
            //    counter -= 1;
            //}
            VBox vBox = ((VBox)getChildren().get((int) pdata.step));
            //PListElement plElem = (PListElement) vBox.getChildren().get(data-counter+1);
            //plElem.configure(pdata);
            for (int i = 1; i < vBox.getChildren().size(); ++i){
                PListElement plElem =(PListElement) vBox.getChildren().get(i);
                if (plElem.jdna.toString().equals(pdata.jdna.toString())){
                    plElem.configure(pdata);
                }
            }
            sortList((int) pdata.step);
        }
    }

    @Override
    public void callback_addData(int table, int data) {
        if (table != _table){
            return;
        }
        insertData(_data.get(_table).get(data));
    }

    @Override
    public void callback_clear(int table) {
        getChildren().clear();
    }

    @Override
    public void callback_newKey(int table, String key) {
        if (table != _table){
            return;
        }
    }
}
