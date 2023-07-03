package data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ResultWriter{




    public static File createFile() {
        int i = 1;
        String extension = ".txt";
        String filePath = "\\resultados\\";
        String fileName = "resultado_votacao_";

        File file = new File(fileName + i + extension);

        while (file.exists()) {
            i++;
            file = new File( fileName + i + extension);
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static void createFileContent(File file, String[] candidates, String[] electionResult, List<Vote> votes) {

        try {
            FileWriter fw = new FileWriter(file);
            fw.write("Candidatos:\n");

            for (int i = 0; i < candidates.length; i++) {
                fw.write(candidates[i]+"\n");
            }
            fw.write("\n\n");

            fw.write("Votações:\n");

            for (Vote vote : votes) {
                fw.write(vote.toString());
            }

            fw.write("\n\n");

            fw.write("Resultado da eleição:\n");

            for (int i = 0; i < electionResult.length; i++) {
                fw.write(electionResult[i]+"\n");
            }

        fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
