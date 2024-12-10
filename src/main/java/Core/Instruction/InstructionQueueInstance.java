package Core.Instruction;

import java.util.List;
import java.util.ArrayList;

public class InstructionQueueInstance {
    private InstructionType op;
    private String dest;
    private String j;
    private String k;
    private int issue;
    private List<Integer> execution;
    private int write;

    public InstructionQueueInstance(InstructionType op, String dest, String j, String k) {
        this.op = op;
        this.dest = dest;
        this.j = j;
        this.k = k;
        this.execution = new ArrayList<>();
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

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    public List<Integer> getExecution() {
        return execution;
    }

    public void setExecution(List<Integer> execution) {
        this.execution = execution;
    }

    public int getWrite() {
        return write;
    }

    public void setWrite(int write) {
        this.write = write;
    }
}