package chi.training.downloadfilerx.download

import android.os.Handler
import android.os.Message
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object DownloadManager {
    private val tasks: MutableMap<UUID, FutureTask<DownloadTask>> = HashMap()
    private val listeners: MutableList<IStatus> = ArrayList()
    private val executorService = Executors.newFixedThreadPool(3)
    private val handler = DownloadHandler()

    const val IS_STARTED = 1
    const val IN_DOWNLOADING = 2
    const val IS_FINISHED = 3
    const val IS_FAILURE = 4
    const val IS_CANCELED = 5

    fun startDownload(task: DownloadTask) {
        executorService.execute(task)
        task.info.status = Status.IN_QUEUE
        task.status = false
        val msg = handler.obtainMessage(DownloadManager.IN_DOWNLOADING, task.info)
        handler.sendMessage(msg)
    }

    fun cancelDownload(task: DownloadTask) {
        val taskFuture = tasks[task.id]
        if (taskFuture != null) {
            taskFuture.cancel(true)
            task.status = true
            task.percent = 0
            if (!taskFuture.isDone) {
                taskFuture.get().path.delete()
            }
        }
    }

    fun addTask(request: DownloadRequest): DownloadTask {
        request.handler = handler
        val task = DownloadTask(request)
        tasks[task.id] = FutureTask(task, task)
        return task
    }

    fun getTask(uuid: UUID): FutureTask<DownloadTask>? {
        return tasks[uuid]
    }

    fun removeTask(task: DownloadTask) {
        tasks.remove(task.id)
    }

    fun addSubscriber(subscriber: IStatus) {
        if(!listeners.contains(subscriber)) {
            listeners.add(subscriber)
        }
    }

    fun removeSubscriber(subscriber: IStatus) {
        if(listeners.contains(subscriber)) {
            listeners.remove(subscriber)
        }
    }

    class DownloadHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                IS_STARTED -> {
                    notifyObservers(IS_STARTED, msg.obj as Info)
                }
                IN_DOWNLOADING -> {
                    notifyObservers(IN_DOWNLOADING, msg.obj as Info)
                }
                IS_FINISHED -> {
                    notifyObservers(IS_FINISHED, msg.obj as Info)
                }
                IS_FAILURE -> {
                    notifyObservers(IS_FAILURE, msg.obj)
                }
                IS_CANCELED -> {
                    notifyObservers(IS_CANCELED, msg.obj as Info)
                }
            }
        }
    }

    private fun notifyObservers(eventID: Int, fileInfo: Any) {
        for (observer in listeners) {
            when (eventID) {
                IS_STARTED -> {
                    observer.downloadStarted(true)
                }
                IN_DOWNLOADING -> {
                    observer.inDownloading(fileInfo as Info)
                }
                IS_FINISHED -> {
                    observer.downloadFinished(fileInfo as Info)
                }
                IS_FAILURE -> {
                    observer.downloadFailed(fileInfo)
                }
                IS_CANCELED -> {
                    observer.downloadCanceled(fileInfo as Info)
                }
            }
        }
    }

}