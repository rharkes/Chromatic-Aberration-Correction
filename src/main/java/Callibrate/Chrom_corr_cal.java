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
package Callibrate;

import Classes.*;

import java.io.File;
import net.imagej.ImageJ;

import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.Previewable;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.Button;

/**
 * Fits the affine transform to two csv-files of for example multi-colored beads
 */
@Plugin(type = Command.class, headless = true,
        menuPath = "Plugins>Chromatic Aberation Correction>Callibrate Affine")
public class Chrom_corr_cal implements Command, Previewable {

    @Parameter
    private LogService log;

    @Parameter
    private StatusService statusService;

    @Parameter(label = "Positions 1", description = "csv file 1")
    private File csvfile1;

    @Parameter(label = "Positions 2", description = "csv file 2", style = "open")
    private File csvfile2;
    
    @Parameter(label = "Fit Affine Transformation", callback = "fitaffine")
    private Button FitAffine;
    
    @Parameter(label = "Affine Transformation")
    private String Affine;

    public static void main(final String... args) throws Exception {
        // Launch ImageJ as usual.
        final ImageJ ij = net.imagej.Main.launch(args);

        // Launch the command.
        ij.command().run(Chrom_corr_cal.class, true);
    }

    @Override
    public void run() {

    }

    @Override
    public void cancel() {
        log.info("Cancelled");
    }

    @Override
    public void preview() {
        log.info("Preview");
    }
    
    public void fitaffine() {
        csvread file1 = new csvread(csvfile1);
        csvread file2 = new csvread(csvfile2);
        double[] x1 = file1.getdata("x [nm]");
        double[] y1 = file1.getdata("y [nm]");
        double[] x2 = file2.getdata("x [nm]");
        double[] y2 = file2.getdata("y [nm]");
        AffineTransform atrans = new AffineTransform();
        atrans.loadpositions(x1, y1, x2, y2);
        Affine = atrans.getAffineS();
    }
}