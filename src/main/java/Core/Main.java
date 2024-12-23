package Core;
import Core.Instruction.*;
import Core.Register.*;
import Core.Storage.*;
import java.util.*;

public class Main {

    private static final String filePath = "src/main/java/Core/program4.txt";
    public static boolean isProgramDone = false;
    static boolean stall=false;
    public static int blockSize = 8;
    public static int cacheSize= 64;
    public static Memory memory=new Memory(2024, blockSize);
    public static Cache cache=new Cache(cacheSize,blockSize,memory);
    public static ArrayList<ArithmeticRSEntry> addSubRS = new ArrayList<>();
    public static ArrayList<ArithmeticRSEntry> mulDivRS = new ArrayList<>();
    public static ArrayList<LoadRSEntry> loadRS = new ArrayList<>();
    public static ArrayList<StoreRSEntry> storeRS = new ArrayList<>();
    public static HashMap<String, RegisterEntry> registerFile = new RegisterFile().getRegisters();
    public static int addReservationStationSize= 3;
    public static int addLatency = 3;
    public static int addFPLatency = 3;
    public static int subLatency=3;
    public static int subFPLatency=3;
    public static int mulReservationStationSize= 3;
    public static int mulFPLatency = 3;
    public static int divFPLatency = 3;
    public static int loadReservationStationSize= 3;
    public static int loadLatency = 3;
    public static int loadPenalty = 3;
    public static int storeReservationStationSize= 3;
    public static int storeLatency = 3;
    //do we add a store penalty?
    public static ArrayList<ArithmeticRSEntry> branchRS = new ArrayList<>();
    public static int branchReservationStationSize= 3;
    public static int branchLatency = 1;
    public static  Queue<RSBaseEntry> writeBackQueue = new LinkedList<>();
    public static List<Instruction> instructionQueueParser = new ArrayList<>();
    public static List<Instruction> instructionQueue = new ArrayList<>();
    public static int cycle = 0;
    public static int pc = 0;
    public static boolean firstLoad = true;

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
                if(instruction.getOp() == InstructionType.ADD_D || instruction.getOp() == InstructionType.ADD_S  )
                    addSubRS.get(i).setValues(true, addFPLatency, instruction);
                if(instruction.getOp() == InstructionType.DADDI)
                    addSubRS.get(i).setValues(true, addLatency, instruction);
                if(instruction.getOp() == InstructionType.SUB_S || instruction.getOp() == InstructionType.SUB_D)
                    addSubRS.get(i).setValues(true, subFPLatency, instruction);
                if(instruction.getOp() == InstructionType.DSUBI)
                    addSubRS.get(i).setValues(true, subLatency, instruction);
                String j = addSubRS.get(i).instruction.getJ();
                String k = addSubRS.get(i).instruction.getK();

                double jvalue = registerFile.get(j).getValue();

                if(instruction.getOp() != InstructionType.DSUBI && instruction.getOp() != InstructionType.DADDI){
                    double kvalue = registerFile.get(k).getValue();
                    String kQ = registerFile.get(k).getQ();
                    if(kQ.equals("0")){
                        addSubRS.get(i).setVk(kvalue);
                    }else{
                        addSubRS.get(i).setQk(kQ);
                    }
                }else{
                    addSubRS.get(i).setVk(Double.parseDouble(instruction.getK()));
                }

                String jQ = registerFile.get(j).getQ();

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
                    mulDivRS.get(i).setValues(true, mulFPLatency, instruction);
                else
                    mulDivRS.get(i).setValues(true, divFPLatency, instruction);

                addSubRS.get(i).setOp(instruction.getOp());

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
//                if (cache.cacheLoadedBlockCheck(Integer.parseInt(instruction.getJ())))
//                    loadRS.get(i).setValues(true, loadLatency, instruction);
//                else
//                    loadRS.get(i).setValues(true, loadLatency + loadPenalty, instruction);
                if(firstLoad){
                    loadRS.get(i).setValues(true, loadLatency + loadPenalty, instruction);
                    firstLoad=false;
                }else{
                    loadRS.get(i).setValues(true, loadLatency, instruction);
                }

