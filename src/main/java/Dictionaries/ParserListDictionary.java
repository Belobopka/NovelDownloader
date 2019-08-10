package Dictionaries;

import Parsers.ParserAbstract;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.function.Function;

public class ParserListDictionary {
    HashMap<String, Function<Integer, ParserAbstract>> hashFunctionDictionary;
    Gson gson = new Gson();
    String jsonFilePath;
    File jsonFile;
    public ParserListDictionary() {
    }
    public ParserListDictionary(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }
    private void createFileBufferFromPath(String filePath) {

    }
    private void createDictionaryFromJson() {

    }
}
