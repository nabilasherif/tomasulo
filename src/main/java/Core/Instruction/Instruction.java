package Core.Instruction;

public class Instruction {
    private InstuctionType op;
    private String dest;
    private String j;
    private String k;

    public Instruction(InstuctionType op, String dest, String j, String k) {
        this.op = op;
        this.dest = dest;
        this.j = j;
        this.k = k;
    }

    public InstuctionType getOp() {
        return op;
    }

    public void setOp(InstuctionType op) {
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
}