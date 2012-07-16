/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.security;

import com.happydroids.utils.Base64;
import com.happydroids.utils.Base64DecoderException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

/**
 * An obfuscator that uses AES to encrypt data.
 */
public class AESObfuscator {
  private static final String UTF8 = "UTF-8";
  private static final String KEYGEN_ALGORITHM = "PBKDF2WithHmacSHA1";
  private static final String CIPHER_ALGORITHM = "AES/CTR/NoPadding";
  private static final byte[] IV =
          {16, 74, 71, -80, 32, 101, -47, 72, 117, -14, 0, -29, 70, 65, -12, 74};
  private static final String header = AESObfuscator.class.getName() + "|";

  private Cipher mEncryptor;
  private Cipher mDecryptor;

  public AESObfuscator(byte[] salt, String password) {
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_ALGORITHM);
      KeySpec keySpec =
              new PBEKeySpec(password.toCharArray(), salt, 1024, 128);
      SecretKey tmp = factory.generateSecret(keySpec);
      SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
      mEncryptor = Cipher.getInstance(CIPHER_ALGORITHM);
      mEncryptor.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(IV));
      mDecryptor = Cipher.getInstance(CIPHER_ALGORITHM);
      mDecryptor.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
    } catch (GeneralSecurityException e) {
      // This can't happen on a compatible Android device.
      throw new RuntimeException("Invalid environment", e);
    }
  }

  public String obfuscate(String original) {
    if (original == null) {
      return null;
    }
    try {
      // Header is appended as an integrity check
      return Base64.encode(mEncryptor.doFinal((header + original).getBytes(UTF8)));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Invalid environment", e);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException("Invalid environment", e);
    }
  }

  public String unobfuscate(String obfuscated) throws ValidationException {
    if (obfuscated == null) {
      return null;
    }
    try {
      String result = new String(mDecryptor.doFinal(Base64.decode(obfuscated)), UTF8);
      // Check for presence of header. This serves as a final integrity check, for cases
      // where the block size is correct during decryption.
      int headerIndex = result.indexOf(header);
      if (headerIndex != 0) {
        throw new ValidationException("Header not found (invalid data or key)" + ":" +
                                              obfuscated);
      }
      return result.substring(header.length(), result.length());
    } catch (Base64DecoderException e) {
      throw new ValidationException(e.getMessage() + ":" + obfuscated);
    } catch (IllegalBlockSizeException e) {
      throw new ValidationException(e.getMessage() + ":" + obfuscated);
    } catch (BadPaddingException e) {
      throw new ValidationException(e.getMessage() + ":" + obfuscated);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Invalid environment", e);
    }
  }

  public class ValidationException extends Exception {
    public ValidationException() {
      super();
    }

    public ValidationException(String s) {
      super(s);
    }

    private static final long serialVersionUID = 1L;
  }

}
