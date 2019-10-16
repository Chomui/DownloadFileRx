package chi.training.downloadfilerx.download

import android.os.Handler
import java.io.File

class DownloadRequest(
    var fileName: String,
    var url: String,
    var path: File
) {
    lateinit var handler: Handler
}