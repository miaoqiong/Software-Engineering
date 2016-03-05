package utilities;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip {

    public static void unzipFile(File zipFile, String outputFolder) throws IOException {

        byte[] buffer = new byte[1024];
        //get the zip file content
        ZipInputStream input = new ZipInputStream(new FileInputStream(zipFile));
        //get the zipped file list entry
        ZipEntry zipEntry = input.getNextEntry();

        while (zipEntry != null) {

            String fileName = zipEntry.getName();

            if (fileName.contains("demographics.csv") || fileName.contains("app.csv") || fileName.contains("app-lookup.csv")) {

                File newFile = new File(outputFolder + File.separator + fileName);
                /*create all non exists folders
                 else you will hit FileNotFoundException for compressed folder*/
                FileOutputStream output = null;

                if (!newFile.exists()) {
                    new File(newFile.getParent()).mkdirs();

                    output = new FileOutputStream(newFile);
                    int len;
                    while ((len = input.read(buffer)) > 0) {
                        output.write(buffer, 0, len);
                    }
                }

                if (output != null) {
                    output.close();
                }
                
            }
            
            zipEntry = input.getNextEntry();

        }
        input.closeEntry();
        input.close();

    }
}
