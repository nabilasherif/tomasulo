package Core.Storage;

import Core.Instruction.InstructionType;
import Core.Instruction.Instruction;


public class ArithmeticRSEntry extends RSBaseEntry {
    private InstructionType op;
    private Object vj;
    private Object vk;
    private String qj;
    private String qk;

    public ArithmeticRSEntry(String name, Instruction instruction) {
        super(name, instruction);
        this.qj = "";
        this.qk = "";
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


    public double execute(){
        //TODO: ASK ABOUT WHETHER BNE AND BEQ ENTER THE RESERVATION STATION OR NOT
        if(instruction.getOp() == InstructionType.ADD_D || op == InstructionType.ADD_S || op == InstructionType.DADDI )
            return (Double)this.vj + (Double) this.vk;
        if(instruction.getOp() == InstructionType.SUB_D ||op == InstructionType.SUB_S || op == InstructionType.DSUBI )
            return (Double)this.vj - (Double) this.vk;
        // this is for now until we know fe branches hena wala la2
        return 0.0;
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