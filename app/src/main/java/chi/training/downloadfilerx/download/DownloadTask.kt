package chi.training.downloadfilerx.download

import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.random.Random

class DownloadTask: Runnable {
    val id: UUID = UUID.randomUUID()
    val info: Info = Info(id)
    var status: Boolean = false
    var percent: Int = 0
    var path: File
    var url: URL
    var request: DownloadRequest

    constructor(request: DownloadRequest) {
        this.request = request
        path = request.path
        url = URL(request.url)
    }

    override fun run() {
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        urlConnection.doInput = true
        urlConnection.connect()
        info.contentLength = urlConnection.contentLength

        val file = File(path, request.fileName)

        val fileOutput = FileOutputStream(file)
        val inputStream = urlConnection.inputStream

        val buffer: ByteArray = ByteArray(1024)
        var bufferLength = inputStream.read(buffer)

        info.status = Status.DOWNLOAD_START
        val msg = request.handler.obtainMessage(DownloadManager.IS_STARTED, info)
        request.handler.sendMessage(msg)
        while (bufferLength > 0) {
            if (!status) {
                info.bytesRead += bufferLength
                percent = info.bytesRead * 100 / info.contentLength
                fileOutput.write(buffer, 0, bufferLength)
                //emitter.onNext(this)
                info.status = Status.DOWNLOAD_START
                val msg = request.handler.obtainMessage(DownloadManager.IN_DOWNLOADING, info)
                request.handler.sendMessage(msg)
                bufferLength = inputStream.read(buffer)
            } else {
                file.delete()
                info.bytesRead = 0
                info.status = Status.CANCELED
                val msg = request.handler.obtainMessage(DownloadManager.IS_CANCELED, info)
                request.handler.sendMessage(msg)
                //emitter.onNext(Download.Failure())
                break
            }
        }

        if (info.bytesRead == info.contentLength && !status) {
            info.status = Status.DOWNLOAD_FINISHED
            val msg = request.handler.obtainMessage(DownloadManager.IS_FINISHED, info)
            request.handler.sendMessage(msg)
            //emitter.onNext(Download.Success())
        }

        //emitter.onComplete()
        inputStream.close()
        fileOutput.close()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DownloadTask) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}