package Core.Register;

import java.util.HashMap;

public class RegisterFile {
    private HashMap<String, RegisterEntry> registers;

    public RegisterFile() {
        registers = new HashMap<>();
        for (int i = 0; i < 32; i++) {

            registers.put("R" + i, new RegisterEntry());
        }
        for (int i = 0; i < 32; i++) {
            registers.put("F" + i, new RegisterEntry());
        }
    }

    public void updateRegister(String registerName, Object value) {
        RegisterEntry register = registers.get(registerName);
        if (value instanceof Integer) {
            register.setValue((Integer) value);
        }
        else if(value instanceof Float){
            register.setValue((Float) value);
        }
        else if(value instanceof Double){
            register.setValue((Double) value);
        }
        else if (value instanceof String) {
            register.setQ((String) value);
            return;
        }
        register.setQ("0");
    }

    public HashMap<String, RegisterEntry> getRegisters() {
        return registers;
    }
}