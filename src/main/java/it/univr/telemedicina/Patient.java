package it.univr.telemedicina;
import java.util.Date;

public class Patient extends User {

    private String birthPlace;
    private String province;
    Date birthDate = new Date();
    private String domicile;
    private char sex;
    private String taxIdCode;
    private int refDoc;

    //Constructor
    public Patient(String name, String surname, String email, String numTelephone, String username, String password, String birthPlace,
                    String province, Date birthDate, String domicile, char sex, String taxIdCode, int refDoc){
        super(name,username,email,numTelephone,username,password);
        this.birthPlace = birthPlace;
        this.province = province;
        this.birthDate = birthDate;
        this.domicile = domicile;
        this.sex = sex;
        this.taxIdCode = taxIdCode;
        this.refDoc = refDoc;
    }

    // Get and set methods
    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getTaxIdCode() {
        return taxIdCode;
    }

    public void setTaxIdCode(String taxIdCode) {
        this.taxIdCode = taxIdCode;
    }

    public int getRefDoc() {
        return refDoc;
    }

    public void setRefDoc(int refDoc) {
        this.refDoc = refDoc;
    }


}
