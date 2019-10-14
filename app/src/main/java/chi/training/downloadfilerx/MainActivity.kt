package chi.training.downloadfilerx

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import chi.training.downloadfilerx.adapter.FileAdapter
import chi.training.downloadfilerx.download.Download
import java.util.concurrent.Executors
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

    private var executorService: ExecutorService = Executors.newFixedThreadPool(3)
    private lateinit var adapter: FileAdapter
    private val filesURL: MutableList<Download.Progress> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }

        adapter = FileAdapter(executorService, filesDir)
        recyclerViewDownload.layoutManager = LinearLayoutManager(this)
        recyclerViewDownload.adapter = adapter

        for(i in 1..10){
            filesURL.add(Download.Progress(
                "https://download.microsoft.com/download/8/b/4/8b4addd8-e957-4dea-bdb8-c4e00af5b94b/NDP1.1sp1-KB867460-X86.exe")
            )
        }

        adapter.submitList(filesURL)
    }
}
