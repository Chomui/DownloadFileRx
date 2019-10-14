package chi.training.downloadfilerx.download

import android.util.Log
import io.reactivex.Observable
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

sealed class Download {

    class Success : Download()
    class Failure : Download()

    data class Progress(
        var url: String
    ) : Download() {
        var bytesRead: Int = 0
        var contentLength: Int = 0
        var isCanceled = false
        var isDownload = false

        fun getPercent(): Int {
            return bytesRead * 100 / contentLength
        }

        fun downloadFile(filesDir: File): Observable<in Download> {
            return Observable.create { emitter ->
                val url: URL = URL(this.url)
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.doInput = true
                urlConnection.connect()
                contentLength = urlConnection.contentLength

                val sdcard = filesDir
                val file = File(sdcard, Random.nextLong().toString())

                /*if(file.exists()) {
                    Log.i("File", "File exists")
                }*/

                val fileOutput = FileOutputStream(file)
                val inputStream = urlConnection.inputStream

                val buffer: ByteArray = ByteArray(1024)
                var bufferLength = inputStream.read(buffer)

                while (bufferLength > 0) {
                    if (!isCanceled) {
                        bytesRead += bufferLength
                        fileOutput.write(buffer, 0, bufferLength)
                        emitter.onNext(this)
                        bufferLength = inputStream.read(buffer)
                    } else {
                        file.delete()
                        bytesRead = 0
                        emitter.onNext(Download.Failure())
                        break
                    }
                }

                if (bytesRead == contentLength && !isCanceled) {
                    bytesRead = 0
                    emitter.onNext(Download.Success())
                }

                emitter.onComplete()
                fileOutput.close()
            }
        }
    }


}