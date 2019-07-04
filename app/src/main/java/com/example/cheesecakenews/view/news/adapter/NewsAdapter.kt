package com.example.cheesecakenews.view.news.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.cheesecakenews.R
import com.example.cheesecakenews.model.News
import kotlinx.android.synthetic.main.news_item.view.*

class NewsAdapter(val clickListener: ((News) -> Unit)?) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    var newsItems: List<News>
    init {
        newsItems = listOf()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(newsItem: News, clickListener: ((News) -> Unit)?) {
            itemView.textViewTitle.text = newsItem.title
            if(newsItem.is_read.equals("r"))
               itemView.imageViewIsRead.setImageResource(R.drawable.ic_drafts)
            itemView.textViewAuthors.text = newsItem.authors
            itemView.textViewDate.text = newsItem.date
            itemView.setOnClickListener {
                if (clickListener != null) {
                    clickListener(newsItem)
                }
            }
            Glide.with(itemView)
                .load("${newsItem?.image_url}")
                .into(itemView.imageViewThumb)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = newsItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        (holder as ViewHolder).bind(newsItems[position], clickListener)
    }

    fun updateList() {
        notifyDataSetChanged()
    }

    fun update(newsItems: List<News>) {
        this.newsItems = emptyList()
        this.newsItems = newsItems
        updateList()
    }
}
