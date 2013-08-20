/*
 * Copyright 2012 Bangkok Project Team, GRIDSOLUT GmbH + Co.KG, and
 * University of Stuttgart (Institute of Architecture of Application Systems)
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allweathercomputing.htm.org;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;

public class SHATest {

    private static final char[] HEXADECIMAL = {'0', '1', '2', '3',

            '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String hashSha256(String value) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest("test".getBytes());

            StringBuffer bf = new StringBuffer(64);
            for (byte b : hash) {
                bf.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            // System.out.println("Hash: " + bf.toString());
            return bf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("Hash couldn't be determined", e);
        }
    }

    public static char[] hashPassword(String password)

            throws Exception {

        byte[] bytes = null;

        char[] result = null;

        String charSet = "UTF-8";

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        //bytes = Utility.convertCharArrayToByteArray(password, charSet);

        bytes = password.getBytes(charSet);

        if (md != null) {

            synchronized (md) {

                md.reset();

                bytes = md.digest(bytes);

            }

        }

        result = hexEncode(bytes);

        return result;

    }

    public static char[] hexEncode(byte[] bytes) {

        StringBuilder sb = new StringBuilder(2 * bytes.length);

        for (int i = 0; i < bytes.length; i++) {

            int low = (int) (bytes[i] & 0x0f);

            int high = (int) ((bytes[i] & 0xf0) >> 4);

            sb.append(HEXADECIMAL[high]);

            sb.append(HEXADECIMAL[low]);

        }

        char[] result = new char[sb.length()];

        sb.getChars(0, sb.length(), result, 0);

        return result;

    }

    public static void main(String[] args) {
        try {
            System.out.println(hashSha256(args[0]));
            System.out.println(hashPassword(args[0]));
            System.out.println(DigestUtils.sha256Hex(args[0]));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
