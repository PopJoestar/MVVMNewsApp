package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.list_article.view.*

class BreakingNewsFragment: BaseFragment(R.layout.fragment_breaking_news) {

    lateinit var newsAdapter: NewsAdapter
    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

       newsAdapter.setOnItemClickListener {
           showArticleDetails(it)
       }

        displayBreakingNews()
    }


    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        listBreakingNews.rvArticles.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }

    private fun showArticleDetails(article: Article) {
        val action =
            BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(
                article.url
            )
            findNavController().navigate(action)

    }

    private fun showProgressBar () {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar () {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun displayBreakingNews () {
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()

                    response.data?.let {
                        newsAdapter.diffUtil.submitList(it.articles)
                        if (it.articles.isEmpty()) {
                            listBreakingNews.txtEmpty.text = "No items"
                            listBreakingNews.txtEmpty.visibility = View.VISIBLE
                            listBreakingNews.rvArticles.visibility = View.GONE
                        } else {
                            listBreakingNews.txtEmpty.visibility = View.GONE
                            listBreakingNews.rvArticles.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Log.e(TAG, it)
                    }
                }
            }

        })

    }
}