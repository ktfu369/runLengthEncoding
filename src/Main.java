/*
Project: File Compression: Run Length Encoding
Author: Katie Fu
 */

import java.util.*;
import java.util.Scanner;
import java.io.File;
import java.io.PrintStream;
import java.io.FileWriter;

public class Main {

    public static void main(String[] args) {
        int action = welcomeMsg(1);

        String fileName = getFile(); // ask for input file name
        String outputName = outputFileName(fileName, 0); // find output file name

        if(action == 1) { // user has selected file compression
            compression(fileName, outputName);
        }else{ // user has selected file decompression
            decompression(fileName, outputName);
        }
        System.out.println("\nThank you for using File Compression: Run Length Encoding!");
        System.out.println("Come again next time!");

    }

    public static int welcomeMsg(int cnt){
        Scanner scanner = new Scanner(System.in);
        System.out.println("File Compression: Run Length Encoding");
        if(cnt == 1){
            System.out.println("Developed by Katie Fu\n");
        }else{
            System.out.println("Please input a valid option! Try again\n");
        }
        System.out.println("Menu:");
        System.out.println("Please enter the number desired");
        System.out.println("1: File compression");
        System.out.println("2: File decompression");
        String ans = scanner.nextLine();
        if(ans.equals("1") || ans.equals("2")){
            return Integer.parseInt(ans);
        }else{
            return welcomeMsg(cnt+1); // if answer is not 1 or 2, continue asking (recursion!)
        }
    }

    public static String getFile(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPlease enter the name of the file:");
        String fileName = scanner.nextLine();
        try{
            File file = new File(fileName);
            Scanner scanner2 = new Scanner(file);
        }catch(Exception e){
            System.out.println("Error! File does not exist");
            return getFile();
        }
        return fileName;
    }

    public static String outputFileName(String inputFileName,int cnt) {
        String outputFileName = inputFileName.substring(0,inputFileName.length()-4);
        outputFileName += "_out";
        if(cnt>0){
            outputFileName += "(" + cnt + ")";
        }
        outputFileName += ".txt";
        File file = new File(outputFileName);
        if(file.exists()){
            outputFileName = outputFileName(inputFileName,cnt+1);
        }
        return outputFileName;
    }

    public static void compression(String inputFileName, String outputFileName){
        System.out.println("\nCompressing...\n");
        int numberOfLines = findLength(inputFileName);
        String[] input = new String[numberOfLines];
        String[] output = new String[numberOfLines];
        readFile(inputFileName,input);

        double minRatio = Integer.MAX_VALUE;
        double maxRatio = Integer.MIN_VALUE;
        double avgRatio = 0;

        for(int i=0 ; i<numberOfLines ; i++){
            String ans = "";
            int cnt = 1;
            char cur = input[i].charAt(0);
            for(int j=1;j<input[i].length();j++){
                if(input[i].charAt(j) == input[i].charAt(j-1)){
                    cnt++;
                }else{
                    ans += cnt + "" + (char)cur;
                    cur = input[i].charAt(j);
                    cnt = 1;
                }
                if(j==input[i].length()-1){
                    ans += cnt + "" + (char)cur;
                }
            }
            output[i] = ans;
            double ratio = compressionRatio(ans.length(),input[i].length());
            maxRatio = Math.max(maxRatio,ratio);
            minRatio = Math.min(minRatio,ratio);
            avgRatio += ratio;
        }

        avgRatio /= numberOfLines;

        outputFile(outputFileName,output);
        System.out.println("Compressed file is successfully stored in " + outputFileName);

        updateFile(inputFileName, outputFileName, "Compression", avgRatio, minRatio, maxRatio);
        System.out.println("Compression information has successfully been updated in log.txt");
    }

    public static void decompression(String inputFileName, String outputFileName){
        System.out.println("\nDecompressing...\n");
        int numberOfLines = findLength(inputFileName);
        String[] input = new String[numberOfLines];
        String[] output = new String[numberOfLines];
        readFile(inputFileName,input);

        double minRatio = Integer.MAX_VALUE;
        double maxRatio = Integer.MIN_VALUE;
        double avgRatio = 0;

        for(int i=0 ; i<numberOfLines ; i++){
            String ans = "";
            int num = 0;
            char cur;
            for(int j=0 ; j<input[i].length() ; j++){
                if(input[i].charAt(j) >= '0' && input[i].charAt(j)<='9'){
                    num = num*10 + input[i].charAt(j)-'0';
                }else{
                    cur = input[i].charAt(j);
                    for(int k=1 ; k <= num ; k++){
                        ans += (char)cur;
                    }
                    num=0;
                }
            }
            output[i] = ans;
            double ratio = compressionRatio(input[i].length(),ans.length());
            maxRatio = Math.max(maxRatio,ratio);
            minRatio = Math.min(minRatio,ratio);
            avgRatio += ratio;
        }

        avgRatio /= numberOfLines;

        outputFile(outputFileName, output);
        System.out.println("Decompressed file is successfully stored in " + outputFileName);

        updateFile(inputFileName, outputFileName, "Decompression", avgRatio, minRatio, maxRatio);
        System.out.println("Decompression information has successfully been updated in log.txt");
    }

    public static int findLength(String inputFileName){
        int length=0;
        try{
            File file = new File(inputFileName);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                length++;
                scanner.nextLine();
            }
        }catch(Exception e){
            System.out.println("Error! Restart the program");
        }
        return length;
    }

    public static void readFile(String inputFileName,String[] input){
        File file = new File(inputFileName);
        try{
            Scanner scanner = new Scanner(file);
            int index=0;
            while(scanner.hasNextLine()){
                input[index++] = scanner.nextLine();
            }
        }
        catch(Exception e){
            System.out.println("Error! Restart the program");
        }
    }

    public static double compressionRatio(int compressLength,int decompressLength){
        return (double) compressLength / decompressLength;
    }

    public static double roundSecond(double input){
        input *= 100;
        int temp = (int)input;
        return (double)temp/100;
    }

    public static void updateFile(String inputFileName, String outputFileName, String actionType,
                                  double avgRatio, double minRatio, double maxRatio) {
        try{
            File file = new File("log.txt");
            FileWriter fr = new FileWriter(file, true);

            String ans = inputFileName;
            for(int i=1; i<=28-inputFileName.length();i++){
                ans += " ";
            }

            ans += outputFileName;
            for(int i=1; i<=28-outputFileName.length(); i++){
                ans += " ";
            }

            ans += actionType + "     ";
            if(actionType.equals("Compression")) ans += "  ";

            avgRatio = roundSecond(avgRatio);
            ans += avgRatio + "        ";

            minRatio = roundSecond(minRatio);
            ans += minRatio + "       ";

            maxRatio = roundSecond(maxRatio);
            ans += maxRatio;

            ans += "\n";
            fr.write(ans);
            fr.close();
        }
        catch(Exception e){
            System.out.println("Error! Restart the program");
        }
    }

    public static void outputFile(String outputFileName, String[] output){
        try{
            PrintStream ps = new PrintStream(outputFileName);
            for(int i=0; i<output.length; i++){
                ps.println(output[i]);
            }
        }
        catch(Exception e){
            System.out.println("Error! Restart the program");
        }
    }
}

