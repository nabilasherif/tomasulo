package Core.Storage;

import Core.Instruction.InstructionType;
import Core.Instruction.Instruction;
import Core.Operations;

public class ArithmeticRSEntry extends RSBaseEntry {
    private InstructionType op;
    private Object vj;
    private Object vk;
    private String qj;
    private String qk;

    public ArithmeticRSEntry(String name, Instruction instruction) {
        super(name, instruction);
        this.qj = "0";
        this.qk = "0";
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
        super.clear();
        this.vj = null;
        this.vk = null;
        this.qj = "0";
        this.qk = "0";
    }

    public double execute(){
        InstructionType op = instruction.getOp();
        switch (op) {
            case ADD_D, DADDI: return Operations.ADD_D((Double)this.vj, (Double)this.vk);
            case ADD_S: return Operations.ADD_S((Double)this.vj, (Double)this.vk);
            case DSUBI, SUB_D: return Operations.SUB_D((Double)this.vj, (Double)this.vk);
            case SUB_S: return Operations.SUB_S((Double)this.vj, (Double)this.vk);
            case MUL_D: return Operations.MUL_D((Double)this.vj, (Double)this.vk);
            case MUL_S: return Operations.MUL_S((Double)this.vj,(Double)this.vk);
            case DIV_D: return Operations.DIV_D((Double)this.vj, (Double)this.vk);
            case DIV_S: return Operations.DIV_S((Double)this.vj, (Double)this.vk);
            case BNE: if(Operations.BNE((Double)this.vj, (Double)this.vk)) return 1.0; else return 0.0;
            case BEQ: if(Operations.BEQ((Double)this.vj, (Double)this.vk)) return 1.0; else return 0.0;
            default:
                return 0.0;
        }
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
        System.out.println("Result: " + this.getResult());
        System.out.println("-------------------------");
    }
}