package ru.medbox.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import ru.medbox.R
import ru.medbox.databinding.ActivityArticlesBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.viewmodel.ArticleListViewModel
import ru.medbox.utils.CATEGORY_KEY

class ArticlesActivity : BaseActivity() {
    private lateinit var binding: ActivityArticlesBinding
    private lateinit var viewModel: ArticleListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryId = intent.extras?.getInt(CATEGORY_KEY)
        val params: HashMap<String, Any?> = hashMapOf(CATEGORY_KEY to categoryId)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(params)).get(ArticleListViewModel::class.java)
        viewModel.errorMessage.observe(this, Observer {
            if (it != null) showError(binding.root, viewModel.errorClickListener, it) else hideError()
        })

        binding = DataBindingUtil.setContentView(this, R.layout.activity_articles)
        binding.list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewModel = viewModel
    }
}

