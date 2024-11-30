package Core.Storage;

public class LoadBufferEntry extends ReservationStationBaseEntry {
    private int effectiveAddress;

    public LoadBufferEntry(String name) {
        super(name);
    }

    public void setEffectiveAddress(int address) {
        this.effectiveAddress = address;
    }

    public int getEffectiveAddress() {
        return effectiveAddress;
    }

    @Override
    public void clear() {
        super.clear();
        this.effectiveAddress = 0;
    }
}