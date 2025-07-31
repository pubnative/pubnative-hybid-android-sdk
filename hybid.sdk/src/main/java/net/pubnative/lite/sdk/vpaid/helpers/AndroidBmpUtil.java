// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.helpers;

import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AndroidBmpUtil {

    private final int BMP_WIDTH_OF_TIMES = 4;
    private final int BYTE_PER_PIXEL = 3;

    public boolean save(Bitmap orgBitmap, String filePath) {

        if (orgBitmap == null) {
            return false;
        }

        if (filePath == null) {
            return false;
        }

        boolean isSaveSuccess = true;

        //image size
        int width = orgBitmap.getWidth();
        int height = orgBitmap.getHeight();

        int dummySize = 0;
        byte[] dummyBytesPerRow = null;
        boolean hasDummy = false;
        if (isBmpWidth4Times(width)) {
            hasDummy = true;
            dummySize = BMP_WIDTH_OF_TIMES - (width % BMP_WIDTH_OF_TIMES);
            dummyBytesPerRow = new byte[dummySize * BYTE_PER_PIXEL];
            for (int i = 0; i < dummyBytesPerRow.length; i++) {
                dummyBytesPerRow[i] = (byte) 0xFF;
            }
        }

        int[] pixels = new int[width * height];
        int imageSize = pixels.length * BYTE_PER_PIXEL + (height * dummySize * BYTE_PER_PIXEL);
        int imageDataOffset = 0x36;
        int fileSize = imageSize + imageDataOffset;

        //Android Bitmap Image Data
        orgBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        //ByteArrayOutputStream baos = new ByteArrayOutputStream(fileSize);
        ByteBuffer buffer = ByteBuffer.allocate(fileSize);

        try {

            buffer.put((byte) 0x42);
            buffer.put((byte) 0x4D);

            //size
            buffer.put(writeInt(fileSize));

            //reserved
            buffer.put(writeShort((short) 0));
            buffer.put(writeShort((short) 0));

            //image data start offset
            buffer.put(writeInt(imageDataOffset));

            buffer.put(writeInt(0x28));

            //width, height
            buffer.put(writeInt(width));
            buffer.put(writeInt(height));

            //planes
            buffer.put(writeShort((short) 1));

            //bit count
            buffer.put(writeShort((short) 24));

            //bit compression
            buffer.put(writeInt(0));

            //image data size
            buffer.put(writeInt(imageSize));

            //horizontal resolution in pixels per meter
            buffer.put(writeInt(0));

            //vertical resolution in pixels per meter (unreliable)
            buffer.put(writeInt(0));
            buffer.put(writeInt(0));
            buffer.put(writeInt(0));

            int row = height;
            int col = width;
            int startPosition = 0;
            int endPosition = 0;

            while (row > 0) {

                startPosition = (row - 1) * col;
                endPosition = row * col;

                for (int i = startPosition; i < endPosition; i++) {
                    buffer.put(write24BitForPixcel(pixels[i]));

                    if (hasDummy) {
                        if (isBitmapWidthLastPixcel(width, i)) {
                            buffer.put(dummyBytesPerRow);
                        }
                    }
                }
                row--;
            }

            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(buffer.array());
            fos.close();

        } catch (IOException e1) {
            e1.printStackTrace();
            isSaveSuccess = false;
        } finally {

        }

        return isSaveSuccess;
    }


    private boolean isBitmapWidthLastPixcel(int width, int i) {
        return i > 0 && (i % (width - 1)) == 0;
    }

    private boolean isBmpWidth4Times(int width) {
        return width % BMP_WIDTH_OF_TIMES > 0;
    }

    private byte[] writeInt(int value) throws IOException {
        byte[] b = new byte[4];

        b[0] = (byte) (value & 0x000000FF);
        b[1] = (byte) ((value & 0x0000FF00) >> 8);
        b[2] = (byte) ((value & 0x00FF0000) >> 16);
        b[3] = (byte) ((value & 0xFF000000) >> 24);

        return b;
    }

    private byte[] write24BitForPixcel(int value) throws IOException {
        byte[] b = new byte[3];

        b[0] = (byte) (value & 0x000000FF);
        b[1] = (byte) ((value & 0x0000FF00) >> 8);
        b[2] = (byte) ((value & 0x00FF0000) >> 16);

        return b;
    }

    private byte[] writeShort(short value) throws IOException {
        byte[] b = new byte[2];

        b[0] = (byte) (value & 0x00FF);
        b[1] = (byte) ((value & 0xFF00) >> 8);

        return b;
    }
}
