package Core;
import Core.Instruction.*;
import Core.Register.*;
import Core.Storage.*;
import java.util.*;

public class Main {

    public static int blockSize = 3;
    public static int cacheSize= 3;
    public static Memory memory=new Memory(2024, blockSize);
    public static Cache cache=new Cache(cacheSize,blockSize,memory);
    public static ArrayList<ArithmeticRSEntry> addSubRS = new ArrayList<>();
    public static ArrayList<ArithmeticRSEntry> mulDivRS = new ArrayList<>();
    public static ArrayList<LoadRSEntry> loadRS = new ArrayList<>();
    public static ArrayList<StoreRSEntry> storeRS = new ArrayList<>();
    public static RegisterFile registerFile3=new RegisterFile();
    public static HashMap<String, RegisterEntry> registerFile = new RegisterFile().getRegisters();


    // From the GUI
    public static int addReservationStationSize= 3;
    public static int addLatency = 4;
    public static int addFPLatency = 4;
    public static int subLatency=4;
    public static int subFPLatency=4;
    public static int mulReservationStationSize= 3;
    public static int mulLatency = 4;
    public static int mulFPLatency = 4;
    public static int divLatency = 5;
    public static int divFPLatency = 5;
    public static int loadReservationStationSize= 3;
    public static int loadLatency = 4;
    public static int loadPenalty = 8;
    public static int storeReservationStationSize= 3;
    public static int storeLatency = 4;

    public static boolean checkAnEmptyStation(List<? extends RSBaseEntry> reservationStation) {
        for (RSBaseEntry rs : reservationStation) {
            if (!rs.isBusy()) {
                return true;
            }
        }
        return false;
    }

    public static boolean allStationsEmpty(){
        for (RSBaseEntry rs : addSubRS) {
            if (rs.isBusy()) {
                return false;
            }
        }
        return true;
    }

    public static String addToRS(Instruction instruction){
        // TODO: REPEAT THIS FOR EACH TYPE OF RESERVATION STATION
        // TODO: MAKE THIS NEATER FOR THE LOVE OF GOD
        for (int i = 0; i < addSubRS.size(); i++) {
            // If my reservation station's current entry is not busy, add the instruction to the reservation station
            if (!addSubRS.get(i).isBusy()) {
                addSubRS.get(i).setValues(true, addLatency,instruction);
                String j = addSubRS.get(i).instruction.getJ();
                String k = addSubRS.get(i).instruction.getK();

                double jvalue = registerFile.get(j).getValue();
                double kvalue = registerFile.get(k).getValue();
                String kQ = registerFile.get(k).getQ();
                String jQ = registerFile.get(j).getQ();
                if(kQ.equals("0")){
                    addSubRS.get(i).setVk(kvalue);
                }else{
                    addSubRS.get(i).setQk(kQ);
                }
                if(jQ.equals("0")){
                    addSubRS.get(i).setVj(jvalue);
                }else{
                    addSubRS.get(i).setQj(jQ);
                }
                return addSubRS.get(i).getTag();
            }
        }
        return "";
    }

