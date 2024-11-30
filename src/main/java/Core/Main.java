package Core;
import Core.Register.*;
import Core.Storage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        //this is how a station is created
        RegisterFile registerFileInstance = new RegisterFile();
        List<ReservationStationEntry> adders = new ArrayList<>(5);
        System.out.println("Testing StoreBufferEntry:");
        StoreBufferEntry storeEntry = new StoreBufferEntry("S1");
        storeEntry.setEffectiveAddress(100);
        storeEntry.setValueToStore(12);

        System.out.println("Name: " + storeEntry.getName());
        System.out.println("Effective Address: " + storeEntry.getEffectiveAddress());
        System.out.println("Value to Store: " + storeEntry.getValueToStore());

        storeEntry.clear();
        System.out.println("After clearing:");
        System.out.println("Effective Address: " + storeEntry.getEffectiveAddress());
        System.out.println("Value to Store: " + storeEntry.getValueToStore());

        System.out.println("\nTesting LoadBufferEntry:");

        LoadBufferEntry loadEntry = new LoadBufferEntry("L1");
        loadEntry.setEffectiveAddress(200);

        System.out.println("Name: " + loadEntry.getName());
        System.out.println("Effective Address: " + loadEntry.getEffectiveAddress());

        loadEntry.clear();
        System.out.println("After clearing:");
        System.out.println("Effective Address: " + loadEntry.getEffectiveAddress());

        System.out.println("\nTesting ReservationStationEntry:");

        ReservationStationEntry resEntry = new ReservationStationEntry("A1");
        adders.add(resEntry);
        resEntry.setVj(-1.7);
        resEntry.setVk(6.3);
        resEntry.setQj("0");
        resEntry.setQk("L1");
        System.out.println("Adders first entry:  " + adders.get(0).getName());
        System.out.println("Adders size: " + adders.size());
        System.out.println("Name: " + resEntry.getName());
        System.out.println("Vj: " + resEntry.getVj());
        System.out.println("Vk: " + resEntry.getVk());
        System.out.println("Qj: " + resEntry.getQj());
        System.out.println("Qk: " + resEntry.getQk());

        resEntry.clear();
        System.out.println("After clearing:");
        System.out.println("Vj: " + resEntry.getVj());
        System.out.println("Vk: " + resEntry.getVk());
        System.out.println("Qj: " + resEntry.getQj());
        System.out.println("Qk: " + resEntry.getQk());
    }
}
