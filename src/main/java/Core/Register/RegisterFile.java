package Core.Register;

import java.util.*;

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
        registers = sortRegisters(registers);
    }

    private LinkedHashMap<String, RegisterEntry> sortRegisters(HashMap<String, RegisterEntry> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        keys.sort((key1, key2) -> {
            char prefix1 = key1.charAt(0);
            char prefix2 = key2.charAt(0);
            int number1 = Integer.parseInt(key1.substring(1));
            int number2 = Integer.parseInt(key2.substring(1));

            if (prefix1 != prefix2) {
                return Character.compare(prefix1, prefix2);
            }
            return Integer.compare(number1, number2);
        });
        LinkedHashMap<String, RegisterEntry> sortedMap = new LinkedHashMap<>();
        for (String key : keys) {
            sortedMap.put(key, map.get(key));
        }
        return sortedMap;
    }

    public HashMap<String, RegisterEntry> getRegisters() {
        return registers;
    }
}