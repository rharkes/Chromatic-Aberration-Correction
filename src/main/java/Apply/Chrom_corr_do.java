/* 
 * Copyright (C) 2018 Rolf Harkes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Apply;

import Classes.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.imagej.ImageJ;
import org.json.JSONArray;
import org.json.JSONObject;

import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.Previewable;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Does a pre-defined affine transform on x-y data in a csv-file
 */
@Plugin(type = Command.class, headless = true,
        menuPath = "Plugins>Chromatic Aberation Correction>Do Affine")
public class Chrom_corr_do implements Command, Previewable {

    @Parameter
    private LogService log;

    @Parameter
    private StatusService statusService;

    @Parameter(label = "CSV source", description = "Source csv file", style="open")
    private File csvfile1;
    
    @Parameter(label = "CSV target", description = "Target csv file", style="save")
    private File csvfile2;
    
    @Parameter(label = "Affine Transformation")
    private File affine_file;

    public static void main(final String... args) throws Exception {
        // Launch ImageJ as usual.
        final ImageJ ij = net.imagej.Main.launch(args);

        // Launch the command.
        ij.command().run(Chrom_corr_do.class, true);
    }
    
    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    @Override
    public void run() {
        try {
            csvread file1 = new csvread(csvfile1);
            double[] x1 = file1.getdata("x [nm]");
            double[] y1 = file1.getdata("y [nm]");
            JSONObject JObj = new JSONObject(readFile(affine_file.toString(),StandardCharsets.UTF_8));
            JSONArray JArr = JObj.getJSONArray("Values");
            double[][] affine = new double[3][2];
            for (int i = 0;i<3;i++){
                for (int j = 0;j<2;j++){
                    affine[i][j]=JArr.getDouble(i*2+j);
                }
            }
            
            AffineTransform atrans = new AffineTransform();
            atrans.setAffine(affine);
            double[][] res_xy = atrans.correctpositions(x1, y1);
            csvwrite file2 = new csvwrite(csvfile2);
            //copy
            file2.setheader(file1.getheader());
            file2.setdata(file1.getdata());
            //replace
            file2.setdata("x [nm]",res_xy[0]);
            file2.setdata("y [nm]",res_xy[1]);
            //write
            file2.writeall();
        } catch (IOException ex) {
            Logger.getLogger(Chrom_corr_do.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void cancel() {
        log.info("Cancelled");
    }

    @Override
    public void preview() {
        log.info("Preview");
    }
}