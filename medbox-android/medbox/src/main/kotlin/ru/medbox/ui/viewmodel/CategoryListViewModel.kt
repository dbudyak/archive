package ru.medbox.ui.viewmodel

import io.reactivex.Observable
import ru.medbox.api.Api
import ru.medbox.db.Dao
import ru.medbox.db.model.Category
import ru.medbox.ui.BaseViewModel
import ru.medbox.ui.adapter.CategoryListAdapter
import javax.inject.Inject

class CategoryListViewModel : BaseViewModel() {

    @Inject lateinit var api: Api
    @Inject lateinit var dao: Dao

    val adapter: CategoryListAdapter = CategoryListAdapter()

    init {
        subscribeOnRequest(Observable.fromCallable { dao.categories }
                .concatMap {
                    if (it.isEmpty())
                        api.getCategories().concatMap { category ->
                            dao.insertCategory(*category.toTypedArray())
                            Observable.just(category)
                        }
                    else
                        Observable.just(it)
                })

    }

    override fun onRetrieveDataSuccess(data: Any) {
        adapter.updateArticleList((data as List<*>).filterIsInstance(Category::class.java))
    }
}