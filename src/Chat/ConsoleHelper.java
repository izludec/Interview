package Chat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper
{
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static void writeMessage(String message) {
        System.out.println(message);
    }
    public static String readString(){
        String out = "";
        try
        {
            out = reader.readLine();
        }
        catch (IOException e){
           writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
           out = readString();
        }
       return out;
    }
    public static int readInt(){
        int num=0;
        try
        {
            num=Integer.parseInt(readString());
        }
        catch (NumberFormatException e){
            writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            num = readInt();
        }
        return num;
    }
}
