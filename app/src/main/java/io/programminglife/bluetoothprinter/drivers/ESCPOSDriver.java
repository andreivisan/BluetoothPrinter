package io.programminglife.bluetoothprinter.drivers;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Created by andreivisan on 10/28/15.
 */
public class ESCPOSDriver {

    private static String tag = ESCPOSDriver.class.getSimpleName();

    private static final byte[] LINE_FEED = {0x0A};
    private static final byte[] PAPER_FEED = {27, 0x4A, (byte)0xFF};
    private static final byte[] PAPER_CUT = {0x1D, 0x56, 0x1};
    private static final byte[] ALIGN_LEFT = {0x1B, 0x61, 0};
    private static final byte[] ALIGN_CENTER = {0x1B, 0x61, 1};
    private static final byte[] ALIGN_RIGHT = {0x1B, 0x61, 2};
    private static final byte[] BOLD_ON = {0x1B, 0x45, 1};
    private static final byte[] BOLD_OFF = {0x1B, 0x45, 0};
    private static final byte[] INIT = {0x1B, 0x40};
    private static final byte[] STANDARD_MODE = {0x1B, 0x53};

    public void initPrint(BufferedOutputStream bufferedOutputStream) {
        try {
            bufferedOutputStream.write(INIT);
            bufferedOutputStream.write(STANDARD_MODE);
        } catch (IOException e) {
            Log.e(tag, e.getMessage(), e);
        }
    }

    public void printLineAlignLeft(BufferedOutputStream bufferedOutputStream, String lineData) {
        try {
            bufferedOutputStream.write(ALIGN_LEFT);
            bufferedOutputStream.write(lineData.getBytes());
            bufferedOutputStream.write(LINE_FEED);
        } catch (IOException e) {
            Log.e(tag, e.getMessage(), e);
        }
    }

    public void printLineAlignCenter(BufferedOutputStream bufferedOutputStream, String lineData) {
        try {
            bufferedOutputStream.write(ALIGN_CENTER);
            bufferedOutputStream.write(lineData.getBytes());
            bufferedOutputStream.write(LINE_FEED);
        } catch (IOException e) {
            Log.e(tag, e.getMessage(), e);
        }
    }

    public void printLineAlignRight(BufferedOutputStream bufferedOutputStream, String lineData) {
        try {
            bufferedOutputStream.write(ALIGN_RIGHT);
            bufferedOutputStream.write(lineData.getBytes());
            bufferedOutputStream.write(LINE_FEED);
        } catch (IOException e) {
            Log.e(tag, e.getMessage(), e);
        }
    }

    public void finishPrint(BufferedOutputStream bufferedOutputStream) {
        try {
            bufferedOutputStream.write(PAPER_FEED);
            bufferedOutputStream.write(PAPER_CUT);
        } catch (IOException e) {
            Log.e(tag, e.getMessage(), e);
        }
    }

}
