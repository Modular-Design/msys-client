package msys.client.optimization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import msys.client.eventhandling.GUIEventHandler;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

public class PDiagramm extends VBox implements IPlotCallback {
    private DataModel _data;

    private int _table = 0;
    int x_id = 0, y_id = 0;

    private ComboBox<String> CBDiagramm = new ComboBox<>(), CBKey1 = new ComboBox<>(), CBKey2 = new ComboBox<>();

    private NumberAxis xAxis_line = new NumberAxis();
    private NumberAxis xAxis_point = new NumberAxis();
    private NumberAxis yAxis_line = new NumberAxis();
    private NumberAxis yAxis_point = new NumberAxis();

    Comparator<XYChart.Data<Number, Number>> data_compaire = Comparator.comparingDouble(d -> d.getXValue().doubleValue());

    private LineChart<Number,Number> lineChart = new LineChart<>(xAxis_line, yAxis_line);
    private ScatterChart<Number,Number> pointChart = new ScatterChart<>(xAxis_point,yAxis_point);

    final XYChart.Series<Number, Number> series_all = new XYChart.Series<>(), series_min = new XYChart.Series<>(), series_max = new XYChart.Series<>();


    public PDiagramm(DataModel data){
        _data = data;
        _data.addPlotter(this);

        HBox hbox = new HBox();
        hbox.setSpacing(10f);
        HBox hbox1 = new HBox();
        hbox1.getChildren().add(new Label("Select Diagramm: "));
        hbox1.getChildren().add(CBDiagramm);
        hbox.getChildren().add(hbox1);

        HBox hbox2 = new HBox();
        hbox2.getChildren().add(new Label("Select x-Axis: "));
        hbox2.getChildren().add(CBKey1);
        hbox.getChildren().add(hbox2);

        HBox hbox3 = new HBox();
        hbox3.getChildren().add(new Label("Select y-Axis: "));
        hbox3.getChildren().add(CBKey2);
        hbox.getChildren().add(hbox3);
        getChildren().add(hbox);

        if (_data.size() > _table){
            Vector<String> skeys = _data.get(_table).getKeys();
            if (skeys != null){
                for(int i = 0; i < skeys.size(); ++i){
                    String nkey = skeys.get(i);
                    CBKey1.getItems().add(nkey);
                    CBKey2.getItems().add(nkey);
                }
            }

            for (int i = 0; i < _data.get(_table).size(); ++i){
                insertData(_data.get(_table).get(i));
            }
        }

        ObservableList<String> slist = CBDiagramm.getItems();
        slist.add("Points");
        slist.add("Line");

        CBDiagramm.valueProperty().addListener((ov, t, t1) -> {
            if (getChildren().size() >1){
                getChildren().remove(1);
            }
            if (t1.equals("Points")) {
               getChildren().add(pointChart);
            }
            else{
                getChildren().add(lineChart);
            }
        });

        CBDiagramm.getSelectionModel().select(1);

        CBKey1.valueProperty().addListener((ov, t, t1) -> {
            xAxis_line.setLabel(t1);
            xAxis_point.setLabel(t1);
            if (_data.size() > _table) {
                Vector<String> skeys1 = _data.get(_table).getKeys();
                if (skeys1 != null) {
                    for (int i = 0; i < skeys1.size(); ++i) {
                        if (t1.equals(skeys1.get(i))) {
                            x_id = i;
                            break;
                        }
                    }

                    series_min.getData().clear();
                    series_max.getData().clear();

                    for (int i = 0; i < _data.get(_table).size(); ++i) {
                        IData idata = _data.get(_table).get(i);
                        series_all.getData().get(i).setXValue(idata.getValue(x_id));
                        insertMinData(idata);
                        insertMaxData(idata);
                    }
                    sortData();
                }
            }
        });
        CBKey1.getSelectionModel().select(0);

        CBKey2.valueProperty().addListener((ov, t, t1) -> {
            yAxis_line.setLabel(t1);
            yAxis_point.setLabel(t1);
            if (_data.size() > _table) {
                Vector<String> skeys12 = _data.get(_table).getKeys();
                if (skeys12 != null) {
                    for (int i = 0; i < skeys12.size(); ++i) {
                        if (t1.equals(skeys12.get(i))) {
                            y_id = i;
                            break;
                        }
                    }
                    //ObservableList<XYChart.Data<Number, Number>> smindata = series_min.getData();
                    //ObservableList<XYChart.Data<Number, Number>> smaxdata = series_max.getData();
//
                    //for (int i = 0; i < _data.get(_table).size(); ++i) {
                    //    IData idata = _data.get(_table).get(i);
                    //    series_all.getData().get(i).setYValue(idata.getValue(y_id));
                    //    updateMinYArg(smindata, (Permutation)idata);
                    //    updateMaxYArg(smaxdata, (Permutation)idata);
                    //}

                    series_min.getData().clear();
                    series_max.getData().clear();

                    for (int i = 0; i < _data.get(_table).size(); ++i) {
                        IData idata = _data.get(_table).get(i);
                        series_all.getData().get(i).setYValue(idata.getValue(y_id));
                        insertMinData(idata);
                        insertMaxData(idata);
                    }
                    sortData();
                }
            }
        });
        CBKey2.getSelectionModel().select(0);

        series_all.setName("data");
        series_min.setName("min");
        series_max.setName("max");

        pointChart.getData().addAll(series_all);
        lineChart.getData().addAll(series_min, series_max);
    }

