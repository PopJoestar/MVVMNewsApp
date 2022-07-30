package com.androiddevs.mvvmnewsapp.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*


class ArticleFragment: BaseFragment(R.layout.fragment_article) {

    private val args: ArticleFragmentArgs by navArgs()
    var article: Article? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        article = viewModel.findArticleByUrl(args.url)
        showArticle()

        fab.setOnClickListener {
            article?.let {
                viewModel.saveArticle(it)
                Snackbar.make(view, "${it.title} saved", Snackbar.LENGTH_LONG).show()
            }

        }
    }

    fun showArticle() {
        webView.apply {
            webViewClient = object: WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                    tvProgress.visibility = View.GONE
                    super.onPageFinished(view, url)
                }

            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    tvProgress.text = "$newProgress %"
                    super.onProgressChanged(view, newProgress)
                }

            }
            article?.let { loadUrl(it.url) }
        }
    }



}