package Core.Storage;

public class StoreBufferEntry extends ReservationStationBaseEntry {
    private int effectiveAddress;
    private Object valueToStore;

    public StoreBufferEntry(String name) {
        super(name);
    }

    public void setEffectiveAddress(int address) {
        this.effectiveAddress = address;
    }

    public int getEffectiveAddress() {
        return effectiveAddress;
    }

    public void setValueToStore(Object value) {
        this.valueToStore = value;
    }

    public Object getValueToStore() {
        return valueToStore;
    }

    @Override
    public void clear() {
        super.clear();
        this.effectiveAddress = 0;;
        this.valueToStore = null;
    }
}