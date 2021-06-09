package msys.client.stages.tools.management.module;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class Parser {
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
        return new Gson().toJson(config.get("identifier"));
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Option> extractOptions(Map<String , Object> config){
        ArrayList<Option> result = new ArrayList<>();
        ArrayList<Map<String, Object>> options = (ArrayList<Map<String, Object>>)config.get("options");
        if (options != null){
            for (Map<String, Object> option : options){
                result.add(new Option(option));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Connectable> extractInputs(Map<String , Object> config){
        ArrayList<Connectable> result = new ArrayList<>();
        ArrayList<Map<String, Object>> inputs = (ArrayList<Map<String, Object>>)config.get("inputs");
        if (inputs != null){
            for (Map<String, Object> input : inputs){
                result.add(new Connectable(input));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Connectable> extractOutputs(Map<String , Object> config){
        ArrayList<Connectable> result = new ArrayList<>();
        ArrayList<Map<String, Object>> outputs = (ArrayList<Map<String, Object>>)config.get("outputs");
        if (outputs != null){
            for (Map<String, Object> output : outputs){
                result.add(new Connectable(output));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Module> extractModules(Map<String , Object> config){
        ArrayList<Module> result = new ArrayList<>();
        ArrayList<Map<String, Object>> modules = (ArrayList<Map<String, Object>>)config.get("modules");
        if (modules != null){
            for (Map<String, Object> module : modules){
                result.add(new Module(module));
            }
        }
        return result;
    }

    public static Map<String, Object> updateMetadata(Map<String , Object> config, Metadata metadata){
        config.put("metadata", metadata.toMap());
        return config;
    }
}
