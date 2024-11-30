package Core.Instruction;

public enum InstructionType {
        DADDI, DSUBI,
        ADD_D, ADD_S,
        SUB_D, SUB_S,
        MUL_D, MUL_S,
        DIV_D, DIV_S,
        LW, LD,
        L_S, L_D,
        SW, SD,
        S_S, S_D,
        BNE, BEQ;

        public static InstructionType fromString(String opString) {
                try {
                        return InstructionType.valueOf(opString.replace('.', '_'));
                } catch (IllegalArgumentException e) {
                        return null;
                }
        }

        public String toFormattedString() {
                return name().replace('_', '.');
        }

        public static void main(String[] args) {
                System.out.println(InstructionType.fromString("ADD.D")); // Output: ADD_D
                System.out.println(InstructionType.fromString("ADD_S")); // Output: ADD_S (No change)
                System.out.println(InstructionType.fromString("NON_EXISTENT")); // Output: null (or error handling)

                System.out.println(InstructionType.ADD_D.toFormattedString()); // Output: ADD.D
                System.out.println(InstructionType.MUL_S.toFormattedString()); // Output: MUL.S
        }
}