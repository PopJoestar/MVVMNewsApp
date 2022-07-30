package com.androiddevs.mvvmnewsapp.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.util.Constants
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment: BaseFragment(R.layout.fragment_search_news) {

    private lateinit var newsAdapter: NewsAdapter
    private val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "coucou")

        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            showArticleDetails(it)
        }

        displaySearchNewsResult()

        setAddTextChangedListener()

    }

    private fun setAddTextChangedListener() {
        var job: Job? = null

        etSearch.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_DELAY)
                viewModel.searchNews(it.toString())
            }
        }
    }

    private fun showArticleDetails(article: Article) {
        val action =
            SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(
                article.url
            )
        findNavController().navigate(action)

    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun displaySearchNewsResult() {
        viewModel.searchedNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()

                    response.data?.let {
                        newsAdapter.diffUtil.submitList(it.articles)
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

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }
}