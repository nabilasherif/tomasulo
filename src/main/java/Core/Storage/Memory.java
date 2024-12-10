package Core.Storage;

import java.nio.ByteBuffer;

// i still didnt implement the cache i'm still not sure how to implement it
public class Memory {
    public byte[] memory;

    public Memory(int size) {
        memory = new byte[size];
    }

    // Write a single byte to a specific memory address
    public void writeByte(int address, byte value) {
        if (isValidAddress(address)) {
            memory[address] = value;
        } else {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }
    }

    // Read a single byte from a specific memory address
    public byte readByte(int address) {
        if (isValidAddress(address)) {
            return memory[address];
        } else {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }
    }

    // Write a word (4 bytes) to memory
    public void writeWord(int address, float value) {
        if (isValidAddress(address) && isValidAddress(address + 3)) {
            byte[] bytes = ByteBuffer.allocate(4).putFloat(value).array();
            System.arraycopy(bytes, 0, memory, address, 4);
        } else {
            throw new IllegalArgumentException("Invalid memory address for word: " + address);
        }
    }

    // Read a word (4 bytes) from memory
    public float readWord(int address) {
        if (isValidAddress(address) && isValidAddress(address + 3)) {
            byte[] bytes = new byte[4];
            System.arraycopy(memory, address, bytes, 0, 4);
            return ByteBuffer.wrap(bytes).getFloat();
        } else {
            throw new IllegalArgumentException("Invalid memory address for word: " + address);
        }
    }

    // Write a double word (8 bytes) to memory
    public void writeDoubleWord(int address, double value) {
        if (isValidAddress(address) && isValidAddress(address + 7)) {
            byte[] bytes = ByteBuffer.allocate(8).putDouble(value).array();
            System.arraycopy(bytes, 0, memory, address, 8);
        } else {
            throw new IllegalArgumentException("Invalid memory address for double word: " + address);
        }
    }

    // Read a double word (8 bytes) from memory
    public double readDoubleWord(int address) {
        if (isValidAddress(address) && isValidAddress(address + 7)) {
            byte[] bytes = new byte[8];
            System.arraycopy(memory, address, bytes, 0, 8);
            return ByteBuffer.wrap(bytes).getDouble();
        } else {
            throw new IllegalArgumentException("Invalid memory address for double word: " + address);
        }
    }

    // Check if the address is within bounds
    private boolean isValidAddress(int address) {
        return address >= 0 && address < memory.length;
    }

    // Display the entire memory content
    public void displayMemory() {
        for (int i = 0; i < memory.length; i++) {
            System.out.printf("Address %d: %d%n", i, memory[i]);
        }
    }

    // Main method for testing the Memory
    public static void main(String[] args) {
        int memorySize = 64; // Example memory size
        Memory memorySimulator = new Memory(memorySize);

        // Write a byte
        memorySimulator.writeByte(0, (byte) 12);
        System.out.println("Byte at address 0: " + memorySimulator.readByte(0));

        // Write a word (32-bit float)
        memorySimulator.writeWord(4, 123.3f);
        System.out.println("Word at address 4: " + memorySimulator.readWord(4));

        // Write a double word (64-bit double)
        memorySimulator.writeDoubleWord(8, 98765.0);
        System.out.println("Double word at address 8: " + memorySimulator.readDoubleWord(8));

        // Display entire memory
        System.out.println("\nMemory Content:");
        memorySimulator.displayMemory();
    }
}
