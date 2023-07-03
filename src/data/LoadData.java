package data;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class LoadData {

    public static String[] loadArray() {
        List<String> candidatesList = new ArrayList<>();
        final JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);


        if(returnValue != JFileChooser.APPROVE_OPTION){
            System.out.println("File not Chosen");
            return null;
        }
           File file = fileChooser.getSelectedFile();
        try{
            Scanner scanner = new Scanner(file, "UTF-8");
            while (scanner.hasNextLine()){
                candidatesList.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        String[] candidatesArray = new String[candidatesList.size()];
        int i = 0;
        for (String s : candidatesList) {
            candidatesArray[i] = s;
            i++;
        }

        return  candidatesArray;

    }

}
