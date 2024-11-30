package Core.Storage;

public class ReservationStationEntry extends ReservationStationBaseEntry {
    private Object vj;
    private Object vk;
    private String qj="0";
    private String qk="0";

    public ReservationStationEntry(String name) {
        super(name);
        this.vj = null;
        this.vk = null;
        this.qj = "0";
        this.qk = "0";
    }

    public void setVj(Object value) {
        this.vj = value;
    }

    public Object getVj() {
        return vj;
    }

    public void setVk(Object value) {
        this.vk = value;
    }

    public Object getVk() {
        return vk;}

    public void setQj(String qj) {
        this.qj = qj;
    }
    public String getQj() {
        return qj;
    }
    public void setQk(String qk) {
        this.qk = qk;
    }
    public String getQk() {
        return qk;
    }

    @Override
    public void clear() {
        super.clear();
        this.vj = null;
        this.vk = null;
        this.qj = "0";
        this.qk = "0";
    }
}