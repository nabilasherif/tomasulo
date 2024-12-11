package Core.Register;

public class RegisterEntry {
    private double value;
    private String q;

    public RegisterEntry() {
        this.value = 0;
        this.q = "0";
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public void isValid(){
        this.q = "0";
    }
}