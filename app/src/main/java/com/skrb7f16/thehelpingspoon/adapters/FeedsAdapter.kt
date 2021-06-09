package com.skrb7f16.thehelpingspoon.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skrb7f16.thehelpingspoon.R
import com.skrb7f16.thehelpingspoon.ui.feeds.FeedsModel
import com.squareup.picasso.Picasso
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class FeedsAdapter(private val data: MutableList<FeedsModel>) : RecyclerView.Adapter<FeedsAdapter.FeedsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedsViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.feed, parent, false)
        return FeedsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedsViewHolder, position: Int) {
        holder.title.text = data[position].title
        holder.phone.text = data[position].phone
        holder.by.text = data[position].by
        holder.desc.text = data[position].desc
        holder.at.text = data[position].at
        holder.city.text = data[position].city
        holder.address.text = data[position].address
        val uri: Uri = Uri.parse("https://res.cloudinary.com/skrb7f16/" + data[position].pic)
        Picasso.get().load(uri).into(holder.pic)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.pic.clipToOutline=true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }





    class FeedsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var address: TextView = view.findViewById(R.id.feed_address);
        var title: TextView=view.findViewById(R.id.feed_title);
        var phone: TextView=view.findViewById(R.id.feed_phone);
        var pic: ImageView = view.findViewById(R.id.feed_pic);
        var at: TextView=view.findViewById(R.id.feed_time);
        var by: TextView=view.findViewById(R.id.feed_by);
        var city: TextView=view.findViewById(R.id.feed_city);
        var desc: TextView=view.findViewById(R.id.feed_desc);

    }


}