package ir.androidcourse.samangi.kotlinRecycler

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import ir.androidcourse.samangi.kotlinRecycler.ItemFragment.OnListFragmentInteractionListener
import ir.androidcourse.samangi.kotlinRecycler.PictureContent.deleteSavedImages
import ir.androidcourse.samangi.kotlinRecycler.PictureContent.downloadRandomImage
import ir.androidcourse.samangi.kotlinRecycler.PictureContent.loadImage
import ir.androidcourse.samangi.kotlinRecycler.PictureContent.loadSavedImages
import java.io.File

class ScrollingActivity : AppCompatActivity(), OnListFragmentInteractionListener {
    private var context: ScrollingActivity? = null
    private var downloadManager: DownloadManager? = null
    private var recyclerViewAdapter: RecyclerView.Adapter<*>? = null
    private var recyclerView: RecyclerView? = null
    private var onComplete: BroadcastReceiver? = null
    private var progressBar: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        context = this
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        if (recyclerViewAdapter == null) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment)
            recyclerView = currentFragment!!.view as RecyclerView?
            recyclerViewAdapter = (currentFragment.view as RecyclerView?)!!.adapter
            val dividerItemDecoration = DividerItemDecoration(recyclerView!!.context,
                    DividerItemDecoration.VERTICAL)
            recyclerView!!.addItemDecoration(dividerItemDecoration)
        }
        progressBar = findViewById(R.id.indeterminateBar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            runOnUiThread {
                progressBar!!.setVisibility(View.VISIBLE)
                fab.visibility = View.GONE
                downloadRandomImage(downloadManager!!, context!!)
            }
        }
        onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                var filePath: String? = ""
                val q = DownloadManager.Query()
                q.setFilterById(intent.extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID))
                val c = downloadManager!!.query(q)
                if (c.moveToFirst()) {
                    val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val downloadFileLocalUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        filePath = Uri.parse(downloadFileLocalUri).path
                    }
                }
                c.close()
                loadImage(File(filePath))
                recyclerViewAdapter!!.notifyItemInserted(0)
                progressBar!!.setVisibility(View.GONE)
                fab.visibility = View.VISIBLE
            }
        }
        context!!.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_delete) {
            deleteSavedImages(context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS))
            recyclerViewAdapter!!.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        unregisterReceiver(onComplete)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        runOnUiThread {
            loadSavedImages(context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS))
            recyclerViewAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onListFragmentInteraction(item: PictureItem?) {
        // This is where you'd handle clicking an item in the list
    }
}