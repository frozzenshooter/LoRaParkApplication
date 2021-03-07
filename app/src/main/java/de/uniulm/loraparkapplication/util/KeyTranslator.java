package de.uniulm.loraparkapplication.util;

public class KeyTranslator {

    private static KeyTranslator instance;

    public static KeyTranslator getInstance(){
        if(instance == null){
            instance = new KeyTranslator();
        }
        return instance;
    }

    public String getLabelForKey(String key){
        return key;
    }
}
