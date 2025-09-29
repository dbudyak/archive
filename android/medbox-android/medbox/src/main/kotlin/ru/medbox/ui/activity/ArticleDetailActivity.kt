package ru.medbox.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import ru.medbox.R
import ru.medbox.databinding.ActivityArticleBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.viewmodel.ArticleDetailViewModel
import ru.medbox.utils.ARTICLE_KEY


class ArticleDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityArticleBinding
    private lateinit var viewModel: ArticleDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val articleId = intent.extras?.getInt(ARTICLE_KEY)
        val params: HashMap<String, Any?> = hashMapOf(ARTICLE_KEY to articleId)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(params)).get(ArticleDetailViewModel::class.java)
        viewModel.errorMessage.observe(this, Observer { errorMsg -> errorMsg?.let { showError(binding.root, viewModel.errorClickListener, it) } })

        binding = DataBindingUtil.setContentView(this, R.layout.activity_article)
        binding.viewModel = viewModel

    }

}
