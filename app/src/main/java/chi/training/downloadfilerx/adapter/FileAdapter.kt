package chi.training.downloadfilerx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import chi.training.downloadfilerx.R
import chi.training.downloadfilerx.download.Download
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.ExecutorService

class FileAdapter(private val executorService: ExecutorService, private val filesDir: File)
    : ListAdapter<Download.Progress, FileAdapter.FileItemViewHolder>(FileAdapterDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recycler_item, parent, false)
        return FileItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        val data = getItem(position)
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
    }

    class FileItemViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var fileProgress: ProgressBar = itemView.findViewById(R.id.progressBarDownload)
        var fileButton: Button = itemView.findViewById(R.id.buttonDownload)
    }
}