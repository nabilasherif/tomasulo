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
    public static  ArrayList<ArithmeticRSEntry> addRS = new ArrayList<>();
    public static HashMap<String, RegisterEntry> registerFile2 = new RegisterFile().getRegisters();
    public static RegisterFile registerFile=new RegisterFile();
    public static int addReservationStationSize= 3;
    public static int addLatency = 4; // TODO: MAKE THIS AN INPUT

    public static boolean checkAnEmptyStation(List<? extends RSBaseEntry> reservationStation) {
        for (RSBaseEntry rs : reservationStation) {
            if (!rs.isBusy()) {
                return true;
            }
        }
        return false;
    }

    public static boolean allStationsEmpty(){
        for (RSBaseEntry rs : addRS) {
            if (rs.isBusy()) {
                return false;
            }
        }
        return true;
    }

    public static String addToRS(InstructionQueueInstance instruction){
        // TODO: REPEAT THIS FOR EACH TYPE OF RESERVATION STATION
        // TODO: MAKE THIS NEATER FOR THE LOVE OF GOD
        for (int i=0; i < addRS.size(); i++) {
            // If my reservation station's current entry is not busy, add the instruction to the reservation station
            if (!addRS.get(i).isBusy()) {
                addRS.get(i).setValues(true, addLatency,instruction);
                String j = addRS.get(i).instruction.getJ();
                String k = addRS.get(i).instruction.getK();

                double jvalue = registerFile2.get(j).getValue();
                double kvalue = registerFile2.get(k).getValue();
                String kQ = registerFile2.get(k).getQ();
                String jQ = registerFile2.get(j).getQ();
                if(kQ.equals("0")){
                    addRS.get(i).setVk(kvalue);
                }else{
                    addRS.get(i).setQk(kQ);
                }
                if(jQ.equals("0")){
                    addRS.get(i).setVj(jvalue);
                }else{
                    addRS.get(i).setQj(jQ);
                }
                return addRS.get(i).getTag();
            }
        }
        return "";
    }

    public static void executeAllExcept(String tag){
        for( ArithmeticRSEntry currentRS : addRS){
            // To prevent executing something that has just been issued
            if(currentRS.getTag().equals(tag))
                continue;
            // If the remaining cycles = 0, change the status to executed
            if(currentRS.remainingCycles == 0){
                currentRS.instruction.setStatus(Status.EXECUTED);
                currentRS.result= currentRS.execute();
            }
            // If the vj and the vk are available, start executing and decrease the remaning cycles by 1
            else if(currentRS.getVj()!= null && currentRS.getVk()!= null){
                currentRS.remainingCycles--;
                currentRS.instruction.setStatus(Status.EXECUTING);
            }
        }
    }

    public static void writeToBusExcept(String exception ){
        // TODO: MAKE SURE THAT YOU DO NOT WRITE MORE THAN ONE THING AT ONCE
        // TODO: WRITE BACK IN REGISTER FILE
        // Go through all reservation stations and see if anything should be placed in bus
        for(ArithmeticRSEntry rs : addRS){
            if(rs.getTag().equals(exception))
                continue;
            // If the instruction is done executing, begin writing back to all reservation stations
            if(rs.instruction != null && rs.instruction.getStatus().equals(Status.EXECUTED)){
                String tag = rs.getTag();
                double value = rs.result;
                for(ArithmeticRSEntry rs2 : addRS){
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
            addRS.add(new ArithmeticRSEntry("A" + i, null));
        }
    }


    public static void main(String[] args) {

        String filePath = "src/main/java/Core/test1.txt";
        List<InstructionQueueInstance> instructionQueue = InstructionFileParser.fillInstructionsQueue(filePath);
        int cycle = 0;
        int pc = 0;


        // TODO: INITIALISE ALL INPUTS FUNCTION FOR INITIALISING ALL GLOBAL VARIABLES
        initReservationStations();

        for (InstructionQueueInstance queueInstance : instructionQueue) {
            System.out.println("Op: " + queueInstance.getOp()
                    + ", Dest: " + queueInstance.getDest()
                    + ", J: " + queueInstance.getJ()
                    + ", K: " + queueInstance.getK()
                    + ", Issue Cycle: " + queueInstance.getIssue()
                    + ", Write Cycle: " + queueInstance.getWrite());
        }

        /// HANDLE INTEGRATION WITH FE
        while(pc < instructionQueue.size() || !allStationsEmpty()){

            String tag= "";
            boolean issued = false;
            cycle++;
            if (pc < instructionQueue.size()) {
                InstructionQueueInstance currentInstruction = instructionQueue.get(pc);
                InstructionQueueInstance clonedInstruction = currentInstruction.deepClone();
                // TODO: GO THROUGH EACH TYPE OF RESERVATION STATION BASED ON THE INCOMING INSTRUCTION TYPE

                // checking if there is an empty reservation station slot
                if (checkAnEmptyStation(addRS)) {

                    tag = addToRS(clonedInstruction);
                    clonedInstruction.setStatus(Status.ISSUED);
                    pc++;
                    issued = true;
                }
            }

            // If i just issued something in the current cycle, I don't want to execute it in the same cycle
            // If i just executed something in the current cycle, I don't want to write it back in the same cycle

            executeAllExcept(tag);
            writeToBusExcept(tag);
            System.out.println("Cycle " + cycle);

            for (ArithmeticRSEntry addR : addRS) addR.printRSDetails();



        }



    }
}
