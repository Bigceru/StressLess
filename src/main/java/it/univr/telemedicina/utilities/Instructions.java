package it.univr.telemedicina.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum Instructions {
    Mattina,
    Pomeriggio,
    Sera,
    Notte,
    Vicino_Pasti,
    Lontano_Pasti;

    /**
     *  Method that return the collection of Instruction
     * @return list
     */
    public static Collection<String> returnCollection(){
        List<String> list = new ArrayList<>();
        for(Instructions i : Instructions.values()){
            list.add((i.toString().contains("_"))? i.toString().replace("_"," ") : i.toString());
        }
        return list;
    }
}

