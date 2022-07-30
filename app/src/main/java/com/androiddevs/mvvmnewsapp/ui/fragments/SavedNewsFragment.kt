package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.models.Article
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.list_article.view.*

class SavedNewsFragment: BaseFragment(R.layout.fragment_saved_news) {


    lateinit var newsAdapter: NewsAdapter
    val TAG = "SavedNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            showArticleDetails(it)
        }

        setupItemTouchHelper(view)

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
            newsAdapter.diffUtil.submitList(it)

            if (it.isEmpty()) {
                listNews.txtEmpty.text = "No items"
                listNews.txtEmpty.visibility = View.VISIBLE
                listNews.rvArticles.visibility = View.GONE
            } else {
                listNews.txtEmpty.visibility = View.GONE
                listNews.rvArticles.visibility = View.VISIBLE
            }
        })



    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        listNews.rvArticles.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showArticleDetails(article: Article) {
        val action =
            SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(
                article.url
            )
        findNavController().navigate(action)

    }

    private fun setupItemTouchHelper(view: View) {
        val itemTouchHelperCallback = object :ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
               val position = viewHolder.adapterPosition
                val article = newsAdapter.diffUtil.currentList[position]

                viewModel.deleteArticle(article)
                Snackbar.make(view, "Delete ${article.title}", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listNews.rvArticles)
    }
}