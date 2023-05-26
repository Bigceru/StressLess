package it.univr.telemedicina;

public class TablePatientDrugs {
    private String name;
    private String instruction;
    private int amount;
    private int dose;

    public TablePatientDrugs(String name, int dose, int amount, String instruction){
        this.name = name;
        this.instruction = instruction;
        this.amount = amount;
        this.dose = dose;
    }

    public String getName() {
        return name;
    }

    public String getInstruction() {
        return instruction;
    }

    public int getAmount() {
        return amount;
    }

    public int getDose() {
        return dose;
    }
}