package Core;

import static Core.Main.cache;
import static Core.Main.registerFile;

public class Operations {

    //a will be the content inside the register, b immediate

    public static double ADD_D(double a, double b) {return a + b;}

    public static double ADD_S(double a, double b) {return a + b;}

    public static double SUB_D(double a, double b) {return a - b;}

    public static double SUB_S(double a, double b) {return a - b;}

    public static double MUL_D(double a, double b) {return a * b;}

    public static double MUL_S(double a, double b) {return a * b;}

    public static double DIV_D(double a, double b) {return a / b;}

    public static double DIV_S(double a, double b) {return a / b;}

    public static int LW(int address){
        return (int)cache.readWord(address) ;
    }

    public static long LD(int address){
        return (long) cache.readDoubleWord(address);
    }

    public static float L_S(int address){
        return (float) cache.readWord(address);
    }

    public static double L_D(int address){
        return (double) cache.readDoubleWord(address);
    }

    public static void SW(String register,int address){
        cache.writeWord(address , (int)registerFile.get(register).getValue());
    }

    public static void SD(String register, int address) {
        cache.writeDoubleWord(address,(int) registerFile.get(register).getValue());
    }

    public static void S_S(String register, int address) {
        cache.writeWord(address, (float)registerFile.get(register).getValue());
    }

    public static void S_D(String register, int address) {
        cache.writeDoubleWord(address,(double)registerFile.get(register).getValue());
    }

    public static boolean BNE(double a, double b) {
        return a!=b;
    }

    public static boolean BEQ(double a, double b) {
        return a==b;
    }
}