package chi.training.downloadfilerx.download

import androidx.annotation.IntDef


class Status {
    @IntDef(DOWNLOAD_START, DOWNLOAD_STOPPED, DOWNLOAD_FINISHED, IN_QUEUE, CANCELED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class DownloadStatus

    companion object {
        @DownloadStatus
        const val DOWNLOAD_START: Int = 1
        @DownloadStatus
        const val DOWNLOAD_STOPPED: Int = 2
        @DownloadStatus
        const val DOWNLOAD_FINISHED: Int = 3
        @DownloadStatus
        const val IN_QUEUE: Int = 4
        @DownloadStatus
        const val CANCELED: Int = 5
    }
}

