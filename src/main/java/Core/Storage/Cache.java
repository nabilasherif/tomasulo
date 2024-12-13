package Core.Storage;

import java.nio.ByteBuffer;

public class Cache {

    public byte[] cache;
    private boolean[] initialized;
    private final int blockSize;
    private final Memory memory;

    public Cache(int size, int blockSize, Memory memory) {
        this.cache = new byte[size];
        this.initialized = new boolean[size];
        this.blockSize = blockSize;
        this.memory = memory;
    }

    private void loadBlock(int address) {
        int blockStartAddress = address - (address % blockSize);
        byte[] block = memory.readBlock(blockStartAddress);
        System.arraycopy(block, 0, cache, blockStartAddress, blockSize);

        for (int i = blockStartAddress; i < blockStartAddress + blockSize; i++) {
            if (i < initialized.length) {
                initialized[i] = true;
            }
        }
    }

    public boolean cacheLoadedBlockCheck(int address) {
        int blockStartAddress = address - (address % blockSize);
        return initialized[blockStartAddress];
    }

    private void writeBack(int address) {
        int blockStartAddress = address - (address % blockSize);
        byte[] block = new byte[blockSize];
        System.arraycopy(cache, blockStartAddress, block, 0, blockSize);
        memory.writeBlock(blockStartAddress, block);
    }

    public byte readByte(int address) {
        if (!initialized[address]) {
            loadBlock(address);
        }
        return cache[address];
    }

    public void writeByte(int address, byte value) {
        cache[address] = value;
        initialized[address] = true;
        writeBack(address);
    }

    public float readWord(int address) {
        byte[] word = new byte[4];
        System.arraycopy(cache, address, word, 0, 4);
        return ByteBuffer.wrap(word).getFloat();
    }

    public void writeWord(int address, float value) {
        byte[] bytes = ByteBuffer.allocate(4).putFloat(value).array();
        System.arraycopy(bytes, 0, cache, address, 4);
        writeBack(address);
    }

    public double readDoubleWord(int address) {
        byte[] dword = new byte[8];
        System.arraycopy(cache, address, dword, 0, 8);
        return ByteBuffer.wrap(dword).getDouble();
    }

    public void writeDoubleWord(int address, double value) {
        byte[] bytes = ByteBuffer.allocate(8).putDouble(value).array();
        System.arraycopy(bytes, 0, cache, address, 8);
        writeBack(address);
    }

    public static void main(String[] args) {
        Memory memory = new Memory(16, 8);
        Cache cache = new Cache(24, 8, memory);
        byte[] block = new byte[8];
        for (int i = 0; i < 8; i++) {
            block[i] = (byte) (i + 1);
        }
        memory.writeBlock(0, block);

        for (int i = 0; i < 8; i++) {
            System.out.print(cache.readByte(i) + " ");
        }
        System.out.println();

        cache.writeByte(4, (byte) 100);
        memory.displayMemory();
        System.out.println(cache.readByte(4));
        cache.writeByte(12, (byte) 123);
        System.out.println(cache.readByte(12));
        System.out.println(cache.readByte(12));
    }
}
