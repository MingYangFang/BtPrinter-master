package com.yefeng.night.btprinter;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.yefeng.night.btprinter.print.GPrinterCommand;
import com.yefeng.night.btprinter.print.PrintPic;
import com.yefeng.night.btprinter.print.PrintQueue;
import com.yefeng.night.btprinter.print.PrintUtil;

import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Vector;

/**
 * Created by yefeng on 6/2/15.
 * github:yefengfreedom
 * <p/>
 * print ticket service
 */
public class BtService extends IntentService {

    private static final String TAG = "BtService";

    public BtService() {
        super("BtService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BtService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(PrintUtil.ACTION_PRINT_TEST)) {
            printTest();
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT)) {
            print(intent.getByteArrayExtra(PrintUtil.PRINT_EXTRA));
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_TICKET)) {
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_BITMAP)) {
            printBitmapTest();
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_PAINTING)) {
            printPainting();
        }
    }

    private void printTest() {
        try {
            ArrayList<byte[]> bytes = new ArrayList<byte[]>();
            String message = "蓝牙打印测试\n蓝牙打印测试\n蓝牙打印测试\n\n";


//            String str = "G0AdIQEgICAgICAgICDH/rXA0rXO8dSxs8K3ydS+HSEACiAgICAgICAgICAgICAgIMihss26xTogQTcKLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLQrX+c67OiAxCraptaWx4LrFOiA2MTYzMzA2MzIyNTE5Njk3MzMyCsqxvOQ6IDIwMTYtMDctMjUgMTk6Mzk6MDMKLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLQqyy8a3ICAgICAgICAgICAgICAgICAgICAgICAgICAgILWlvNsgICAgICDK/cG/CrLmydXI4ijQobfdKSAgICAgICAgICAgICAgICAgICAgMC4xMCAgICAgICAxCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgINfcvNsgIDAuMTAKsbjXojoKCh0hASAgICAgICAgu7bTrbniwdkdIQAKCgo=";

//            String result = decryptBASE64(str, "GBK");
            bytes.add(GPrinterCommand.reset);
            bytes.add(message.getBytes("gbk"));
//            bytes.add(result.getBytes("gbk"));
            bytes.add(GPrinterCommand.print);
            bytes.add(GPrinterCommand.print);
            bytes.add(GPrinterCommand.print);
            PrintQueue.getQueue(getApplicationContext()).add(bytes);
            PrintQueue.getQueue(getApplicationContext()).add(bytes);
            PrintQueue.getQueue(getApplicationContext()).add(bytes);
            PrintQueue.getQueue(getApplicationContext()).add(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    public static String decryptBASE64(String str, String charsetName) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes(charsetName);
            // base64 解密
            return new String(Base64.decode(encode, Base64.DEFAULT), charsetName);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }




    private void print(byte[] byteArrayExtra) {
        if (null == byteArrayExtra || byteArrayExtra.length <= 0) {
            return;
        }
        PrintQueue.getQueue(getApplicationContext()).add(byteArrayExtra);
    }

    private void printBitmapTest() {
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(getAssets().open(
                    "aps.bmp"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(bis);
        PrintPic printPic = PrintPic.getInstance();
        printPic.init(bitmap);
        if (null != bitmap) {
            if (bitmap.isRecycled()) {
                bitmap = null;
            } else {
                bitmap.recycle();
                bitmap = null;
            }
        }
        byte[] bytes = printPic.printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(GPrinterCommand.reset);
        printBytes.add(GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(GPrinterCommand.print);
        PrintQueue.getQueue(getApplicationContext()).add(bytes);




//        print_image();
    }

    private void printPainting() {
        byte[] bytes = PrintPic.getInstance().printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(GPrinterCommand.reset);
        printBytes.add(GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(GPrinterCommand.print);
        PrintQueue.getQueue(getApplicationContext()).add(bytes);
    }


    private void print_image() {

        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(getAssets().open(
                    "ic_bluetooth_off.png"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bitmap bmp = BitmapFactory.decodeStream(bis);

        convertBitmap(bmp);

        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(GPrinterCommand.reset);
        printBytes.add(GPrinterCommand.print);

        printBytes.add(PrinterCommands.SET_LINE_SPACING_24);

            int offset = 0;
            while (offset < bmp.getHeight()) {
                printBytes.add(PrinterCommands.SELECT_BIT_IMAGE_MODE);
                for (int x = 0; x < bmp.getWidth(); ++x) {

                    for (int k = 0; k < 3; ++k) {

                        byte slice = 0;
                        for (int b = 0; b < 8; ++b) {
                            int y = (((offset / 8) + k) * 8) + b;
                            int i = (y * bmp.getWidth()) + x;
                            boolean v = false;
                            if (i < dots.length()) {
                                v = dots.get(i);
                            }
                            slice |= (byte) ((v ? 1 : 0) << (7 - b));
                        }
                        printBytes.add(new byte[]{slice});
                    }
                }
                offset += 24;
                printBytes.add(PrinterCommands.FEED_LINE);
//                printBytes.add(PrinterCommands.FEED_LINE);
//                printBytes.add(PrinterCommands.FEED_LINE);
//                mService.write(PrinterCommands.FEED_LINE);
//                mService.write(PrinterCommands.FEED_LINE);
//                mService.write(PrinterCommands.FEED_LINE);
            }
        printBytes.add(PrinterCommands.SET_LINE_SPACING_30);

        if (null != bmp) {
            if (bmp.isRecycled()) {
                bmp = null;
            } else {
                bmp.recycle();
                bmp = null;
            }
        }

        PrintQueue.getQueue(getApplicationContext()).add(printBytes);

    }


    BitSet dots;
    private void convertArgbToGrayscale(Bitmap bmpOriginal, int width,
                                        int height) {
        int pixel;
        int k = 0;
        int B = 0, G = 0, R = 0;
        dots = new BitSet();
        try {

            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    // get one pixel color
                    pixel = bmpOriginal.getPixel(y, x);

                    // retrieve color of all channels
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);
                    // take conversion up to one single value by calculating
                    // pixel intensity.
                    R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
                    // set bit into bitset, by calculating the pixel's luma
                    if (R < 55) {
                        dots.set(k);//this is the bitset that i'm printing
                    }
                    k++;

                }


            }


        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, e.toString());
        }
    }

    public String convertBitmap(Bitmap inputBitmap) {

        int mWidth = inputBitmap.getWidth();
        int mHeight = inputBitmap.getHeight();

        convertArgbToGrayscale(inputBitmap, mWidth, mHeight);
        String mStatus = "ok";
        return mStatus;

    }
}



