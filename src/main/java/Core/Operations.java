package Core;

import static Core.Main.cache;
import static Core.Main.registerFile;

public class Operations {

    //a will be the content inside the register, b immediate
    public static long DADDI(long a, short b) {
        return a + b;
    }

    public static long SUBBI(long a, short b) {
        return a - b;
    }

    public static double ADD_D(double a, double b) {
        return a + b;
    }

    public static float ADD_S(float a, float b) {
        return a + b;
    }

    public static double SUB_D(double a, double b) {
        return a - b;
    }

    public static float SUB_S(float a, float b) {
        return a - b;
    }

    public static double MUL_D(double a, double b) {
        return a * b;
    }
    public static float MUL_S(float a, float b) {
        return a * b;
    }

    public static double DIV_D(double a, double b) {
        return a / b;
    }

    public static float DIV_S(float a, float b) {
        return a / b;
    }

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
        cache.writeDoubleWord(address,(long) registerFile.get(register).getValue());
    }

    public static void S_S(String register, int address) {
        cache.writeWord(address, (float)registerFile.get(register).getValue());
    }

    public static void S_D(String register, int address) {
        cache.writeDoubleWord(address,(double)registerFile.get(register).getValue());
    }

    //return true if not equal so branch
    public static boolean BNE(long a, long b) {
        return a!=b;
    }

    //return true if equal so branch
    public static boolean BEQ(long a, long b) {
        return a==b;
    }
}