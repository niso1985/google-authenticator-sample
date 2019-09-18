package models

import java.io.FileOutputStream
import java.net.URLEncoder

import com.google.zxing._
import com.google.zxing.client.j2se.MatrixToImageWriter
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Hex

import scala.util.Random
import org.ietf.tools.TOTP

case class GoogleAuth(secret: String){
  import GoogleAuth._
  def getCode: String = {
    val normalizedBase32Key = secret.replace(" ", "").toUpperCase
    val bytes = base32.decode(normalizedBase32Key)
    val hexKey = Hex.encodeHexString(bytes)
    val time = (System.currentTimeMillis / 1000) / 30
    val hexTime = time.toHexString
    TOTP.generateTOTP(hexKey, hexTime, "6")
  }

  def verifyCode(code: String): Boolean = code == getCode

  import java.io.UnsupportedEncodingException

  def getGoogleAuthenticatorBarCode(secretKey: String, account: String, issuer: String): String = {
    val normalizedBase32Key = secretKey.replace(" ", "").toUpperCase
    try {
      "otpauth://totp/" + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20") + "?secret=" + URLEncoder.encode(normalizedBase32Key, "UTF-8").replace("+", "%20") + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20")
    }catch {
      case e: UnsupportedEncodingException =>
        throw new IllegalStateException(e)
    }
  }

  def createQRCode(barCodeData: String, filePath: String, height: Int, width: Int): Unit = {
    val matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height)
    try {
      val out = new FileOutputStream(filePath)
      try MatrixToImageWriter.writeToStream(matrix, "png", out)
      finally if (out != null) out.close()
    }
  }
}


object GoogleAuth{
  val base32 = new Base32()
  def apply(): GoogleAuth = {
    val bytes = new Array[Byte](20)
    Random.nextBytes(bytes)
    val s = base32.encodeToString(bytes).toLowerCase.replaceAll("(.{4})(?=.{4})", "$1 ")
    new GoogleAuth(s)
  }
}