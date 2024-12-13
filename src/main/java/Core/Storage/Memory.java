package Core.Storage;

public class Memory {

    public byte[] memory;
    public final int blockSize;

    public Memory(int size, int blockSize) {
        memory = new byte[size];
        this.blockSize = blockSize;
    }

    // returns a whole block
    public byte[] readBlock(int address) {
        if (!isValidAddress(address)) {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }

        int blockStartAddress = address - (address % blockSize);

        byte[] blockData = new byte[blockSize];

        if (blockStartAddress + blockSize > memory.length) {
            throw new IllegalArgumentException("Block exceeds memory bounds");
        }

        System.arraycopy(memory, blockStartAddress, blockData, 0, blockSize);
        return blockData;
    }

    // Write a block of memory based on the address
    public void writeBlock(int address, byte[] data) {
        if (data.length != blockSize) {
            throw new IllegalArgumentException("Data size must be equal to the block size");
        }

        if (!isValidAddress(address)) {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }

        // Calculate the block start address by rounding down to the nearest multiple of blockSize
        int blockStartAddress = address - (address % blockSize);  // Accessing blockSize from Main

        if (blockStartAddress + blockSize > memory.length) {
            throw new IllegalArgumentException("Block exceeds memory bounds");
        }

        // Write the block data to memory
        System.arraycopy(data, 0, memory, blockStartAddress, blockSize);
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
        int memorySize = 16; // Example memory size
        Memory memorySimulator = new Memory(memorySize, 4);
        byte[] data = new byte[4];
        for (int i = 0; i < 4; i++) {
            data[i] = (byte) (i + 1);  // Fill the block with values 1, 2, ..., 8
        }
        memorySimulator.writeBlock(10, data);

        byte[] readData = memorySimulator.readBlock(10);
        System.out.println("Read block starting from address 10:");
        for (byte b : readData) {
            System.out.print(b + " ");  // Output should be 1 2 3 4 5 6 7 8
        }
    }
}