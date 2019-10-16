package chi.training.downloadfilerx.download

import java.util.*

class Info(var id: UUID) {
    var status: Int = 0
    var bytesRead: Int = 0
    var contentLength: Int = 0
}