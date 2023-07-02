package it.univr.telemedicina;

/**
 * Class to represent the table do the drugs taken by the patient
 */
public class TablePatientDrugs {
    private String name;
    private String instruction;
    private int amount;
    private int dose;

    /**
     *
     * @param name name of drug
     * @param dose dose taken
     * @param amount amount taken
     * @param instruction instruction
     */
    public TablePatientDrugs(String name, int dose, int amount, String instruction){
        this.name = name;
        this.instruction = instruction;
        this.amount = amount;
        this.dose = dose;
    }

    /**
     * Take Name
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Take Instruction
     * @return String
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * Take Amount
     * @return String
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Take Dose
     * @return String
     */
    public int getDose() {
        return dose;
    }
}