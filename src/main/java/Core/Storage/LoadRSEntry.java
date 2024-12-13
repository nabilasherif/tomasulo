package Core.Storage;

import Core.Instruction.Instruction;
import Core.Instruction.InstructionType;
import Core.Operations;

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

    public double execute() {
        InstructionType op = instruction.getOp();
        switch (op) {
            case LW: return Operations.LW(this.getAddress());
            case LD: return Operations.LD(this.getAddress());
            case L_S: return Operations.L_S(this.getAddress());
            case L_D: return Operations.L_D(this.getAddress());
            default:
                return 0.0;
        }
    }

    public void clear() {
        super.clear();
        this.address = null;
    }

    public void printRSDetails() {
        System.out.println("Tag: " + this.getTag());
        System.out.println("Busy: " + this.isBusy());
        System.out.println("Remaining Cycles: " + this.getRemainingCycles());
        System.out.println("Instruction Status: " + (this.instruction != null ? this.instruction.getStatus() : "No Instruction"));
        System.out.println("Address:" + this.address);
        System.out.println("Result: " + this.getResult());
        System.out.println("-------------------------");
    }
}