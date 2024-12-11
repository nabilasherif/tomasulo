package Core;

import Core.Instruction.InstructionQueueInstance;
import Core.Instruction.InstructionType;

import java.io.*;
import java.util.*;

public class InstructionFileParser {

    public static List<InstructionQueueInstance> parseInstructionsFromFile(String filePath) {
        List<InstructionQueueInstance> instructions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                InstructionQueueInstance instruction = parseInstructionLine(line);
                if (instruction != null) {
                    instructions.add(instruction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return instructions;
    }

    static InstructionQueueInstance parseInstructionLine(String line) {
        // Trim the line to remove leading/trailing whitespace
        line = line.trim();

        // Split the instruction by space (e.g., "L.D F4,F2,100" becomes ["L.D", "F4,F2,100"])
        String[] parts = line.split(" ");

        String opString = parts[0];
        InstructionType op = InstructionType.fromString(opString);

        if (op == null) {
            return null;
        }


        String[] operands = parts[1].split(",");
        String dest = operands[0]; // Destination register (e.g., F4)
        String j = operands.length > 1 ? operands[1] : ""; // First operand (e.g., F2)
        String k = operands.length > 2 ? operands[2] : "";

        return new InstructionQueueInstance(op, dest, j, k);
    }

    //this fill the instruction queue with the instructions from the file but all has 0 for issue and write cycle
    public static List<InstructionQueueInstance> fillInstructionsQueue(String filePath) {
        List<InstructionQueueInstance> instructionQueue = new ArrayList<InstructionQueueInstance>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                InstructionQueueInstance instruction = parseInstructionLine(line);
                if (instruction != null) {
                    instructionQueue.add(instruction);

//                    InstructionQueueInstance queueInstance = new InstructionQueueInstance(instruction.getOp(),instruction.getDest(), instruction.getJ(), instruction.getK());
//                    instructionQueue.add(queueInstance);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instructionQueue;
    }

    public static void main(String[] args) {
        String filePath = "src/main/java/Core/test1.txt";
        List<InstructionQueueInstance> instructions = parseInstructionsFromFile(filePath);

        for (InstructionQueueInstance instruction : instructions) {
            System.out.println("Op: " + instruction.getOp() + ", Dest: " + instruction.getDest()
                    + ", J: " + instruction.getJ() + ", K: " + instruction.getK());
        }
    }
}