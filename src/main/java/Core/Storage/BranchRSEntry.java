package Core.Storage;

import Core.Instruction.InstructionType;
import Core.Instruction.Instruction;

public class BranchRSEntry extends RSBaseEntry {
    private InstructionType op;
    private Object vj;
    private Object vk;
    private String qj;
    private String qk;

    public BranchRSEntry(String name, Instruction instruction) {
        super(name, instruction);
    }

    public InstructionType getOp() {
        return op;
    }

    public void setOp(InstructionType op) {
        this.op = op;
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

    public void clear() {
        super.setBusy(false);
    }

    public boolean evaluateBranch() {
        //TODO: Evaluate the branch instruction
        return false;
    }

    public void printRSDetails() {
        System.out.println("Tag: " + this.getTag());
        System.out.println("Busy: " + this.isBusy());
        System.out.println("Remaining Cycles: " + this.getRemainingCycles());
        System.out.println("Instruction Status: " + (this.instruction != null ? this.instruction.getStatus() : "No Instruction"));
        System.out.println("Vj: " + this.getVj());
        System.out.println("Vk: " + this.getVk());
        System.out.println("Qj: " + this.getQj());
        System.out.println("Qk: " + this.getQk());
        System.out.println("Result: " + this.result);
        System.out.println("-------------------------");
    }
}
