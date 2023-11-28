package com.nowcoder.community1.community1;

import java.io.IOException;

public class WkTests {
    public static void main(String[] args) {
        String cmd = "d:/software/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcode.com d:/niukePro/data/wk-images/2.png";
        try{
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
