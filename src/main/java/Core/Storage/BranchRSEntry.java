package Core.Storage;

import Core.Instruction.InstructionType;
import Core.Instruction.Instruction;

public class BranchRSEntry extends RSBaseEntry {

    public BranchRSEntry(String tag, Instruction instruction) {
        super(tag, instruction);
    }

    public boolean evaluateBranch() {
        //  Evaluate the branch instruction
        return false;
    }
}
