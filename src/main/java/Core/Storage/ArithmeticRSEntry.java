package Core.Storage;

import Core.Instruction.InstructionType;

public class ArithmeticRSEntry extends RSBaseEntry {
    private InstructionType op;
    private Object vj;
    private Object vk;
    private String qj;
    private String qk;

    public ArithmeticRSEntry(String name) {
        super(name);
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
        super.setBusy(0);
    }

}