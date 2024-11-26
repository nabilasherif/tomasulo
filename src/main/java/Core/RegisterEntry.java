package Core;

public class RegisterEntry {
    private int value;
    private boolean busy;
    private String waitingStation;

    public RegisterEntry() {
        this.value = 0;
        this.busy = false;
        this.waitingStation = "";
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getWaitingStation() {
        return waitingStation;
    }

    public void setWaitingStation(String waitingStation) {
        this.waitingStation = waitingStation;
    }
}