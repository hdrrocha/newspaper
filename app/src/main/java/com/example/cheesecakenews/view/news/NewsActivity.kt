package com.example.cheesecakenews.view.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.cheesecakenews.R
import com.example.cheesecakenews.data.NewsDBHelper
import com.example.cheesecakenews.model.News
import com.example.cheesecakenews.view.NewsNotice.NewsNoticeActivity
import com.example.cheesecakenews.view.news.adapter.NewsAdapter
import com.example.cheesecakenews.view_model.NewsViewModel
import com.example.cheesecakenews.view_model.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.util.*
import javax.inject.Inject
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.Gravity
import android.transition.Slide
import android.os.Build
import android.app.ActivityOptions
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast


class NewsActivity : AppCompatActivity() {
    @Inject
    lateinit var newsVMFactory: ViewModelFactory<NewsViewModel>

    private val newsViewModel by lazy {
        ViewModelProviders.of(this, newsVMFactory)[NewsViewModel::class.java]
    }

    protected val ItemsObserver = Observer<List<News>>(::onItemsFetched)

    private lateinit var adapter: NewsAdapter
    var layoutManager = LinearLayoutManager(this)
    var listNewsView: MutableList<News> = mutableListOf()

    lateinit var newsDBHelper : NewsDBHelper

    override fun onResume() {
        super.onResume()
        listNewsView = newsDBHelper.readSortItem()

        updateAdapter(listNewsView)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        newsDBHelper = NewsDBHelper(this)
        listNewsView = newsDBHelper.readAllNews()
        if (listNewsView.isEmpty()) {
            newsViewModel.data.observe(this, ItemsObserver)
            newsViewModel.fetchNews()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {
            true
        }
        R.id.action_author -> {
            updateAdapter(sorListAuthor(listNewsView))
            true
        }
        R.id.action_title -> {
            updateAdapter(sorListTitle(listNewsView))
            true
        }
        R.id.action_date -> {
            updateAdapter(sorListDate(listNewsView))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun onItemsFetched(list: List<News>?) {
        if (list != null) {
            list.forEach {
                addNews(it)
            }
            listNewsView = newsDBHelper.readSortItem()
            updateAdapter(listNewsView)
        }
    }

    fun addNews(news: News){
        news.is_read = "n"
        newsDBHelper.insertNews(news)
    }

    private fun updateAdapter(list: List<News>) {
        recyclerViewNews.layoutManager = layoutManager
        recyclerViewNews.setHasFixedSize(true)
        adapter = NewsAdapter({ news: News -> partItemClicked(news) } )
        adapter.update(list)
        recyclerViewNews.adapter = adapter
        progressBar.visibility = View.GONE
    }

    private fun partItemClicked(news: News) {
        news.is_read = "r"
        val intent = Intent(this.baseContext, NewsNoticeActivity::class.java)
        intent.putExtra("notice", news as Serializable)
        newsDBHelper.readItem(news)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_up,  R.anim.no_animation);
    }


    private fun sorListAuthor(originalItems: MutableList<News>): List<News> {
        return originalItems.sortedWith(compareBy(News::authors))
    }

    private fun sorListTitle(originalItems: MutableList<News>): List<News> {
        return originalItems.sortedWith(compareBy(News::title))
    }

    private fun sorListDate(originalItems: MutableList<News>): List<News> {
        return originalItems.sortedWith(compareBy(News::date))
    }

}
