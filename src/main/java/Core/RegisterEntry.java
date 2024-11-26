package Core;

public class RegisterEntry {
    private Object value;
    private boolean busy;
    private String Qj;

    public RegisterEntry() {
        this.value = 0;
        this.busy = false;
        this.Qj = "";
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getQj() {
        return Qj;
    }

    public void setQj(String qj) {
        this.Qj = qj;
    }
}