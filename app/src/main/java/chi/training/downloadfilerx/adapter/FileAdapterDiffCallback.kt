package chi.training.downloadfilerx.adapter

import androidx.recyclerview.widget.DiffUtil
import chi.training.downloadfilerx.download.DownloadTask

class FileAdapterDiffCallback : DiffUtil.ItemCallback<DownloadTask>() {
    override fun areItemsTheSame(oldItem: DownloadTask, newItem: DownloadTask): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: DownloadTask,
        newItem: DownloadTask
    ): Boolean {
        return oldItem == newItem
    }
}