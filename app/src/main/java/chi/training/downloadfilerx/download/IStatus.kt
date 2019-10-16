package chi.training.downloadfilerx.download

interface IStatus {
    fun downloadStarted(isStarted: Boolean)
    fun inDownloading(fileInfo: Info)
    fun downloadCanceled(fileInfo: Info)
    fun downloadFinished(fileInfo: Info)
    fun downloadFailed(exception: Any)
}