    protected void addEvent(XYChart.Data data, Permutation per){
        Node node = data.getNode();
        if (node != null && per.jprocessor != null){
            node.setOnMouseClicked(mouseEvent -> {
                System.out.println("CALLED");
                GUIEventHandler.getEventHandler(1).publishEvent(10,"OPEN", per.jprocessor);
            });
        }
    }

    private void insertData( IData data){
        if (data instanceof Permutation){
            XYChart.Data<Number, Number> xydata=  new XYChart.Data<>(data.getValue(x_id), data.getValue(y_id));
            series_all.getData().add(xydata);
            addEvent(xydata, (Permutation) data);
        }
    }

    private void insertMinData( IData data){
        if (data instanceof Permutation){
            Permutation per = (Permutation)data;
            double x = data.getValue(x_id);
            double y = data.getValue(y_id);
            if (containsXArg(series_min.getData(), x)){
                updateMinYArg(series_min.getData(), per);
            }else{
                series_min.getData().add(new XYChart.Data<>(x, y));
            }
        }
    }

    private void insertMaxData( IData data){
        if (data instanceof Permutation){
            Permutation per = (Permutation)data;
            double x = data.getValue(x_id);
            double y = data.getValue(y_id);
            if (containsXArg(series_max.getData(), x)){
                updateMaxYArg(series_max.getData(), per);
            }else{
                XYChart.Data<Number, Number> xydata=  new XYChart.Data<>(x, y);
                series_max.getData().add(xydata);
            }
        }
    }

    private void sortData(){
        series_min.getData().sort(data_compaire);
        series_max.getData().sort(data_compaire);
        //series_all.getData().sort(data_compaire);
    }

    private boolean containsXArg(final ObservableList<XYChart.Data<Number, Number>> list, final Double x){
        return list.stream().filter(o -> o.getXValue().equals(x)).findFirst().isPresent();
    }

    private void updateMinYArg(final ObservableList<XYChart.Data<Number, Number>> list, Permutation per){
        double x = per.getValue(x_id);
        double y = per.getValue(y_id);
        list.stream().filter(o -> o.getXValue().equals(x)).forEach(
                o -> {
                    if (o.getYValue().doubleValue()> y){
                        o.setYValue(y);
                        addEvent(o, per);
                    }
                }
        );
    }
    private void updateMaxYArg(final ObservableList<XYChart.Data<Number, Number>> list, Permutation per){
        double x = per.getValue(x_id);
        double y = per.getValue(y_id);
        list.stream().filter(o -> o.getXValue().equals(x)).forEach(
                o -> {
                    if (o.getYValue().doubleValue()<y){
                        o.setYValue(y);
                        addEvent(o, per);
                    }
                }
        );
    }

    @Override
    public void callback_change(int table, int data, int key) {
        if (table != _table){
            return;
        }
        IData idata = _data.get(_table).get(data);
        if (idata instanceof Permutation){
            Permutation pdata = (Permutation) idata;
            if (key == x_id){
                series_all.getData().get(data).setXValue(idata.getValue(x_id));
            }else if (key == y_id){
                series_all.getData().get(data).setYValue(idata.getValue(y_id));
            }
            addEvent(series_all.getData().get(data), pdata);
            series_min.getData().clear();
            series_max.getData().clear();

            for (int i = 0; i < _data.get(_table).size(); ++i) {
                IData iData = _data.get(_table).get(i);
                insertMinData(iData);
                insertMaxData(iData);
            }
            sortData();
        }
    }

    @Override
    public void callback_addData(int table, int data) {
        if (table != _table){
            return;
        }
        IData idata = _data.get(_table).get(data);
        insertData(idata);
        insertMinData(idata);
        insertMaxData(idata);
    }

    @Override
    public void callback_clear(int table) {
        series_all.getData().clear();
        series_min.getData().clear();
        series_max.getData().clear();
    }

    @Override
    public void callback_newKey(int table, String key) {
        if (table != _table){
            return;
        }
        ObservableList<String> keys = CBKey1.getItems();
        for(int i = 0; i < keys.size(); ++i){
            if (keys.get(i).equals(key)){
                return;
            }
        }
        CBKey1.getItems().add(key);
        CBKey2.getItems().add(key);
    }
}
