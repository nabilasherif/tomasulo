package Core.Storage;

import Core.Instruction.Instruction;

public class LoadRSEntry extends RSBaseEntry {
    private Integer address;

    public LoadRSEntry(String tag, Instruction instruction) {
        super(tag,instruction);
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return this.address;
    }
}