                String j = loadRS.get(i).instruction.getJ();//100
                if(registerFile.containsKey(j)){
                    RegisterEntry entry = registerFile.get(j);
                    if(entry.getQ().equals("0")){
                        loadRS.get(i).setAddress((int) entry.getValue());
                    }else{

                    }
                }else{
                    loadRS.get(i).setAddress(Integer.parseInt(j));
                }
                loadRS.get(i).setAddress(Integer.parseInt(j));
                return loadRS.get(i).getTag();
            }
        }
        return "";
    }

    private static String addToBranchRS(Instruction instruction) {
        for (int i = 0; i < branchRS.size(); i++) {
            // If my reservation station's current entry is not busy, add the instruction to the reservation station
            if (!branchRS.get(i).isBusy()) {
                branchRS.get(i).setValues(true, branchLatency, instruction);
                String j = branchRS.get(i).instruction.getJ();
                String k = branchRS.get(i).instruction.getDest();

                double jvalue = registerFile.get(j).getValue();
                double kvalue = registerFile.get(k).getValue();
                String kQ = registerFile.get(k).getQ();
                String jQ = registerFile.get(j).getQ();

                if(kQ.equals("0")){
                    branchRS.get(i).setVk(kvalue);
                }else{
                    branchRS.get(i).setQk(kQ);
                }

                if(jQ.equals("0")){
                    branchRS.get(i).setVj(jvalue);
                }else{
                    branchRS.get(i).setQj(jQ);
                }
                return branchRS.get(i).getTag();
            }
        }
        return "";
    }

    public static HashSet<String> executeAllExcept(String tag) {
        HashSet<String> justFinished = new HashSet<>();

        for (ArithmeticRSEntry currentRS : addSubRS) {
            if ( currentRS.getTag().equals(tag) || currentRS.instruction == null)
                continue;

            if (currentRS.getQk().equals("0") && currentRS.getQj().equals("0") && (currentRS.instruction.getStatus() == Status.EXECUTING || currentRS.instruction.getStatus() == Status.ISSUED)) {
                currentRS.instruction.setStatus(Status.EXECUTING);
                List<Integer> currExecution = currentRS.instruction.getExecution();
                currExecution.add(cycle);
                currentRS.instruction.setExecution(currExecution);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    currentRS.setResult(currentRS.execute());
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (ArithmeticRSEntry currentRS : mulDivRS) {
            if ( currentRS.getTag().equals(tag) || !currentRS.isBusy())
                continue;

            if (currentRS.getQk().equals("0") && currentRS.getQj().equals("0")&&
                    (currentRS.instruction.getStatus() == Status.EXECUTING ||
                            currentRS.instruction.getStatus() == Status.ISSUED)) {
                List<Integer> currExecution = currentRS.instruction.getExecution();
                currExecution.add(cycle);
                currentRS.instruction.setExecution(currExecution);
                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    currentRS.setResult(currentRS.execute());
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (StoreRSEntry currentRS : storeRS) {
            if ( currentRS.getTag().equals(tag) || !currentRS.isBusy())
                continue;

            if (currentRS.getValue() != null &&
                    (currentRS.instruction.getStatus() == Status.EXECUTING || currentRS.instruction.getStatus() == Status.ISSUED)) {
                List<Integer> currExecution = currentRS.instruction.getExecution();
                currExecution.add(cycle);
                currentRS.instruction.setExecution(currExecution);
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
            if ( currentRS.getTag().equals(tag) || !currentRS.isBusy())
                continue;
            if (currentRS.instruction != null &&
                    (currentRS.instruction.getStatus() == Status.EXECUTING ||
                            currentRS.instruction.getStatus() == Status.ISSUED)) {
                List<Integer> currExecution = currentRS.instruction.getExecution();
                currExecution.add(cycle);
                currentRS.instruction.setExecution(currExecution);
                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;
                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    currentRS.setResult(currentRS.execute());
                    justFinished.add(currentRS.getTag());
                }
            }
        }

        for (ArithmeticRSEntry currentRS : branchRS) {
            if ( currentRS.getTag().equals(tag) || !currentRS.isBusy()) continue;

            if (currentRS.getQk().equals("0") && currentRS.getQj().equals("0")
                    && (currentRS.instruction.getStatus() == Status.EXECUTING
                    || currentRS.instruction.getStatus() == Status.ISSUED)) {
                List<Integer> currExecution = currentRS.instruction.getExecution();
                currExecution.add(cycle);
                currentRS.instruction.setExecution(currExecution);

                currentRS.instruction.setStatus(Status.EXECUTING);
                currentRS.remainingCycles--;

                if (currentRS.remainingCycles == 0) {
                    currentRS.instruction.setStatus(Status.EXECUTED);
                    double branchRes= currentRS.execute(); //not sure what to do
                    currentRS.setBusy(false);
                    if(branchRes == 1){
                        handleBranchTrue(currentRS.instruction);
                    }else{
                        pc++;
                    }
                    stall = false;

//                    justFinished.add(currentRS.getTag());
                }
            }
        }
        return justFinished;
    }

    public static void writeToBusExcept(HashSet<String> tags) {
        // Populate the write-back queue
        populateWritebackQueue(tags);

        // Write back a single entry from the queue
        if (!writeBackQueue.isEmpty()) {
            RSBaseEntry rs = writeBackQueue.poll();
            String tag = rs.getTag();
            double value = rs.getResult();

            // Update other reservation stations

            for (ArithmeticRSEntry rs2 : addSubRS) {
                if (rs2.getQj().equals(tag)) {
                    rs2.setVj(value);
                    rs2.setQj("0");
                }
                if (rs2.getQk().equals(tag)) {
                    rs2.setVk(value);
                    rs2.setQk("0");

                }
            }
            for (ArithmeticRSEntry rs2 : mulDivRS) {
                if ( rs2.getQj().equals(tag)) {
                    rs2.setVj(value);
                    rs2.setQj("0");
                }
                if (  rs2.getQk().equals(tag)) {
                    rs2.setVk(value);
                    rs2.setQk("0");
                }
            }
            for (StoreRSEntry rs2 : storeRS) {
                if ( rs2.getQ().equals(tag)) {
                    rs2.setValue(value);
                    rs2.setQ("0");
                }
            }

            for (ArithmeticRSEntry rs2 : branchRS) {
                if (rs2.getQj().equals(tag)) {
                    rs2.setVj(value);
                    rs2.setQj("0");
                }
                if (rs2.getQk().equals(tag)) {
                    rs2.setVk(value);
                    rs2.setQk("0");

                }
            }
            //Updating the register files
            if(rs.instruction.getOp() != InstructionType.BNE && rs.instruction.getOp() != InstructionType.BEQ  ){
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
            }

            // Update status
            rs.instruction.setStatus(Status.WRITTEN_BACK);
            if(rs.isBusy())
                rs.instruction.setWrite(cycle);
            rs.setBusy(false);
        }
    }

    private static void populateWritebackQueue(HashSet<String> tags) {
        List<RSBaseEntry> readyEntries = new ArrayList<>();

        for (LoadRSEntry rs : loadRS) {
            if (!tags.contains(rs.getTag()) && !isAlreadyInQueue(rs)
                    && rs.isBusy() && rs.instruction.getStatus().equals(Status.EXECUTED)) {
                readyEntries.add(rs);
            }
        }

        for (ArithmeticRSEntry rs : addSubRS) {
            if (!tags.contains(rs.getTag()) && !isAlreadyInQueue(rs)
                    && rs.isBusy() && rs.instruction.getStatus().equals(Status.EXECUTED)) {
                readyEntries.add(rs);
            }
        }

        for (ArithmeticRSEntry rs : mulDivRS) {
            if (!tags.contains(rs.getTag()) && !isAlreadyInQueue(rs)
                    && rs.isBusy() && rs.instruction.getStatus().equals(Status.EXECUTED)) {
                readyEntries.add(rs);
            }
        }
        readyEntries.sort(Comparator.comparingInt(rs -> instructionQueue.indexOf(rs.instruction)));
        writeBackQueue.addAll(readyEntries);
    }

    private static boolean isAlreadyInQueue(RSBaseEntry rs) {
        for (RSBaseEntry entry : writeBackQueue) {
            if (entry.getTag().equals(rs.getTag())) {
                return true;
            }
        }
        return false;
    }

    public static void initReservationStations(){
        addSubRS=new ArrayList<>();
        for(int i =1; i <= addReservationStationSize; i++){
            addSubRS.add(new ArithmeticRSEntry("A" + i, null));
        }
        mulDivRS=new ArrayList<>();
        for(int i =1; i <= mulReservationStationSize; i++){
            mulDivRS.add(new ArithmeticRSEntry("M" + i, null));
        }
        storeRS =new ArrayList<>();
        for(int i =1; i <= storeReservationStationSize; i++){
            storeRS.add(new StoreRSEntry("S" + i, null));
        }
        loadRS=new ArrayList<>();
        for(int i =1; i <= loadReservationStationSize; i++){
            loadRS.add(new LoadRSEntry("L" + i, null));
        }
        branchRS=new ArrayList<>();
        for(int i =1; i <= branchReservationStationSize; i++){
            branchRS.add(new ArithmeticRSEntry("B" + i, null));
        }
    }

    private static void printRegisters(HashMap<String, RegisterEntry> registerFile) {
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
        instructionQueueParser = InstructionFileParser.fillInstructionsQueue(filePath);

        for (Instruction instruction : instructionQueueParser) {
            instructionQueue.add(instruction.deepClone());
        }

        memory = new Memory(2024, blockSize);
        byte [] loopover = new byte[blockSize];
        for(int i =0; i < loopover.length; i++)
            loopover[i]= (byte)i;
        memory.writeBlock(0,loopover );
        cache=new Cache(cacheSize,blockSize,memory);
        initReservationStations();
        initRegisterFile();
    }

    public static void handleBranchTrue(Instruction instruction){
        int addressLoop = Integer.parseInt(instruction.getK());
        int addressBranch=instructionQueueParser.size();

        for (int i=addressLoop;i< instructionQueueParser.size();i++){
            instructionQueue.add(instructionQueueParser.get(i).deepClone());
        }
        pc++;

    }

    public static void clearAllWrittenBack(){

        for (ArithmeticRSEntry rs : mulDivRS) {
            if (rs.instruction != null && rs.instruction.getStatus() == Status.WRITTEN_BACK) {
                rs.clear();
            }
        }

        for (ArithmeticRSEntry rs : addSubRS) {
            if (rs.instruction != null && rs.instruction.getStatus() == Status.WRITTEN_BACK) {
                rs.clear();
            }
        }

        for (StoreRSEntry rs : storeRS) {
            if (rs.instruction != null && rs.instruction.getStatus() == Status.EXECUTED) {
                rs.clear();
            }
        }

        for (LoadRSEntry rs : loadRS) {
            if (rs.instruction != null && rs.instruction.getStatus() == Status.WRITTEN_BACK) {
                rs.clear();
            }
        }

        for(ArithmeticRSEntry rs : branchRS){
            if (rs.instruction != null && rs.instruction.getStatus() == Status.EXECUTED) {
                rs.clear();
            }
        }
    }

    public static void incrementCycle(){
        clearAllWrittenBack();
        String tag= "0";
        cycle++;
//        Scanner sc = new Scanner(System.in);
//        sc.nextInt();
        // if previous was a branch so don't issue for 1 cycle until decision is known
        if (pc < instructionQueue.size() && !stall) {
            Instruction clonedInstruction = instructionQueue.get(pc);
            switch (clonedInstruction.getOp()) {
                case DADDI,DSUBI,ADD_D,ADD_S,SUB_D,SUB_S:
                    if (checkAnEmptyStation(addSubRS)) {
                        tag = addToAddSubRS(clonedInstruction);
                        clonedInstruction.setStatus(Status.ISSUED);
                        clonedInstruction.setIssue(cycle);
                        pc++;
                    }
                    break;
                case MUL_D,MUL_S,DIV_D,DIV_S:
                    if (checkAnEmptyStation(mulDivRS)) {
                        tag = addToMulDivRS(clonedInstruction);
                        clonedInstruction.setStatus(Status.ISSUED);
                        clonedInstruction.setIssue(cycle);
                        pc++;
                    }
                    break;
                case LW,LD,L_S,L_D:
                    if (checkAnEmptyStation(loadRS)) {
                        tag = addToLoadRS(clonedInstruction);
                        clonedInstruction.setStatus(Status.ISSUED);
                        clonedInstruction.setIssue(cycle);
                        pc++;
                    }
                    break;
                case SW,SD,S_S,S_D:
                    if (checkAnEmptyStation(storeRS)) {
                        tag = addToStoreRS(clonedInstruction);
                        clonedInstruction.setStatus(Status.ISSUED);
                        clonedInstruction.setIssue(cycle);
                        pc++;
                    }
                    break;
                case BNE,BEQ:
                    if (checkAnEmptyStation(branchRS)) {
                        tag = addToBranchRS(clonedInstruction);
                        clonedInstruction.setStatus(Status.ISSUED);
                        clonedInstruction.setIssue(cycle);
                        stall = true;
                    }
                    break;
                default:
                    break;
            }

            // go through the register files and set the Q to the tag
            if(clonedInstruction.getOp() != InstructionType.SW && clonedInstruction.getOp() != InstructionType.SD &&
                    clonedInstruction.getOp()!= InstructionType.S_D && clonedInstruction.getOp()!= InstructionType.S_S &&
            clonedInstruction.getOp()!= InstructionType.BNE && clonedInstruction.getOp()!= InstructionType.BEQ){
                String destination = clonedInstruction.getDest();
                RegisterEntry registerEntry = registerFile.get(destination);
                registerEntry.setQ(tag);
                registerFile.put(destination, registerEntry);

            }
        }
        HashSet<String> justExecuted = executeAllExcept(tag);
        writeToBusExcept(justExecuted);

    }

    public static void main(String[] args) {

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

        while(!isProgramDone){
            isProgramDone = pc >= instructionQueue.size() && allStationsEmpty();
            incrementCycle();
        }
    }

}