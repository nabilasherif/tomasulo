package Core.Storage;

import Core.Instruction.Instruction;
import Core.Instruction.InstructionType;
import Core.Operations;

public class StoreRSEntry extends RSBaseEntry {

    private Integer address;
    private Object value;
    private String q;

    public StoreRSEntry(String tag, Instruction instruction) {super(tag, instruction);}

    public void setAddress(int address) {this.address = address;}

    public int getAddress() {return address;}

    public void setValue(Object value) {this.value = value;}

    public Object getValue() {return value;}

    public void setQ(String q) {this.q = q;}

    public String getQ() {return q;}

    public void printRSDetails() {
        System.out.println("Tag: " + this.getTag());
        System.out.println("Busy: " + this.isBusy());
        System.out.println("Remaining Cycles: " + this.getRemainingCycles());
        System.out.println("Instruction Status: " + (this.instruction != null ? this.instruction.getStatus() : "No Instruction"));
        System.out.println("Q: " + this.getQ());
        System.out.println("Value: " + this.getValue());
        System.out.println("Address: " + (this.address != null? this.getAddress(): "not assigned yet"));
        System.out.println("Result: " + this.getResult());
        System.out.println("-------------------------");
    }

    public void execute() {
        InstructionType op = instruction.getOp();
        switch (op) {
            case SW:  Operations.SW(this.instruction.getDest(), this.getAddress()); break;
            case SD: Operations.SD(this.instruction.getDest(), this.getAddress()); break;
            case S_S:Operations.S_S(this.instruction.getDest(), this.getAddress());break;
            case S_D: Operations.S_D(this.instruction.getDest(), this.getAddress());break;
        }
    }
}