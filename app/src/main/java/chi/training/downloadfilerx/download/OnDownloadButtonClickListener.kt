package chi.training.downloadfilerx.download

interface OnDownloadButtonClickListener {
    fun downloadClick(id: Int)
    fun cancelClick(id: Int)
}