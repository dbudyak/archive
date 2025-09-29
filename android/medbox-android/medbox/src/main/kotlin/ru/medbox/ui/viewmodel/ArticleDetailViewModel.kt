package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import io.reactivex.Observable
import ru.medbox.api.Api
import ru.medbox.db.Dao
import ru.medbox.db.model.Article
import ru.medbox.ui.BaseViewModel
import ru.medbox.utils.ARTICLE_KEY
import javax.inject.Inject

class ArticleDetailViewModel(params: HashMap<String, Any?>) : BaseViewModel() {

    @Inject lateinit var api: Api
    @Inject lateinit var dao: Dao

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val content = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()

    private var articleId: Int = params[ARTICLE_KEY].let { it as Int }

    init {
        subscribeOnRequest(Observable.fromCallable { dao.getArticleById(articleId) })
    }

    override fun onRetrieveDataSuccess(data: Any) {
        val article = data as Article
        Log.d("BIND", article.toString())
        title.value = article.title
        description.value = article.description
        content.value = article.content
        imageUrl.value = article.imageUrl
    }
}