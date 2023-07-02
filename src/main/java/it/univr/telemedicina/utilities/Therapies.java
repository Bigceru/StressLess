package it.univr.telemedicina.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public enum Therapies {
    ACE_INIBITORI,
    BETA_BLOCCANTI,
    CALCIO_ANTAGONISTI,
    DIURETICHE,
    SARTANI,
    SIMPATICOLITICI;

    /**
     * Method that return the collection of Therapies
     * @return list
     */
    public static Collection<String> returnCollection(){
        List<String> list = new ArrayList<>();
        for(Therapies i : Therapies.values()){
            list.add((i.toString().contains("_"))? i.toString().replace("_"," ") : i.toString());
        }

        return list;
    }
}

