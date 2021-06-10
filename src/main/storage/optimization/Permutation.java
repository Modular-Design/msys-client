package msys.client.optimization;

import mdd.client.GUIEventHandler;
import mdd.client.IGUIEventClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class Permutation implements IData, IGUIEventClient {
    private Vector<String> _skeys = new Vector<>();
    private Vector<Integer> _ikeys = new Vector<>();
    private Vector<Double> _vals = new Vector<>();
    protected GUIEventHandler _eventHandler;
    public JSONObject jprocessor;
    public JSONArray jdna;
    public JSONArray jouts;
    public long step;

    private DataTable _model;
    private int _row;

    public Permutation(long gen, JSONObject json){
        step = gen;
        _eventHandler = GUIEventHandler.getEventHandler(0);
        _eventHandler.addEventListener(this, 5);
        setValue("step", (double)gen);
        configure(json);
    }

    @Override
    public void setDataModel(DataTable table, int row){
        _model = table;
        _row = row;
        for (int i = 0; i < _skeys.size(); ++i){
            _ikeys.set(i, _model.addColumn(_skeys.get(i)));
        }
    }

    @Override
    public Vector<String> getKeys(){
        return _skeys;
    }

    @Override
    public Vector<Integer> getIDs(){
        return _ikeys;
    }

    @Override
    public Double getValue(int column){
        for (int i = 0; i < _skeys.size(); ++i){
            if (_ikeys.get(i).equals(column)){
                return _vals.get(i);
            }
        }
        return null;
    }

    @Override
    public void setValue(String key, Double val){
        for (int i = 0; i < _skeys.size(); ++i){
            if (_skeys.get(i).equals(key)){
                if (!_vals.get(i).equals(val)){
                    _vals.set(i,val);
                    if (_model != null){
                        _model.callback_change(_row, _ikeys.get(i));
                    }
                }
                return;
            }
        }
        //key doesnt exist
        _skeys.add(key);
        _ikeys.add(null);
        _vals.add(val);
        if (_model != null){
            _ikeys.set(_ikeys.size()-1, _model.addColumn(key));
            _model.callback_change(_row, _ikeys.lastElement());
        }
    }

    protected void configure(JSONObject json){
        if(json.containsKey("status")){
            switch(json.get("status").toString()){
                case "1":
                    setValue("status", 1.0);
                    break;
                case "-1":
                    setValue("status", -1.0);
                    break;
                default:
                    setValue("status", 0.0);
            }
        }
        double fit_test = 0;
        double out_test = 0;
        if(json.containsKey("fitness")){
            setValue("fitness", Double.valueOf(json.get("fitness").toString()));
            fit_test = Double.valueOf(json.get("fitness").toString());
        }
        if(json.containsKey("accepted")){
            //setValue("accepted", Double.valueOf(json.get("fitness").toString()));
        }
        if(json.containsKey("time")){
            setValue("time", Double.valueOf(json.get("time").toString()));
            //setValue("time", (double)json.get("time"));
        }
        if (jdna == null){
            if(json.containsKey("dna")){
                JSONParser parser = new JSONParser();
                try {
                    //jdna = (JSONArray)json.get("dna");
                    jdna = (JSONArray)parser.parse(json.get("dna").toString());
                }
                catch (ParseException e){
                    jdna = (JSONArray)json.get("dna");
                }

                for (int i = 0; i < jdna.size(); ++i){
                    JSONArray params = (JSONArray) jdna.get(i);
                    for (int j = 0; j < params.size(); ++j){
                        setValue("param["+i+","+j+"]", (double) params.get(j));
                    }
                }
            }
        }
        if(json.containsKey("outputs")){
            JSONParser parser = new JSONParser();

            try {
                //jdna = (JSONArray)json.get("dna");
                jouts = (JSONArray)parser.parse(json.get("outputs").toString());
            }
            catch (ParseException e){
                jouts = (JSONArray)json.get("outputs");
            }

            for (int i = 0; i < jouts.size(); ++i){
                JSONArray params = (JSONArray) jouts.get(i);
                for (int j = 0; j < params.size(); ++j){
                    setValue("out["+i+","+j+"]", (double) params.get(j));
                    out_test = (double) params.get(j);
                }
            }
        }
        if (Math.abs(out_test-fit_test)>0.5){
            int test = 0;
        }
        if(json.containsKey("processor")){
            JSONParser parser = new JSONParser();
            try {
                jprocessor = (JSONObject) parser.parse(json.get("processor").toString());
            }catch (ParseException e){
                if(!json.get("processor").toString().equals("NULL")){
                    jprocessor = (JSONObject)json.get("processor");
                }
            }
        }
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        if (event.equals("change")) {
            if (!args.containsKey("args")) {
                return;
            }
            JSONObject jargs = (JSONObject) args.get("args");
            if (!jargs.containsKey("subject")) {
                return;
            }
            JSONObject jsub = (JSONObject) jargs.get("subject");
            if (!jsub.get("type").equals("optimizer")) {
                return;
            }
            if (!args.containsKey("operation")) {
                return;
            }
            if (!"change".equals((String) args.get("operation"))) {
                return;
            }
            JSONObject jobj = (JSONObject) jargs.get("object");
            if (!jobj.containsKey("step")) {
                return;
            }
            if (step !=(long)jobj.get("step")){
                return;
            }
            if (!jobj.containsKey("permutation")) {
                return;
            }
            JSONObject jind = (JSONObject)jobj.get("permutation");
            //System.out.println("Permutation: " + _dna.toString());
            if (!jdna.equals((JSONArray)jind.get("dna"))) {
                return;
            }else{
                if (!jdna.toString().equals(jind.get("dna").toString())){
                    System.out.println("It happened!");
                }
                configure(jind);
            }

        }
    }

    @Override
    public void close() throws Exception {

    }
}
