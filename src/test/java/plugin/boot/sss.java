package plugin.boot;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class sss {
    private static byte[] build3DesKey(String keyStr) throws Exception {
        byte[] key = new byte[24];
        byte[] temp = keyStr.getBytes("UTF-8");
        System.out.println(Arrays.toString(temp));
        if (key.length > temp.length){
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else{
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        System.out.println(Arrays.toString(key));
        return key;
    }

    private static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l=src.length()/2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++){
            m=i*2+1;
            n=m+1;
            ret[i]=(byte)Integer.parseInt(src.substring(i*2, m) + src.substring(m,n), 16);
        }
        return ret;
    }

    /**
     *	String src 客户拿过来的文件数据
     *	String key 代理业务号
     *	return 解密后数据
     */
    public static String Decrypt3DES(String src, String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(build3DesKey(key), "DESede");
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] b = cipher.doFinal(hexStr2Bytes(src));
        return new String(b,"UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String src = "D1A55A28C89431725AC7B56529B201ECC2467275B716B9C90E0E21365CCEEBC18E9013AFB9A8DB637464158268DD23314A03839429F11044"; // 六盘水的密文
        String key = "090500593004000"; // 安信
        System.out.println(Arrays.toString(key.getBytes()));
        String t = Decrypt3DES(src, key);
        System.out.println(t);
        System.out.println("================");
        String mm = "";
        for(byte b : key.getBytes()) {
        	mm+=Integer.toBinaryString(b);
        }
        for(int i = 0;i<mm.length();i++) {
        	if(i%8 != 7)
        		System.out.print(mm.charAt(i));
        	else
        		System.out.print(" "+mm.charAt(i)+" ");
        }
        System.out.println("================");
        mm = "";
        key = "080500583014010"; // 安信
        for(byte b : key.getBytes()) {
        	mm+=Integer.toBinaryString(b);
        }
        for(int i = 0;i<mm.length();i++) {
        	if(i%8 != 7)
        		System.out.print(mm.charAt(i));
        	else
        		System.out.print(" "+mm.charAt(i)+" ");
        }
        System.out.println("================");
        System.out.println(Arrays.toString(key.getBytes()));
        t = Decrypt3DES(src, key);
        System.out.println(t);
    }
}