/*
Project: File Compression: Run Length Encoding
Author: Katie Fu
Date: Jan 12th 2023
 */

import java.util.*;
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

    public static int welcomeMsg(int cnt){ // welcome and prompts
        Scanner scanner = new Scanner(System.in);
        System.out.println("File Compression: Run Length Encoding");
        if(cnt == 1){ // beginning of program -> print developer message
            System.out.println("Developed by Katie Fu\n");
        }else{ // user has previously input wrong -> prompt again
            System.out.println("Please input a valid option! Try again\n");
        }

        // print menu
        System.out.println("Menu:");
        System.out.println("Please enter the number desired");
        System.out.println("1: File compression");
        System.out.println("2: File decompression");

        String ans = scanner.nextLine(); // input user's choice
        if(ans.equals("1") || ans.equals("2")){
            return Integer.parseInt(ans);
        }else{
            return welcomeMsg(cnt+1); // if answer is not 1 or 2, continue asking (recursion!)
        }
    }

    public static String getFile(){ // ask for file input name
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPlease enter the name of the file:"); // prompt
        String fileName = scanner.nextLine(); // input
        File file = new File(fileName);
        if(!file.exists()) { // if file does not exist, continue asking (recursion)
            return getFile();
        }
        return fileName; // return input file name
    }

    public static String outputFileName(String inputFileName,int cnt) { // find output file name
        String outputFileName = inputFileName.substring(0,inputFileName.length()-4); // remove ".txt" from input file
        outputFileName += "_out"; // add _out output file name
        if(cnt>0){ // for repeated file names, add ([num]) to end
            outputFileName += "(" + cnt + ")";
        }
        outputFileName += ".txt"; // add .txt to output file name

        File file = new File(outputFileName);
        if(file.exists()){ // determine if file exists
            // continue finding, cnt+1 updates the number in brackets
            outputFileName = outputFileName(inputFileName,cnt+1);
        }
        return outputFileName;
    }

    public static void compression(String inputFileName, String outputFileName){
        System.out.println("\nCompressing...\n");
        int numberOfLines = findLength(inputFileName); // find the length of the input file
        String[] input = new String[numberOfLines]; // create input array to store file
        String[] output = new String[numberOfLines]; // create output array to store answer
        readFile(inputFileName,input); // read file and store contents in input

        double minRatio = Integer.MAX_VALUE; // set minRatio to biggest value possible
        double maxRatio = Integer.MIN_VALUE; // set maxRatio to smallest value possible
        double avgRatio = 0; // record avgRatio

        for(int i=0 ; i<numberOfLines ; i++){ // iterate through each line of the input file
            String ans = ""; // create empty string that stores the answer
            int cnt = 1; // records the number of repetitions
            char cur = input[i].charAt(0); // records the current character

            for(int j=1;j<input[i].length();j++){ // iterate through length of line
                if(input[i].charAt(j) == input[i].charAt(j-1)){ // character is equal to character in front
                    cnt++;
                }else{
                    ans += cnt + "" + cur; // update ans
                    cur = input[i].charAt(j); // update cur
                    cnt = 1; // reset cnt
                }
                if(j==input[i].length()-1){ // if at the end of the line
                    ans += cnt + "" + cur; // update ans
                }
            }
            output[i] = ans; // update output array
            double ratio = compressionRatio(ans.length(),input[i].length()); // calculate compression ratio
            maxRatio = Math.max(maxRatio,ratio); // update max ratio
            minRatio = Math.min(minRatio,ratio); // update min ratio
            avgRatio += ratio; // add to avg ratio
        }

        avgRatio /= numberOfLines; // divide by number of lines to successfully obtain the avg ratio

        outputFile(outputFileName,output); // print output contents into the output file
        System.out.println("Compressed file is successfully stored in " + outputFileName);

        updateFile(inputFileName, outputFileName, "Compression", avgRatio, minRatio, maxRatio); // update log file
        System.out.println("Compression information has successfully been updated in log.txt");
    }

    public static void decompression(String inputFileName, String outputFileName){
        System.out.println("\nDecompressing...\n");
        int numberOfLines = findLength(inputFileName); // find the length of the input file
        String[] input = new String[numberOfLines]; // create input array to store file
        String[] output = new String[numberOfLines]; // create output array to store answer
        readFile(inputFileName,input); // read file and store contents in input

        double minRatio = Integer.MAX_VALUE; // set minRatio to biggest value possible
        double maxRatio = Integer.MIN_VALUE; // set maxRatio to smallest value possible
        double avgRatio = 0; // record avgRatio

        for(int i=0 ; i<numberOfLines ; i++){ // iterates through each line in the file
            String ans = ""; // stores the final answer
            int num = 0; // stores the number of repetitions
            char cur; // stores the current character
            for(int j=0 ; j<input[i].length() ; j++){ // iterates through each element in the line
                if(input[i].charAt(j) >= '0' && input[i].charAt(j)<='9'){ // if character is a number
                    num = num*10 + input[i].charAt(j)-'0'; // add to variable num
                }else{ // character is a letter
                    cur = input[i].charAt(j); // update cur
                    for(int k=1 ; k <= num ; k++){ // add the specific character 'num' number of times
                        ans += (char)cur;
                    }
                    num=0; // reset num
                }
            }
            output[i] = ans; // store in output string array
            double ratio = compressionRatio(input[i].length(),ans.length()); // calculate compression ratio
            maxRatio = Math.max(maxRatio,ratio); // update max ratio
            minRatio = Math.min(minRatio,ratio); // update min ratio
            avgRatio += ratio; // update avg ratio (sum)
        }

        avgRatio /= numberOfLines; // divide avg ratio by length

        outputFile(outputFileName, output); // print to output file
        System.out.println("Decompressed file is successfully stored in " + outputFileName);

        updateFile(inputFileName, outputFileName, "Decompression", avgRatio, minRatio, maxRatio); // update log file
        System.out.println("Decompression information has successfully been updated in log.txt");
    }

    public static int findLength(String inputFileName){ // finds the length of the file
        int length=0;
        try{
            File file = new File(inputFileName);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                length++;
                scanner.nextLine();
            }
        }catch(Exception e){ // if error occurs
            System.out.println("Error! Restart the program");
        }
        return length;
    }

    public static void readFile(String inputFileName,String[] input){ // stores each line of the file into the string array input
        File file = new File(inputFileName);
        try{
            Scanner scanner = new Scanner(file);
            int index=0;
            while(scanner.hasNextLine()){
                input[index++] = scanner.nextLine(); // store into input
            }
        }
        catch(Exception e){ // if error occurs
            System.out.println("Error! Restart the program");
        }
    }

    public static double compressionRatio(int compressLength,int decompressLength){ // calculate the compression ratio
        return (double) compressLength / decompressLength; // divide compressed length by decompressed length
    }

    public static double roundSecond(double input){ // round the number to its second decimal place
        input *= 100;
        int temp = (int)input;
        return (double)temp/100;
    }

    public static void updateFile(String inputFileName, String outputFileName, String actionType,
                                  double avgRatio, double minRatio, double maxRatio) {
        // updates the information into the log file
        try{
            File file = new File("log.txt");
            FileWriter fr = new FileWriter(file, true); // file writer is used rather than printstream, as this can append rather than rewriting

            String ans = inputFileName;
            for(int i=1; i<=28-inputFileName.length();i++){ // ensure even spacing
                ans += " ";
            }

            ans += outputFileName;
            for(int i=1; i<=28-outputFileName.length(); i++){ // ensure even space
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
        catch(Exception e){ // error occurs
            System.out.println("Error! Restart the program");
        }
    }

    public static void outputFile(String outputFileName, String[] output){ // prints the string array into the output file
        try{
            PrintStream ps = new PrintStream(outputFileName);
            for(int i=0; i<output.length; i++){ // iterates through each line
                ps.println(output[i]); // prints to the output file
            }
        }
        catch(Exception e){ // error occurs
            System.out.println("Error! Restart the program");
        }
    }
}

