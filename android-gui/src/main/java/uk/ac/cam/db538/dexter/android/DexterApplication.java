package uk.ac.cam.db538.dexter.android;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.rx201.dx.translator.DexCodeGeneration;

import org.apache.commons.io.IOUtils;
import org.jf.dexlib.DexFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

import dalvik.system.DexClassLoader;
import uk.ac.cam.db538.dexter.dex.type.ClassRenamer;
import uk.ac.cam.db538.dexter.dex.type.DexTypeCache;
import uk.ac.cam.db538.dexter.hierarchy.builder.HierarchyBuilder;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;
import uk.ac.cam.db538.dexter.utils.Pair;

public class DexterApplication extends Application {

    public static final String APP_NAME = "DEXTER";

    private File frameworkCache;
    private File frameworkFolder;

    private HierarchyBuilder hierarchyBuilder;
    private Semaphore hierarchyAvailable;

    @Override
    public void onCreate() {
        super.onCreate();

        // disable debug
        DexCodeGeneration.DEBUG = false;
        DexCodeGeneration.INFO = true;

        // create file/dir constants
        frameworkCache = new File(this.getFilesDir(), "framework.cache");
        frameworkFolder = new File("/system/framework/");

        // initialize semaphore that will be acquired until the hierarchy is loaded
        hierarchyAvailable = new Semaphore(1);
        try {
            hierarchyAvailable.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        // start a background thread that will create the hierarchy builder
        workerHierarchyLoader.start();
        workerHierarchyLoader.setPriority(Thread.MIN_PRIORITY);
    }

    private final Thread workerHierarchyLoader = new Thread() {
        @Override
        public void run() {
            HierarchyBuilder builder;
            boolean cached = true;

            builder = loadFromCache();
            if (builder == null) {
                cached = false;
                builder = loadFromSystemFolder();
                if (builder == null)
                    throw new RuntimeException("Cannot load framework files");
            }

            DexterApplication.this.hierarchyBuilder = builder;
            DexterApplication.this.hierarchyAvailable.release();

            if (!cached)
                synchronized (hierarchyBuilder) {
                    try {
                        Log.d(APP_NAME, "Storing framework into " + frameworkCache.getAbsolutePath());
                        hierarchyBuilder.serialize(frameworkCache);
                        Log.d(APP_NAME, "Framework successfully stored");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(APP_NAME, "Framework could not be stored");
                    }
                }
        }

        private HierarchyBuilder loadFromCache() {
            if (!frameworkCache.exists())
                return null;

            Log.d(APP_NAME, "Loading framework from " + frameworkCache.getAbsolutePath());

            HierarchyBuilder result;
            try {
                 result = HierarchyBuilder.deserialize(frameworkCache);
            } catch (Throwable e) {
                Log.e(APP_NAME, "Framework could not be loaded from cache");
                e.printStackTrace();
                return null;
            }

            Log.d(APP_NAME, "Framework successfully loaded from cache");
            return result;
        }

        private HierarchyBuilder loadFromSystemFolder() {
            if (!frameworkFolder.exists())
                return null;

            Log.d(APP_NAME, "Loading framework from " + frameworkFolder.getAbsolutePath());

            HierarchyBuilder result = new HierarchyBuilder();
            try {
                result.importFrameworkFolder(frameworkFolder);
            } catch (IOException e) {
                Log.e(APP_NAME, "Framework could not be loaded from system folder");
                e.printStackTrace();
                return null;
            }

            Log.d(APP_NAME, "Framework successfully loaded from system folder");
            return result;
        }
    };

    public void waitForHierarchy() {
        try {
            hierarchyAvailable.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        hierarchyAvailable.release();
    }

    public Pair<RuntimeHierarchy, ClassRenamer> getRuntimeHierarchy(DexFile fileApp, DexFile fileAux) {
        waitForHierarchy();
        synchronized (hierarchyBuilder) {
            return hierarchyBuilder.buildAgainstApp(fileApp, fileAux);
        }
    }

    public void importInternalDex(DexFile file) {
        waitForHierarchy();
        synchronized (hierarchyBuilder) {
            hierarchyBuilder.importDex(file, true);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Log.d(APP_NAME, "Being asked to free memory");
    }

    public static String getOwnPackageName() {
        return DexterApplication.class.getPackage().getName();
    }
}
