package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article

class NewsRepository(val db: ArticleDatabase) {

    suspend fun getBreakingNews (countryCode:String, page:Int) = RetrofitInstance.api.getBreakingNews(countryCode, page)
    suspend fun searchNews (searchQuery: String, page:Int) = RetrofitInstance.api.searchNews(searchQuery, page)

    fun getAllArticles() = db.getArticleDao().selectAllArticles()

    suspend fun upsertArticle(article: Article) = db.getArticleDao().upsertArticle(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}