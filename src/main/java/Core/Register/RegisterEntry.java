package Core.Register;

public class RegisterEntry {
    private Object value;
    private String Q;

    public RegisterEntry() {
        this.value = 0;
        this.Q = "";
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getQ() {
        return Q;
    }

    public void setQ(String qj) {
        this.Q= qj;
    }
}