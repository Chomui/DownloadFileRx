package chi.training.downloadfilerx.adapter

import androidx.recyclerview.widget.DiffUtil
import chi.training.downloadfilerx.download.Download

class FileAdapterDiffCallback : DiffUtil.ItemCallback<Download.Progress>() {
    override fun areItemsTheSame(oldItem: Download.Progress, newItem: Download.Progress): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: Download.Progress,
        newItem: Download.Progress
    ): Boolean {
        return oldItem == newItem
    }
}