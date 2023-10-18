import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class FileHash {
  public static void evaluateHash(String sourceFilePath, String targetFilePath) {
    try {
      File sourceFile = new File(sourceFilePath);
      File targetFile = new File(targetFilePath);

      FileInputStream reader = new FileInputStream(sourceFile);
      PrintWriter writer = new PrintWriter(targetFile);

      MessageDigest md = MessageDigest.getInstance("SHA-256"); // define the algorithm
      byte[] dataByteArray = new byte[8192]; // buffer array to store text as a array of bytes when the text is updated
      // to md. (Eğer burada 1024 yerine textin byte'ından küçük mesela 256 gibi bir
      // değer belirlersek, aşağıdaki while döngüsünde part part okuyacaktır. büyük
      // değer verildiğinde eğer text in byte değerinden fazlaysa tek seferde
      // okuyacaktır)

      int bytesRead;

      // this allows us to read the file part by part. if we assign the size of
      // databytearray smaller than size of text bytes, it reads part by part.
      while ((bytesRead = reader.read(dataByteArray)) != -1) { // until end of the file. reader will return -1 when it
        // reaches to end of the file.
        // every step, bytesRead will be equal to number of bytes that are read by
        // reader.
        // and the content of dataByteArray is updated with each read operation.

        md.update(dataByteArray, 0, bytesRead); // updates the digest with dataByteArray from byte 0 to byte bytesRead

        // !!!!DENEME AMAÇLI
        String str2 = new String(dataByteArray, StandardCharsets.UTF_8);
        System.out.println("dizi bu adimda: " + str2 + "\nbytesRead: " + bytesRead);

      }

      byte[] hashByteArray = md.digest(); // hash value is calculated with this command and assigned hashByteArray

      StringBuilder hashHexArray = new StringBuilder();
      for (byte b : hashByteArray) { // each value in the hasByteArray is converted the hexadecimal value with format
        // function and append to hashHexArray
        hashHexArray.append(String.format("%02x", b));
      }

      writer.println(hashHexArray); // write the hash value of the text to the target file

      reader.close();
      writer.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void encryptFile(SecretKey secretKey, Cipher cipher) {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey); // nesneyi encrypt mode da encrypt key ile başlatıyoruz

      String inputFilePath = "Albil_staj_hash.txt"; // şifrelenecek file
      String encryptedFilePath = "encryptedfile.enc"; // şifrelenmiş hali

      FileInputStream fis = new FileInputStream(inputFilePath);
      FileOutputStream fos = new FileOutputStream(encryptedFilePath);

      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = fis.read(buffer)) != -1) { // read işlemi ile okuruz ve okunan byte sayısını
        // bytesRead değişkenine atarız. buffer ı her
        // seferinde günceller read
        byte[] encryptedData = cipher.update(buffer, 0, bytesRead); // update ile veriyi şifreleriz. buffer ı
        // byte 0 dan bytesRead değişkenindeki byte
        // sayısına kadar şifreler.
        fos.write(encryptedData); // şifrelenmiş dosyaya yaz
      }

      byte[] finalData = cipher.doFinal(); // şifreleme işlemini tamamlar
      fos.write(finalData); // dosyaya yaz

      fis.close();
      fos.close();

      System.out.println("Dosya başariyla şifrelendi");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void decryptFile(SecretKey secretKey, Cipher cipher) {
    try {
      byte[] keyBytes = secretKey.getEncoded(); // decrypt işleminde de kullanabilmek için key'in byte dizisi

      SecretKey decryptKey = new SecretKeySpec(keyBytes, "AES"); // decrypt için key

      String encryptedFilePath = "encryptedfile.enc";
      String decryptedFilePath = "decryptedfile.txt";

      FileInputStream fis = new FileInputStream(encryptedFilePath);
      FileOutputStream fos = new FileOutputStream(decryptedFilePath);

      cipher.init(Cipher.DECRYPT_MODE, decryptKey); // cipher nesnesini decrypt modda ve decrypt key ile
      // başlatırız

      byte[] buffer2 = new byte[1024];
      int bytesRead2;
      while ((bytesRead2 = fis.read(buffer2)) != -1) {
        byte[] decryptedData = cipher.update(buffer2, 0, bytesRead2);
        fos.write(decryptedData);
      }

      byte[] finalData2 = cipher.doFinal();
      fos.write(finalData2);

      fis.close();
      fos.close();

      System.out.println("Dosya başariyla çözüldü.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void replaceText() {
    try {
      Scanner scanner = new Scanner(System.in); // to read from console
      System.out.println("Enter any text you want: ");
      String input = scanner.nextLine();
      if (input.length() == 0) {
        System.out.println("Please write something: ");
        input = scanner.nextLine();
      }

      scanner.close();

      String targetFilePath = "C:\\Users\\user\\Desktop\\Albil_staj_hash_proje\\Albil_staj.txt";
      PrintWriter writer = new PrintWriter(targetFilePath);
      writer.println(input);

      writer.close();

      System.out.println("text has been changed");

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void compareHashedFiles(String firstFile, String secondFile) {
    try {
      FileInputStream fis1 = new FileInputStream(firstFile);
      FileInputStream fis2 = new FileInputStream(secondFile);
      String data1 = " ";
      String data2 = " ";

      byte[] buffer = new byte[1024];
      int bytesRead;

      while ((bytesRead = fis1.read(buffer)) != -1) {
        data1 = new String(buffer, 0, bytesRead);
      }

      byte[] buffer2 = new byte[1024];
      bytesRead = 0;
      while ((bytesRead = fis2.read(buffer2)) != -1) {
        data2 = new String(buffer2, 0, bytesRead);
      }

      fis1.close();
      fis2.close();

      if (data1.equals(data2)) {
        System.out.println("the file has not been changed");
      } else {
        System.out.println("the file has been changed");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) {
    try {
      String sourceFilePath = "C:\\Users\\user\\Desktop\\Albil_staj_hash_proje\\Albil_staj.txt";
      String targetFilePath = "C:\\Users\\user\\Desktop\\Albil_staj_hash_proje\\Albil_staj_hash.txt";
      String replacedTextFilePath = "C:\\Users\\user\\Desktop\\Albil_staj_hash_proje\\Albil_staj_hash_2.txt";

      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES"); // key oluşturmak için nesne
      keyGenerator.init(128);
      SecretKey secretKey = keyGenerator.generateKey(); // encrypt key

      Cipher cipher = Cipher.getInstance("AES"); // şifreleme işlemleri için nesne

      evaluateHash(sourceFilePath, targetFilePath);

      encryptFile(secretKey, cipher);

      replaceText();

      evaluateHash(sourceFilePath, replacedTextFilePath);

      decryptFile(secretKey, cipher);

      compareHashedFiles(targetFilePath, replacedTextFilePath);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
