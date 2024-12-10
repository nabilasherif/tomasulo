package Core.Storage;

public class Bus {
    private String tag;
    private Object value;

    public Bus() {
        this.tag = "";
        this.value = 0;
    }

    public String getTag() {
        return tag;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void clear(){
        this.tag = "";
        this.value = 0;
    }
}