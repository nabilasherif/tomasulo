package Core;
import Core.Instruction.*;
import Core.Register.*;
import Core.Storage.*;
import java.util.*;

public class Main {
    public static int addLatency;
    public static int subLatency;
    public static int multLatency;
    public static int divLatency;
    public static int storeLatency;
    public static int loadLatency=1;
    public static int loadPenalty=5;
    public static int addReservationStationSize;
    public static int multReservationStationSize;
    public static int intReservationStationSize;
    public static int storeBufferSize;
    public static int loadBufferSize;
    public static int blockSize;
    public static int cacheSize;
    public static Memory memory=new Memory(2024, blockSize);
    public static Cache cache=new Cache(cacheSize,blockSize,memory);
    public static RegisterFile registerFile=new RegisterFile();
    public static Queue<Bus> bus=new LinkedList<>();
    public static ArithmeticRSEntry[]  addSubFP;
    public static ArithmeticRSEntry[]  mulDivFP;
    public static ArithmeticRSEntry[]  integerRS;
    public static LoadRSEntry[]  loadBuffer;
    public static StoreRSEntry[] storeBuffer;
    public static InstructionQueueInstance[] instructionQueue;

    public static void main(String[] args) {

        //this is how a station is created
        RegisterFile registerFileInstance = new RegisterFile();
        List<ArithmeticRSEntry> adders = new ArrayList<>(5);

        String filePath = "src/main/java/Core/test1.txt";
        List<InstructionQueueInstance> instructionQueue = InstructionFileParser.fillInstructionsQueue(filePath);

        for (InstructionQueueInstance queueInstance : instructionQueue) {
            System.out.println("Op: " + queueInstance.getOp()
                    + ", Dest: " + queueInstance.getDest()
                    + ", J: " + queueInstance.getJ()
                    + ", K: " + queueInstance.getK()
                    + ", Issue Cycle: " + queueInstance.getIssue()
                    + ", Write Cycle: " + queueInstance.getWrite());
        }
//        System.out.println("Testing StoreBufferEntry:");
//        StoreRSEntry storeEntry = new StoreRSEntry("S1");
//        storeEntry.setAddress(100);
//        storeEntry.setValue(12);
//
//
//        System.out.println("Name: " + storeEntry.getTag());
//        System.out.println("Effective Address: " + storeEntry.getAddress());
//        System.out.println("Value to Store: " + storeEntry.getValue());
//        System.out.println("is busy"+ storeEntry.isBusy());
//
//        storeEntry.clear();
//        System.out.println("After clearing:");
//        System.out.println("Effective Address: " + storeEntry.getAddress());
//        System.out.println("Value to Store: " + storeEntry.getValue());
//        System.out.println("is busy"+ storeEntry.isBusy());
//
//        System.out.println("\nTesting LoadBufferEntry:");
//
//        LoadRSEntry loadEntry = new LoadRSEntry("L1");
//        loadEntry.setAddress(200);
//
//        System.out.println("Name: " + loadEntry.getTag());
//        System.out.println("Effective Address: " + loadEntry.getAddress());
//
//        loadEntry.clear();
//        System.out.println("After clearing:");
//        System.out.println("Effective Address: " + loadEntry.getAddress());
//
//        System.out.println("\nTesting ReservationStationEntry:");
//
//        ArithmeticRSEntry resEntry = new ArithmeticRSEntry("A1");
//        adders.add(resEntry);
//        resEntry.setVj(-1.7);
//        resEntry.setVk(6.3);
//        resEntry.setQj("0");
//        resEntry.setQk("L1");
//        System.out.println("Adders first entry:  " + adders.get(0).getTag());
//        System.out.println("Adders size: " + adders.size());
//        System.out.println("Tag: " + resEntry.getTag());
//        System.out.println("Vj: " + resEntry.getVj());
//        System.out.println("Vk: " + resEntry.getVk());
//        System.out.println("Qj: " + resEntry.getQj());
//        System.out.println("Qk: " + resEntry.getQk());
//        resEntry.setBusy(1);
//        System.out.println("is busy"+ resEntry.isBusy());
//
//        resEntry.clear();
//        System.out.println("After clearing:");
//        System.out.println("Vj: " + resEntry.getVj());
//        System.out.println("Vk: " + resEntry.getVk());
//        System.out.println("Qj: " + resEntry.getQj());
//        System.out.println("Qk: " + resEntry.getQk());
//        resEntry.clear();
//        System.out.println("is busy"+ resEntry.isBusy());
    }
}