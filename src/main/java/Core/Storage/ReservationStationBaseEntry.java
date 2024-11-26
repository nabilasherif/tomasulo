package Core.Storage;

public class ReservationStationBaseEntry {
    private String name;
    private int busy;
    private String destination;
    //private String currentInstruction;

    public ReservationStationBaseEntry(String name) {
        this.name = name;
        this.busy = 0;
        this.destination = "";
        //this.currentInstruction = "";
    }
    public void clear() {
        this.busy = 0;
        this.destination = null;
        //this.currentInstruction = null;
    }

    public String getName() {
        return name;
    }

    public int isBusy() {
        return busy;
    }

    /*public String getCurrentInstruction() {
        return currentInstruction;
    }*/
}