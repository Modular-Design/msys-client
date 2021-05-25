package msys.client.optimization;

import java.util.ArrayList;

public class DataModel extends ArrayList<DataTable> implements IPlotCallback{
    private ArrayList<IPlotCallback> _plotter = new ArrayList<>();


    public DataModel(){

    }

    public void addPlotter(IPlotCallback plotter){
        _plotter.add(plotter);
    }

    public void removePlotter(IPlotCallback plotter){
        _plotter.remove(plotter);
    }

    public void add(int table, IData data){
        while (size() <= table){
            add(new DataTable(this, size()));
        }
        get(table).add(data);
    }

    public void clear(){
        for (int i = 0; i < size(); ++i){
            callback_clear(i);
        }
        removeAll(this);
    }

    @Override
    public void callback_change(int table, int data, int key){
        for (int i = 0; i < _plotter.size(); ++i){
            _plotter.get(i).callback_change(table, data, key);
        }
    }

    @Override
    public void callback_newKey(int table, String key){
        for (int i = 0; i < _plotter.size(); ++i){
            _plotter.get(i).callback_newKey(table, key);
        }
    }

    @Override
    public void callback_addData(int table, int data){
        for (int i = 0; i < _plotter.size(); ++i){
            _plotter.get(i).callback_addData(table, data);
        }
    }

    @Override
    public void callback_clear(int table) {
        for (int i = 0; i < _plotter.size(); ++i){
            _plotter.get(i).callback_clear(table);
        }
    }

}
