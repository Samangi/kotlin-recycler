package ir.androidcourse.samangi.kotlinRecycler

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PictureContent {
    val ITEMS: MutableList<PictureItem> = ArrayList()
    @JvmStatic
    fun loadSavedImages(dir: File) {
        ITEMS.clear()
        if (dir.exists()) {
            val files = dir.listFiles()
            for (file in files) {
                val absolutePath = file.absolutePath
                val extension = absolutePath.substring(absolutePath.lastIndexOf("."))
                if (extension == ".jpg") {
                    loadImage(file)
                }
            }
        }
    }

    @JvmStatic
    fun deleteSavedImages(dir: File) {
        if (dir.exists()) {
            val files = dir.listFiles()
            for (file in files) {
                val absolutePath = file.absolutePath
                val extension = absolutePath.substring(absolutePath.lastIndexOf("."))
                if (extension == ".jpg") {
                    file.delete()
                }
            }
        }
        ITEMS.clear()
    }

    @JvmStatic
    fun downloadRandomImage(downloadmanager: DownloadManager, context: Context) {
        val ts = System.currentTimeMillis()
        val uri = Uri.parse(context.getString(R.string.image_download_url))
        val request = DownloadManager.Request(uri)
        request.setTitle("My File")
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setVisibleInDownloadsUi(false)
        val fileName = "$ts.jpg"
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
        downloadmanager.enqueue(request)
    }

    private fun getDateFromUri(uri: Uri?): String {
        val split = uri!!.path.split("/".toRegex()).toTypedArray()
        val fileName = split[split.size - 1]
        val fileNameNoExt = fileName.split("\\.".toRegex()).toTypedArray()[0]
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(Date(fileNameNoExt.toLong()))
    }

    @JvmStatic
    fun loadImage(file: File?) {
        val newItem = PictureItem()
        newItem.uri = Uri.fromFile(file)
        newItem.date = getDateFromUri(newItem.uri)
        addItem(newItem)
    }

    private fun addItem(item: PictureItem) {
        ITEMS.add(0, item)
    }
}