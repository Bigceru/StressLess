package it.univr.telemedicina.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//Da mettere anche su DoctorStatisticsSceneController (riga 212)
public enum Therapies {
    ACE_INIBITORI,
    BETA_BLOCCANTI,
    CALCIO_ANTAGONISTI,
    DIURETICHE,
    SARTANI,
    SIMPATICOLITICI;

    public static Collection<String> returnCollection(){
        List<String> list = new ArrayList<>();
        for(Therapies i : Therapies.values()){
            list.add((i.toString().contains("_"))? i.toString().replace("_","-") : i.toString());
        }

        return list;
    }
}

