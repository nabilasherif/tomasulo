package Core.Storage;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Cache {

    private Map<Integer, byte[]> cache;
    private final int blockSize;
    private final Memory memory;

    public Cache(int size, int blockSize,Memory memory) {
        this.cache = new HashMap<>(size);
        this.blockSize = blockSize;
        this.memory = memory;
    }

    // load the block from cache or memory (cache miss will load from memory)
    private byte[] loadBlock(int address) {
        int blockStartAddress = address - (address % blockSize);
        byte[] block = cache.get(blockStartAddress);

        if (block == null) {
            block = memory.readBlock(blockStartAddress);
            cache.put(blockStartAddress, block);
        }

        return block;
    }

    // write the modified block back to memory
    private void writeBack(int address, byte[] block) {
        int blockStartAddress = address - (address % blockSize);

        if (cache.containsKey(blockStartAddress)) {
            cache.put(blockStartAddress, block);
        }

        memory.writeBlock(blockStartAddress, block);
    }

    // read a byte from the cache or memory
    public byte readByte(int address) {
        byte[] block = loadBlock(address);
        int offset = address % blockSize;
        return block[offset];
    }

    // write a byte to the cache and memory
    public void writeByte(int address, byte value) {
        byte[] block = loadBlock(address);
        int offset = address % blockSize;
        block[offset] = value;
        writeBack(address, block);
    }

    // read a word from the cache or memory
    public float readWord(int address) {
        byte[] block = loadBlock(address);
        int offset = address % blockSize;
        return ByteBuffer.wrap(block, offset, 4).getFloat();
    }

    // write a word to the cache and memory
    public void writeWord(int address, float value) {
        byte[] block = loadBlock(address);
        int offset = address % blockSize;
        byte[] bytes = ByteBuffer.allocate(4).putFloat(value).array();
        System.arraycopy(bytes, 0, block, offset, 4);
        writeBack(address, block);
    }

    // read a double word from the cache or memory
    public double readDoubleWord(int address) {
        byte[] block = loadBlock(address);
        int offset = address % blockSize;
        return ByteBuffer.wrap(block, offset, 8).getDouble();
    }

    // write a double word to the cache and memory
    public void writeDoubleWord(int address, double value) {
        byte[] block = loadBlock(address);
        int offset = address % blockSize;
        byte[] bytes = ByteBuffer.allocate(8).putDouble(value).array();
        System.arraycopy(bytes, 0, block, offset, 8);
        writeBack(address, block);
    }

    public static void main(String[] args) {
        Memory memory = new Memory(16, 8);
        Cache cache = new Cache(24,8,memory);
        byte[] block = new byte[8];
        for (int i = 0; i < 8; i++) {
            block[i] = (byte) (i + 1);
        }
        memory.writeBlock(0, block);

        System.out.println("First read (miss) at address 0:");
        for (int i = 0; i < 8; i++) {
            System.out.print(cache.readByte(i) + " ");
        }
        System.out.println();
        cache.writeByte(4, (byte) 100);
        //after updating byte 4
        memory.displayMemory();
        System.out.println("Byte at address 4 after writing to cache: " + cache.readByte(4));
        cache.writeByte(12, (byte) 123);
        System.out.println("Byte at address 12 (direct memory write): " + cache.readByte(12));
        System.out.println("Reading byte at address 12 (cache miss): " + cache.readByte(12));
    }
}