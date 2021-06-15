package msys.client.stages.tools.management.module;

import com.google.gson.Gson;
import msys.client.eventhandling.GUIEventHandler;

import java.util.ArrayList;
import java.util.Map;

public class Parser {

    public static boolean isValid(Map<String , Object> config){
        return config.containsKey("id") && config.containsKey("metadata");
    }

    @SuppressWarnings("unchecked")
    public static Metadata extractMetaData(Map<String , Object> config){
        return new Metadata((Map<String, Object>) config.get("metadata"));
    }


    public static String extractName(Map<String , Object> config){
        Metadata meta = Parser.extractMetaData(config);
        if (meta.name != null && !meta.name.equals("")){
            return meta.name;
        }
        return (String) config.get("id");
    }

    @SuppressWarnings("unchecked")
    public static String extractIdentifier(Map<String , Object> config){
        return new Gson().toJson(config.get("identifier")).replaceAll(" ", "");
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Option> extractOptions(int handler_no, Map<String , Object> config){
        ArrayList<Option> result = new ArrayList<>();
        ArrayList<Map<String, Object>> options = (ArrayList<Map<String, Object>>)config.get("options");
        if (options != null){
            for (Map<String, Object> option : options){
                result.add(new Option(handler_no, option));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Connectable> extractInputs(int handler_no, Map<String , Object> config){
        ArrayList<Connectable> result = new ArrayList<>();
        ArrayList<Map<String, Object>> inputs = (ArrayList<Map<String, Object>>)config.get("inputs");
        if (inputs != null){
            for (Map<String, Object> input : inputs){
                result.add(new Connectable(handler_no, input));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Connectable> extractOutputs(int handler_no, Map<String , Object> config){
        ArrayList<Connectable> result = new ArrayList<>();
        ArrayList<Map<String, Object>> outputs = (ArrayList<Map<String, Object>>)config.get("outputs");
        if (outputs != null){
            for (Map<String, Object> output : outputs){
                result.add(new Connectable(handler_no, output));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Module> extractModules(int handler_no, Map<String , Object> config){
        ArrayList<Module> result = new ArrayList<>();
        ArrayList<Map<String, Object>> modules = (ArrayList<Map<String, Object>>)config.get("modules");
        if (modules != null){
            for (Map<String, Object> module : modules){
                result.add(new Module(handler_no, module));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Connection> extractConnections(int handler_no,Map<String , Object> config){
        ArrayList<Connection> result = new ArrayList<>();
        Map<String, Map<String, Object>> connections = (Map<String, Map<String, Object>>)config.get("connections");

        if (connections != null){
            var members = GUIEventHandler.getEventHandler(handler_no).getMembers();
            for (String outid : connections.keySet()){
                // request output
                Connectable output = (Connectable) members.get(outid.replaceAll( " ", ""));
                if (output == null){
                    continue;
                }
                var inputs = connections.get(outid);

                for (String inid : inputs.keySet()){
                    // request input
                    Connectable input = (Connectable) members.get(inid.replaceAll( " ", ""));
                    if (input == null){
                        continue;
                    }
                    result.add(new Connection(handler_no, output, input));
                }
            }
        }
        return result;
    }

    public static Map<String, Object> updateMetadata(Map<String , Object> config, Metadata metadata){
        config.put("metadata", metadata.toMap());
        return config;
    }
}