    public static HashSet<String> executeAllExcept(String tag) {
        HashSet<String> justFinished = new HashSet<>();

        for (ArithmeticRSEntry currentRS : addSubRS) {
            if (currentRS.getTag().equals(tag))
                continue;

            if (currentRS.getVj() != null && currentRS.getVk() != null) {
                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    // based on operation
                    // currentRS.result= currentRS.execute();
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (ArithmeticRSEntry currentRS : mulDivRS) {
            if (currentRS.getTag().equals(tag))
                continue;

            if (currentRS.getVj() != null && currentRS.getVk() != null) {
                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    // based on opppp
                    // currentRS.result= currentRS.execute();
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (StoreRSEntry currentRS : storeRS) {
            if (currentRS.getTag().equals(tag))
                continue;

            if (currentRS.getValue() != null) {
                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    // call the correct write to cache with the address
                    // we may ignore adding to just finshed here as store actually doesn't wb
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (LoadRSEntry currentRS : loadRS) {
            if (currentRS.getTag().equals(tag))
                continue;

            currentRS.instruction.setStatus(Status.EXECUTING);
            currentRS.remainingCycles--;

            if (currentRS.remainingCycles == 0) {
                currentRS.instruction.setStatus(Status.EXECUTED);
                // call the correct write to cache with the address
                justFinished.add(currentRS.getTag());
            }
        }

        return justFinished;
    }


    public static void writeToBusExcept(String exception ){
        // TODO: MAKE SURE THAT YOU DO NOT WRITE MORE THAN ONE THING AT ONCE
        // TODO: WRITE BACK IN REGISTER FILE
        //TODO: THINK BIG THOUGHTS
        // Go through all reservation stations and see if anything should be placed in bus
        for(ArithmeticRSEntry rs : addSubRS){
            if(rs.getTag().equals(exception))
                continue;
            // If the instruction is done executing, begin writing back to all reservation stations
            if(rs.instruction != null && rs.instruction.getStatus().equals(Status.EXECUTED)){
                String tag = rs.getTag();
                double value = rs.result;
                for(ArithmeticRSEntry rs2 : addSubRS){
                    if(rs2.getQj().equals(tag) && rs2!= rs){
                        rs2.setVj(value);
                        rs2.setQj(null);
                    }
                    if(rs2.getQk().equals(tag ) && rs2!= rs){
                        rs2.setVk(value);
                        rs2.setQk(null);
                    }
                }
                // do the same with reg file
                rs.instruction.setStatus(Status.WRITTEN_BACK);
                rs.setBusy(false);
            }
        }
    }

    public static void initReservationStations(){
        for(int i =0; i < addReservationStationSize; i++){
            addSubRS.add(new ArithmeticRSEntry("A" + i, null));
        }

    }

    public static void main(String[] args) {

        String filePath = "src/main/java/Core/program.txt";
        List<Instruction> instructionQueue = InstructionFileParser.fillInstructionsQueue(filePath);
        int cycle = 0;
        int pc = 0;


        // TODO: INITIALISE ALL INPUTS FUNCTION FOR INITIALISING ALL GLOBAL VARIABLES
        initReservationStations();

        for (Instruction queueInstance : instructionQueue) {
            System.out.println("Op: " + queueInstance.getOp()
                    + ", Dest: " + queueInstance.getDest()
                    + ", J: " + queueInstance.getJ()
                    + ", K: " + queueInstance.getK()
                    + ", Issue Cycle: " + queueInstance.getIssue()
                    + ", Write Cycle: " + queueInstance.getWrite());
        }

        // TODO HANDLE INTEGRATION WITH FE
        while(pc < instructionQueue.size() || !allStationsEmpty()){
            String tag= "";
            cycle++;
            if (pc < instructionQueue.size()) {
                Instruction currentInstruction = instructionQueue.get(pc);
                Instruction clonedInstruction = currentInstruction.deepClone();
                // TODO: GO THROUGH EACH TYPE OF RESERVATION STATION BASED ON THE INCOMING INSTRUCTION TYPE

                // checking if there is an empty reservation station slot
                if (checkAnEmptyStation(addSubRS)) {

                    tag = addToRS(clonedInstruction);
                    clonedInstruction.setStatus(Status.ISSUED);
                    pc++;
                }
            }

            // If i just issued something in the current cycle, I don't want to execute it in the same cycle
            // If i just executed something in the current cycle, I don't want to write it back in the same cycle

            executeAllExcept(tag);
            // TODO FIX THE WRITE TO BUS
            writeToBusExcept(tag);
            System.out.println("Cycle " + cycle);

            for (ArithmeticRSEntry addR : addSubRS) addR.printRSDetails();



        }



    }
}