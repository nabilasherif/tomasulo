package Core.Storage;

import Core.Instruction.Instruction;

public class RSBaseEntry {

    private String tag;
    private boolean busy;
    public int remainingCycles;
    public Instruction instruction;
    private double result;

    public RSBaseEntry(String tag, Instruction instruction) {
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

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public void clear(){
        this.busy = false;
        this.remainingCycles = Integer.MAX_VALUE;
        instruction  = null;
        result= 0;
    }

    public void setValues(boolean busy, int remainingCycles, Instruction instruction) {
        this.busy = busy;
        this.remainingCycles = remainingCycles;
        this.instruction = instruction;
    }

    public void setResult(double result) {this.result = result;}

    public double getResult() {return this.result;}
}