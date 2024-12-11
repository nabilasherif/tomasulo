package Core.Storage;

import Core.Instruction.InstructionQueueInstance;

public class LoadRSEntry extends RSBaseEntry {
    private Integer address;

    public LoadRSEntry(String tag, InstructionQueueInstance instruction) {
        super(tag,instruction);
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return this.address;
    }
}