package chi.training.downloadfilerx

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import chi.training.downloadfilerx.adapter.FileAdapter
import chi.training.downloadfilerx.download.*
import java.util.concurrent.Executors
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity(), OnDownloadButtonClickListener, IStatus {

    private lateinit var adapter: FileAdapter
    private val tasks: MutableList<DownloadTask> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FileAdapter(this, tasks)
        recyclerViewDownload.layoutManager = LinearLayoutManager(this)
        recyclerViewDownload.adapter = adapter

        for (i in 1..10) {
            val request = DownloadRequest(
                "file$i",
                "https://download.microsoft.com/download/8/b/4/8b4addd8-e957-4dea-bdb8-c4e00af5b94b/NDP1.1sp1-KB867460-X86.exe",
                filesDir
            )
            val task = DownloadManager.addTask(request)
            tasks.add(task)
        }

        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        DownloadManager.addSubscriber(this)
    }

    override fun onStop() {
        super.onStop()
        DownloadManager.removeSubscriber(this)
    }

    override fun downloadClick(id: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        } else {
            DownloadManager.startDownload(tasks[id])
        }
    }

    override fun cancelClick(id: Int) {
        DownloadManager.cancelDownload(tasks[id])
    }

    override fun downloadStarted(isStarted: Boolean) {

    }

    override fun inDownloading(fileInfo: Info) {
        adapter.notifyOnyItem(fileInfo.id)
    }

    override fun downloadCanceled(fileInfo: Info) {
        adapter.notifyOnyItem(fileInfo.id)
    }

    override fun downloadFinished(fileInfo: Info) {
        adapter.notifyOnyItem(fileInfo.id)
    }

    override fun downloadFailed(exception: Any) {
    }
}
