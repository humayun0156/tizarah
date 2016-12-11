package utils

import java.util.Date

import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

import play.api.Logger

/**
  * @author Humayun
  */
object EncryptUtil {
  private val IV: String = "AbCdEfGhIjKlMnOp" //length shouldBe 16
  private val ENCRYPTION_KEY: String = getEncryptionKey //"mys3cr3tk3yvlDA$" //length shouldBe 16
  private val ALGORITHM: String = "AES/CBC/PKCS5Padding"
  val logger = Logger(this.getClass)

  def encrypt(plainText: String): String = {
    try {
      val cipher: Cipher = Cipher.getInstance(ALGORITHM)
      val key: SecretKeySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES")
      cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")))
      val bytes: Array[Byte] = cipher.doFinal(plainText.getBytes("UTF-8"))
      Base64.encodeBase64String(bytes)
    } catch {
      case ex: Exception =>
        logger.error("EncryptError", ex)
        ""
    }
  }

  def decrypt(cipherText: String): String = {
    try {
      val cipher: Cipher = Cipher.getInstance(ALGORITHM)
      val key: SecretKeySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES")
      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")))
      val decodeBase64: Array[Byte] = Base64.decodeBase64(cipherText)
      new String(cipher.doFinal(decodeBase64), "UTF-8")
    } catch {
      case ex: Exception =>
        logger.error("decryptError", ex)
        ""
    }
  }

  def getEncryptionKey: String = {
    val x = Utility.formattedDateAsString(new Date().getTime, "ddMMYYYY")
    "aB3u11a6" + x
  }

  def main(args: Array[String]): Unit = {
    val plainText = "shopId=1;userId=1"
    println("plainText: " + plainText)

    val cipherText = encrypt(plainText)
    println("cipherText: " + cipherText)

    val decrypted = decrypt(cipherText)
    println("decrypt: " + decrypted)

    println(getEncryptionKey)
  }

}
