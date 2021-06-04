package com.ae.app1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_item.view.*


class ItemAdapter(
    private val context: Context,
    options: FirestoreRecyclerOptions<Item>
) : FirestoreRecyclerAdapter<Item, ItemAdapter.ItemViewHolder>(options) {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater
                .from(parent.context).inflate(R.layout.layout_item, parent, false)

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Item) {
        holder.tvTitle.text = model.name
        holder.tvDescription.text = model.postal

        if (model.url.isEmpty()) {
            Picasso.with(context).load(R.drawable.ic_android)
                .placeholder(R.drawable.ic_android)
                .fit()
                .centerInside()
                .into(holder.ivAvatar)
        } else {
            Picasso.with(context).load(model.url)
                .placeholder(R.drawable.ic_android)
                .fit()
                .centerInside()
                .into(holder.ivAvatar)
        }
    }

    fun deleteItem(position: Int) {
        snapshots.getSnapshot(position).reference.delete()
    }

    fun getItemIdAt(position: Int): String {
        return snapshots.getSnapshot(position).reference.id
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    /**
     * If you want to send anything from Adapter to the Activity, you have to change the arguments
     * of the interface method. Send data from Adapter to underlying Activity
     */
    interface OnItemClickListener {
        // Send the document to the Activity.
        fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvTitle: TextView = itemView.tvName
        val tvDescription: TextView = itemView.tvPostal
        val ivAvatar: ImageView = itemView.ivAvatar

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener!!.onItemClick(snapshots.getSnapshot(position), position)
            }
        }
    }
}


