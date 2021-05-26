package msys.client.optimization;

import java.util.ArrayList;
import java.util.Vector;

public class DataTable extends ArrayList<IData>{
    private int _id;
    private DataModel _model;
    private Vector<String> _keys = new Vector<>();

    public DataTable(DataModel model, int id){
        super();
        _model = model;
        _id = id;
    }

    public Vector<String> getKeys(){
        return _keys;
    }

    public int addColumn(String key){
        for(int i = 0; i < _keys.size(); ++i){
            if (_keys.get(i).equals(key)){
                return i;
            }
        }
        _keys.add(key);
        _model.callback_newKey(_id, key);
        return _keys.size()-1;
    }

    public boolean add(IData data){
        boolean ret = super.add(data);
        int column = size()-1;
        data.setDataModel(this, column);
        _model.callback_addData(_id, column);
        return  ret;
    }

    public void callback_change(int data, int key){
        _model.callback_change(_id, data, key);
    }
}
