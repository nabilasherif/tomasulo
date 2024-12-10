package Core.Instruction;

import java.util.List;
import java.util.ArrayList;

public class InstructionQueueInstance {
    public Instruction instruction;
    private int issue;
    private List<Integer> execution;
    private int write;

    public InstructionQueueInstance(Instruction instruction) {
        this.instruction = instruction;
        this.execution = new ArrayList<>();
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
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