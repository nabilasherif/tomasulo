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

    public void printRSDetails() {
        System.out.println("Tag: " + this.getTag());
        System.out.println("Busy: " + this.isBusy());
        System.out.println("Remaining Cycles: " + this.getRemainingCycles());
        System.out.println("Instruction Status: " + (this.instruction != null ? this.instruction.getStatus() : "No Instruction"));
        System.out.println("Address:" + this.address);
        System.out.println("Result: " + this.result);
        System.out.println("-------------------------");
    }
}