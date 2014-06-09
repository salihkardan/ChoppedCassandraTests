package ctest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class Main {

    private static final String filePath = "/home/salih/projects/bitbucket/ctest/src/main/resources/result.json";
    public static void main(String[] args) {
        try {
            // read the json file
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            extractMessage(jsonObject);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public static void extractMessage(JSONObject json){
        JSONArray runResults = (JSONArray) json.get("runResults");
        Iterator itr = runResults.iterator();
        while ( itr.hasNext() ){
            JSONObject failures = (JSONObject) itr.next();
            JSONArray obj = (JSONArray) failures.get("failures");
            Iterator iterator = obj.iterator();
            while (iterator.hasNext()){
                JSONObject j = (JSONObject) iterator.next();
                String message = (String) j.get("message");
                System.out.println(message);
            }
        }
    }

}
