package chi.training.downloadfilerx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import chi.training.downloadfilerx.R
import chi.training.downloadfilerx.download.DownloadTask
import chi.training.downloadfilerx.download.OnDownloadButtonClickListener
import chi.training.downloadfilerx.download.Status
import java.util.*

class FileAdapter(private val onGlobalClickListener: OnDownloadButtonClickListener, private val data: MutableList<DownloadTask>)
    : ListAdapter<DownloadTask, FileAdapter.FileItemViewHolder>(FileAdapterDiffCallback()){

    fun notifyOnyItem(id: UUID) {
        for (i in data.indices) {
            if (data[i].id === id) {
                notifyItemChanged(i)
                return
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recycler_item, parent, false)
        return FileItemViewHolder(view, onGlobalClickListener)
    }

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bindTo(holder, data)
        }
    }

    class FileItemViewHolder(
        itemView: View,
        private val onLocalClickListener: OnDownloadButtonClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var fileProgress: ProgressBar = itemView.findViewById(R.id.progressBarDownload)
        var fileButton: Button = itemView.findViewById(R.id.buttonDownload)

        init {
            fileButton.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            if (fileButton.text == "Click to start" || fileButton.text == "CANCELED") {
                onLocalClickListener.downloadClick(adapterPosition)
            } else {
                onLocalClickListener.cancelClick(adapterPosition)
            }
        }

        fun bindTo(holder: FileItemViewHolder ,data: DownloadTask) {
            holder.fileProgress.progress = data.percent
            when(data.info.status) {
                Status.IN_QUEUE -> holder.fileButton.text = "In queue"
                Status.DOWNLOAD_START -> holder.fileButton.text = "In progress"
                Status.DOWNLOAD_STOPPED -> holder.fileButton.text = "Failure"
                Status.CANCELED -> holder.fileButton.text = "CANCELED"
                Status.DOWNLOAD_FINISHED -> holder.fileButton.text = "Done"
                else -> {
                    holder.fileButton.text = "Click to start"
                }
            }
        }
    }


}

/*
 holder.fileButton.setOnClickListener {
            if (!data.isDownload) {
                data.isDownload = true
                holder.fileButton.text = "In queue"
                data.downloadFile(filesDir)
                    .subscribeOn(Schedulers.from(executorService))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (it) {
                            is Download.Progress -> {
                                holder.fileProgress.progress = it.getPercent()
                            }
                            is Download.Failure -> {
                                holder.fileProgress.progress = 0
                                holder.fileButton.text = "Download"
                                data.isCanceled = false
                                data.isDownload = false
                            }
                            is Download.Success -> {
                                holder.fileButton.text = "Download"
                                data.isDownload = false
                            }
                        }
                    }
            } else {
                data.isCanceled = true
            }
        }
 */