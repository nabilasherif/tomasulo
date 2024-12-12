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
    //do we add a store penalty?
    public static ArrayList<BranchRSEntry> branchRS = new ArrayList<>();
    public static int branchReservationStationSize= 3;
    public static int branchLatency = 4;
    public static int branchPenalty = 1;
    public static  Queue<RSBaseEntry> writeBackQueue = new LinkedList<>();
    public static List<Instruction> instructionQueue = new ArrayList<>();

    public static boolean checkAnEmptyStation(List<? extends RSBaseEntry> reservationStation) {
        for (RSBaseEntry rs : reservationStation) {
            if (!rs.isBusy()) {
                return true;
            }
        }
        return false;
    }

    public static boolean allStationsEmpty() {
        for (RSBaseEntry rs : addSubRS) {
            if (rs.isBusy()) return false;
        }
        for (RSBaseEntry rs : mulDivRS) {
            if (rs.isBusy()) return false;
        }
        for (RSBaseEntry rs : storeRS) {
            if (rs.isBusy()) return false;
        }
        for (RSBaseEntry rs : loadRS) {
            if (rs.isBusy()) return false;
        }
        for (RSBaseEntry rs : branchRS) {
            if (rs.isBusy()) return false;
        }
        return true;
    }

    private static String addToAddSubRS(Instruction instruction) {
        for (int i = 0; i < addSubRS.size(); i++) {
            // If my reservation station's current entry is not busy, add the instruction to the reservation station
            if (!addSubRS.get(i).isBusy()) {
                if(instruction.getOp() == InstructionType.ADD_D || instruction.getOp() == InstructionType.ADD_S || instruction.getOp() == InstructionType.DADDI )
                    addSubRS.get(i).setValues(true, addLatency, instruction);
                else
                    addSubRS.get(i).setValues(true, subLatency, instruction);
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

    private static String addToMulDivRS(Instruction instruction) {
        for (int i = 0; i < mulDivRS.size(); i++) {
            // If my reservation station's current entry is not busy, add the instruction to the reservation station
            if (!mulDivRS.get(i).isBusy()) {
                if(instruction.getOp() == InstructionType.MUL_S || instruction.getOp() == InstructionType.MUL_D )
                    mulDivRS.get(i).setValues(true, mulLatency, instruction);
                else
                    mulDivRS.get(i).setValues(true, divLatency, instruction);
                String j = mulDivRS.get(i).instruction.getJ();
                String k = mulDivRS.get(i).instruction.getK();

                double jvalue = registerFile.get(j).getValue();
                double kvalue = registerFile.get(k).getValue();
                String kQ = registerFile.get(k).getQ();
                String jQ = registerFile.get(j).getQ();
                if(kQ.equals("0")){
                    mulDivRS.get(i).setVk(kvalue);
                }else{
                    mulDivRS.get(i).setQk(kQ);
                }
                if(jQ.equals("0")){
                    mulDivRS.get(i).setVj(jvalue);
                }else{
                    mulDivRS.get(i).setQj(jQ);
                }
                return mulDivRS.get(i).getTag();
            }
        }
        return "";
    }

    private static String addToStoreRS(Instruction instruction) {
        for (int i = 0; i < storeRS.size(); i++) {
            // If my reservation station's current entry is not busy, add the instruction to the reservation station
            if (!storeRS.get(i).isBusy()) {
                storeRS.get(i).setValues(true, storeLatency, instruction);
                String dest = storeRS.get(i).instruction.getDest();// f3
                String j = storeRS.get(i).instruction.getJ();//100
                storeRS.get(i).setAddress(Integer.parseInt(j));

                double destValue = registerFile.get(dest).getValue();
                String Q = registerFile.get(dest).getQ();
                if(Q.equals("0")){
                    storeRS.get(i).setValue(destValue);
                }else{
                    storeRS.get(i).setQ(Q);
                }
                return storeRS.get(i).getTag();
            }
        }
        return "";
    }

    private static String addToLoadRS(Instruction instruction) {
        for (int i = 0; i < loadRS.size(); i++) {
            // If my reservation station's current entry is not busy, add the instruction to the reservation station
            if (!loadRS.get(i).isBusy()) {
                loadRS.get(i).setValues(true, loadLatency, instruction);
                String dest = loadRS.get(i).instruction.getDest();// f3
                String j = loadRS.get(i).instruction.getJ();//100
                loadRS.get(i).setAddress(Integer.parseInt(j));
                return loadRS.get(i).getTag();
            }
        }
        return "";
    }

    private static String addToBranchRS(Instruction instruction) {
        for (int i = 0; i < branchRS.size(); i++) {
            if (!branchRS.get(i).isBusy()) {
                branchRS.get(i).setValues(true, 1, instruction); // Assuming 1 cycle for branch evaluation
                return branchRS.get(i).getTag();
            }
        }
        return "";
    }

    //TODO HANDLE EXECUTE FOR ALL INSTRUCTION TYPES
    public static HashSet<String> executeAllExcept(String tag) {
        HashSet<String> justFinished = new HashSet<>();

        for (ArithmeticRSEntry currentRS : addSubRS) {
            if (currentRS.getTag().equals(tag))
                continue;

            if (currentRS.getVj() != null && currentRS.getVk() != null && (currentRS.instruction.getStatus() == Status.EXECUTING || currentRS.instruction.getStatus() == Status.ISSUED)) {
                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    currentRS.execute();
                    //where to store the result
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (ArithmeticRSEntry currentRS : mulDivRS) {
            if (currentRS.getTag().equals(tag))
                continue;

            if (currentRS.getVj() != null && currentRS.getVk() != null &&
                    (currentRS.instruction.getStatus() == Status.EXECUTING ||
                            currentRS.instruction.getStatus() == Status.ISSUED)) {
                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    //where to store the result
                    currentRS.execute();
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (StoreRSEntry currentRS : storeRS) {
            if (currentRS.getTag().equals(tag))
                continue;

            if (currentRS.getValue() != null &&
                    (currentRS.instruction.getStatus() == Status.EXECUTING || currentRS.instruction.getStatus() == Status.ISSUED)) {
                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    currentRS.execute();
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (LoadRSEntry currentRS : loadRS) {
            if (currentRS.getTag().equals(tag)) continue;

            currentRS.instruction.setStatus(Status.EXECUTING);
            currentRS.remainingCycles--;

            if (currentRS.remainingCycles == 0) {
                currentRS.instruction.setStatus(Status.EXECUTED);
                currentRS.execute();
                justFinished.add(currentRS.getTag());
            }
        }

        for (BranchRSEntry currentRS : branchRS) {
            if (currentRS.getTag().equals(tag)) continue;

            if (currentRS.getVj() != null && currentRS.getVk() != null
                    && (currentRS.instruction.getStatus() == Status.EXECUTING
                    || currentRS.instruction.getStatus() == Status.ISSUED)) {

                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    currentRS.execute();
                    justFinished.add(currentRS.getTag());
                }
            }
        }
        return justFinished;
    }

    //TODO HANDLE WRITE BACK FOR ALL INSTRUCTION TYPES
    public static void writeToBusExcept(HashSet<String> tags) {
        // Populate the write-back queue
        populateWritebackQueue(tags);

        // Write back a single entry from the queue
        if (!writeBackQueue.isEmpty()) {
            RSBaseEntry rs = writeBackQueue.poll();
            String tag = rs.getTag();
            double value = rs.result;

            // Update other reservation stations

            for (ArithmeticRSEntry rs2 : addSubRS) {
                if (rs2.getQj().equals(tag)) {
                    rs2.setVj(value);
                    rs2.setQj(null);
                }
                if (rs2.getQk().equals(tag)) {
                    rs2.setVk(value);
                    rs2.setQk(null);

                }
            }

            //Updating the register files
            String destination= rs.instruction.getDest();
            RegisterEntry adjustedEntry = registerFile.get(destination);
            adjustedEntry.setValue(value);
            adjustedEntry.setQ("0");
            registerFile.put(destination, adjustedEntry);
            for(Map.Entry<String, RegisterEntry> entry : registerFile.entrySet()){
                RegisterEntry registerEntry = entry.getValue();
                if(registerEntry.getQ().equals(tag)){
                    registerEntry.setQ("0");
                    registerEntry.setValue(value);
                }
            }

            // Update status
            rs.instruction.setStatus(Status.WRITTEN_BACK);
            rs.setBusy(false);
        }
    }

    private static void populateWritebackQueue(HashSet<String> tags) {
        for (ArithmeticRSEntry rs : addSubRS) {
            if (!tags.contains(rs.getTag()) && rs.instruction != null && rs.instruction.getStatus().equals(Status.EXECUTED)) {
                writeBackQueue.add(rs);
            }
        }

        for (ArithmeticRSEntry rs : mulDivRS) {
            if (!tags.contains(rs.getTag()) && rs.instruction != null && rs.instruction.getStatus().equals(Status.EXECUTED)) {
                writeBackQueue.add(rs);
            }
        }

        for (StoreRSEntry rs : storeRS) {
            if (!tags.contains(rs.getTag()) && rs.instruction != null && rs.instruction.getStatus().equals(Status.EXECUTED)) {
                writeBackQueue.add(rs);
            }
        }

        for (LoadRSEntry rs : loadRS) {
            if (!tags.contains(rs.getTag()) && rs.instruction != null && rs.instruction.getStatus().equals(Status.EXECUTED)) {
                writeBackQueue.add(rs);
            }
        }
    }

    public static void initReservationStations(){
        addSubRS=new ArrayList<>();
        for(int i =0; i < addReservationStationSize; i++){
            addSubRS.add(new ArithmeticRSEntry("A" + i, null));
        }
        mulDivRS=new ArrayList<>();
        for(int i =0; i < mulReservationStationSize; i++){
            mulDivRS.add(new ArithmeticRSEntry("M" + i, null));
        }
        storeRS =new ArrayList<>();
        for(int i =0; i < storeReservationStationSize; i++){
            storeRS.add(new StoreRSEntry("S" + i, null));
        }
        loadRS=new ArrayList<>();
        for(int i =0; i < loadReservationStationSize; i++){
            loadRS.add(new LoadRSEntry("L" + i, null));
        }
        branchRS=new ArrayList<>();
        for(int i =0; i < branchReservationStationSize; i++){
            branchRS.add(new BranchRSEntry("B" + i, null));
        }
    }

    public static void printRegisters(HashMap<String, RegisterEntry> registerFile) {
        System.out.println("Register Contents:");
        for (String registerName : registerFile.keySet()) {
            RegisterEntry entry = registerFile.get(registerName);
            System.out.println(registerName + ": " + entry);
        }
    }

    public static void initRegisterFile(){
        int i =0;
        for(Map.Entry<String, RegisterEntry> entry : registerFile.entrySet()){
            entry.getValue().setValue(i);
            i++;
        }
        printRegisters(registerFile);
    }

    public static void init(){

        String filePath = "src/main/java/Core/program.txt";
        instructionQueue = InstructionFileParser.fillInstructionsQueue(filePath);

        Memory memory=new Memory(2024, blockSize);
        Cache cache=new Cache(cacheSize,blockSize,memory);
        initReservationStations();
        initRegisterFile();
    }

    public static void main(String[] args) {

        String filePath = "src/main/java/Core/program.txt";
        List<Instruction> instructionQueue = InstructionFileParser.fillInstructionsQueue(filePath);
        int cycle = 0;
        int pc = 0;

        // TODO: INITIALISE ALL INPUTS FUNCTION FOR INITIALISING ALL GLOBAL VARIABLES
        init();
        initReservationStations();
        initRegisterFile();

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
            System.out.println("Cycle " + cycle);
            Scanner sc = new Scanner(System.in);
            sc.nextInt();
            if (pc < instructionQueue.size()) {
                Instruction currentInstruction = instructionQueue.get(pc);
                Instruction clonedInstruction = currentInstruction.deepClone();
                switch (clonedInstruction.getOp()) {
                    case DADDI:
                    case DSUBI:
                    case ADD_D:
                    case ADD_S:
                    case SUB_D:
                    case SUB_S:
                        if (checkAnEmptyStation(addSubRS)) {
                            tag = addToAddSubRS(clonedInstruction);
                            clonedInstruction.setStatus(Status.ISSUED);
                            pc++;
                        }
                        break;
                    case MUL_D:
                    case MUL_S:
                    case DIV_D:
                    case DIV_S:
                        if (checkAnEmptyStation(mulDivRS)) {
                            tag = addToMulDivRS(clonedInstruction);
                            clonedInstruction.setStatus(Status.ISSUED);
                            pc++;
                        }
                        break;
                    case LW:
                    case LD:
                    case L_S:
                    case L_D:
                        if (checkAnEmptyStation(loadRS)) {
                            tag = addToLoadRS(clonedInstruction);
                            clonedInstruction.setStatus(Status.ISSUED);
                            pc++;
                        }
                        break;
                    case SW:
                    case SD:
                    case S_S:
                    case S_D:
                        if (checkAnEmptyStation(storeRS)) {
                            tag = addToStoreRS(clonedInstruction);
                            clonedInstruction.setStatus(Status.ISSUED);
                            pc++;
                        }
                        break;
                    case BNE:
                    case BEQ:
                        if (checkAnEmptyStation(branchRS)) {
                            tag = addToBranchRS(clonedInstruction);
                            clonedInstruction.setStatus(Status.ISSUED);
                            pc++;
                        }
                        break;
                    default:
                        break;
                }
                //TODO: HANDLE BRANCH INSTRUCTIONS


            }


//            HashSet<String> justExecuted = executeAllExcept(tag);
//            writeToBusExcept(justExecuted);
            System.out.println("Cycle " + cycle);

            for (ArithmeticRSEntry addR : addSubRS)
                addR.printRSDetails();
            for(LoadRSEntry load: loadRS)
                load.printRSDetails();
            for(StoreRSEntry store : storeRS)
                store.printRSDetails();

        }


        printRegisters(registerFile);
    }
}