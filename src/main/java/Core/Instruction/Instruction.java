package Core.Instruction;

public class Instruction {
    private InstructionType op;
    private String dest;
    private String j;
    private String k;
    private boolean isLoop;

    public Instruction(InstructionType op, String dest, String j, String k, boolean isLoop) {
        this.op = op;
        this.dest = dest;
        this.j = j;
        this.k = k;
        this.isLoop = isLoop;
    }

    public InstructionType getOp() {
        return op;
    }

    public void setOp(InstructionType op) {
        this.op = op;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getJ() {
        return j;
    }

    public void setJ(String j) {
        this.j = j;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public boolean isLoop() {
        return isLoop;
    }
}