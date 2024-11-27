package Core.Bus;

public class Bus {
    private String tag;
    private double value;

    public Bus() {
        this.tag = "";
        this.value = 0;
    }

    public String getTag() {
        return tag;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
