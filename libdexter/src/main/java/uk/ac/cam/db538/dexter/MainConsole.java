package uk.ac.cam.db538.dexter;

import java.io.File;
import java.io.IOException;

import lombok.val;

import org.jf.dexlib.DexFile;

import uk.ac.cam.db538.dexter.apk.Apk;
import uk.ac.cam.db538.dexter.dex.AuxiliaryDex;
import uk.ac.cam.db538.dexter.dex.Dex;
import uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder;
import uk.ac.cam.db538.dexter.transform.Transform;
import uk.ac.cam.db538.dexter.transform.taint.UnitTestTransform;

import com.rx201.dx.translator.DexCodeGeneration;

public class MainConsole {
  
  public static void main(String[] args) throws IOException {
	DexCodeGeneration.DEBUG = false;
	DexCodeGeneration.INFO = false;
	
    if (args.length != 2) {
      System.err.println("usage: dexter <framework-dir> <apk-file>");
      System.exit(1);
    }

    val apkFile = new File(args[1]);
    if (!apkFile.isFile()) {
      System.err.println("<apk-file> is not a file");
      System.exit(1);
    }
    
    val frameworkDir = new File(args[0]);
    if (!frameworkDir.isDirectory()) {
      System.err.println("<framework-dir> is not a directory");
      System.exit(1);
    }
    
    val hierarchyBuilder = new HierarchyBuilder();
    
    System.out.println("Scanning framework");
    hierarchyBuilder.importFrameworkFolder(frameworkDir);
    
    System.out.println("Scanning application");
    val fileApp = new DexFile(apkFile);
    val fileAux = new DexFile("dexter_aux/build/libs/dexter_aux.dex");
    
    System.out.println("Building hierarchy");
    val buildData = hierarchyBuilder.buildAgainstApp(fileApp, fileAux);
    val hierarchy = buildData.getValA();
    val renamerAux = buildData.getValB();
    
    System.out.println("Parsing application");
    AuxiliaryDex dexAux = new AuxiliaryDex(fileAux, hierarchy, renamerAux); 
    Dex dexApp = new Dex(fileApp, hierarchy, dexAux);
    
    System.out.println("Instrumenting application");
    Transform transform = new UnitTestTransform();
    transform.apply(dexApp);
    
    System.out.println("Recompiling application");
    val newDex = dexApp.writeToFile();
    
    System.out.println("Generating new apk");
    Apk.produceAPK(apkFile, new File(apkFile.getAbsolutePath() + "_new.apk"), null, newDex);
    
    
    System.out.println("DONE");
  }
}
