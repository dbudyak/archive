package ru.medbox.ui.viewmodel

import io.reactivex.Observable
import ru.medbox.api.Api
import ru.medbox.db.Dao
import ru.medbox.db.model.Article
import ru.medbox.ui.BaseViewModel
import ru.medbox.ui.adapter.ArticleListAdapter
import ru.medbox.utils.CATEGORY_KEY
import javax.inject.Inject

class ArticleListViewModel(params: HashMap<String, Any?>) : BaseViewModel() {

    @Inject lateinit var api: Api
    @Inject lateinit var dao: Dao

    val adapter: ArticleListAdapter = ArticleListAdapter()

    private var categoryId: Int = params[CATEGORY_KEY] as Int

    init {
        loadData()
    }

    private fun loadData() {
        subscribeOnRequest(Observable.fromCallable { dao.getArticlesById(categoryId) }
                .concatMap { articles ->
                    if (articles.isEmpty())
                        api.getArticles()
                                .concatMap {
                                    dao.insertArticle(*it.toTypedArray())
                                    Observable.just(it)
                                }
                    else Observable.just(articles)
                })
    }

    override fun onRetrieveDataSuccess(data: Any) {
        adapter.updateArticleList((data as List<*>).filterIsInstance(Article::class.java).filter { it.categoryId == categoryId })
    }

}