package Core.Storage;

public class LoadRSEntry extends RSBaseEntry {
    private Integer address;

    public LoadRSEntry(String tag) {
        super(tag);
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return this.address;
    }

}