package Core.Storage;

import Core.Instruction.InstructionQueueInstance;

public class RSBaseEntry {
    private String tag;
//    private int remainingCycles;
    private boolean busy;
    public int remainingCycles;
    public InstructionQueueInstance instruction;
    public double result;


    public RSBaseEntry(String tag, InstructionQueueInstance instruction) {
        this.tag = tag;
        this.busy = false;
        this.remainingCycles = Integer.MAX_VALUE;
        this.instruction = instruction;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isBusy() {
        return busy;
    }

    public int getRemainingCycles() {
        return remainingCycles;
    }

    public void setRemainingCycles(int remainingCycles) {
        this.remainingCycles = remainingCycles;
    }

    //we can make this toggle between 0 and 1 instead
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public void clear(){
        this.busy = false;
        this.tag = null;
        this.remainingCycles = Integer.MAX_VALUE;
        this.instruction = null;
    }

    public void setValues(boolean busy, int remainingCycles, InstructionQueueInstance instruction) {
        this.busy = busy;
        this.remainingCycles = remainingCycles;
        this.instruction = instruction;
    }


}