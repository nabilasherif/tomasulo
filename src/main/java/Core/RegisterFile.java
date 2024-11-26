package Core;
import java.util.Map;

public class RegisterFile {
    private Map<String, RegisterEntry> registers;

    public void updateRegister(String registerName, Object value) {
        RegisterEntry register = registers.get(registerName);
        if (value instanceof Integer) {
            register.setValue((Integer) value);
            register.setBusy(false);
            register.setWaitingStation("");
        } else if (value instanceof String) {
            register.setWaitingStation((String) value);
            register.setBusy(true);
        }
    }
}