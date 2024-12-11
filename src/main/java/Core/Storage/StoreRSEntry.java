package Core.Storage;

import Core.Instruction.Instruction;

public class StoreRSEntry extends RSBaseEntry {
    private Integer address;
    private Object value;
    private String q;

    public StoreRSEntry(String tag, Instruction instruction) {
        super(tag, instruction);
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getQ() {
        return q;
    }
}