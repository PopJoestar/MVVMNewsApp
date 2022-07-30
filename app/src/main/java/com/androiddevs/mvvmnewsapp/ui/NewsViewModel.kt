package com.androiddevs.mvvmnewsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {
    private var articles = mutableSetOf<Article>()

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchedNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchedNewsResponse: NewsResponse? = null


    init {
        getBreakingNews("us")
    }

    fun getBreakingNews (countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleGetBreakingNewsResponse(response))
    }

    fun searchNews (searchQuery: String) = viewModelScope.launch {
        searchedNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchedNews.postValue(handleSearchNewsResponse(response))
    }

    fun findArticleByUrl (url: String): Article ? {
        return articles.find { article -> article.url == url }
    }

    fun getSavedNews () = newsRepository.getAllArticles()

    fun saveArticle (article: Article) = viewModelScope.launch {
        newsRepository.upsertArticle(article)
    }

    fun deleteArticle (article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private fun handleGetBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                breakingNewsPage ++
                articles.addAll(it.articles)

                if (breakingNewsResponse == null) {
                    breakingNewsResponse = it
                } else {
                    breakingNewsResponse?.articles?.addAll(it.articles)
                }

                return Resource.Success(breakingNewsResponse ?: it)

            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPage ++
                articles.addAll(it.articles)

                if (searchedNewsResponse == null) {
                    searchedNewsResponse = it
                } else {
                    searchedNewsResponse?.articles?.addAll(it.articles)
                }

                return Resource.Success(searchedNewsResponse ?: it)

            }
        }
        return Resource.Error(response.message())
    }


}