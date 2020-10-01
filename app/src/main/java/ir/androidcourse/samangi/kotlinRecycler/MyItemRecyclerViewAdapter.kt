package ir.androidcourse.samangi.kotlinRecycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ir.androidcourse.samangi.kotlinRecycler.ItemFragment.OnListFragmentInteractionListener

class MyItemRecyclerViewAdapter(private val mValues: List<PictureItem>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mImageView.setImageURI(mValues[position].uri)
        holder.mDateView.text = mValues[position].date
        holder.mView.setOnClickListener { mListener?.onListFragmentInteraction(holder.mItem) }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mImageView: ImageView
        val mDateView: TextView
        var mItem: PictureItem? = null

        init {
            mImageView = mView.findViewById(R.id.item_image_view)
            mDateView = mView.findViewById(R.id.item_date_tv)
        }
    }
}