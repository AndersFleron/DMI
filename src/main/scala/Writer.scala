import java.io.{BufferedOutputStream, FileOutputStream, FileWriter}
import java.util.zip.GZIPOutputStream

import ujson.{BytesRenderer, Value}

object Writer {
  def writeToGzipFile(data: Value) ={
    val bytesData = ujson.transform(data, BytesRenderer()).toBytes
    val path = sys.env("FILEPATH")
    val bos = new GZIPOutputStream(
      new BufferedOutputStream(
        new FileOutputStream(path)))
    bos.write(bytesData)
    bos.close()
  }
}
