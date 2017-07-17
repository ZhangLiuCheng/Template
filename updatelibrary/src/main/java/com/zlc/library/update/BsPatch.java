package com.zlc.library.update;

/**
 * http://www.daemonology.net/bsdiff/
 * http://www.bzip.org
 */
public class BsPatch {

    static {
        System.loadLibrary("bspatch");
    }

    public native static void patch(String oldFile, String newFile, String patchFile);
}
