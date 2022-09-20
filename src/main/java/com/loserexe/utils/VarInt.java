package com.loserexe.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VarInt {
    // https://wiki.vg/Protocol#VarInt_and_VarLong
    private static final int SEGMENT_BIT = 0x7f;
    private static final int CONTINUE_BIT = 0x80;

    public static int readVarInt(DataInputStream in) {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            try {
                currentByte = in.readByte();
                value |= (currentByte & SEGMENT_BIT) << position;

                if ((currentByte & CONTINUE_BIT) == 0) break;

                position += 7;

                if (position >= 32) throw  new RuntimeException("VarInt is too big");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return value;
    }

    public void writeVarInt(DataOutputStream out, int value) {
        while (true) {
            if ((value & ~SEGMENT_BIT) == 0) {
                try {
                    out.writeByte(value);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            try {
                out.writeByte((value & SEGMENT_BIT) | CONTINUE_BIT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            value >>>= 7;
        }
    }
}
