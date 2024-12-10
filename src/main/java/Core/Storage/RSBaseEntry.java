package Core.Storage;

public class RSBaseEntry {
    private String tag;
    private int busy;
    private int remainingCycles;

    public RSBaseEntry(String tag) {
        this.tag = tag;
        this.busy = 0;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int isBusy() {
        return busy;
    }

    public int getRemainingCycles() {
        return remainingCycles;
    }

    public void setRemainingCycles(int remainingCycles) {
        this.remainingCycles = remainingCycles;
    }

    //we can make this toggle between 0 and 1 instead
    public void setBusy(int busy) {
        this.busy = busy;
    }

    public void clear(){
        this.busy = 0;
    }


}