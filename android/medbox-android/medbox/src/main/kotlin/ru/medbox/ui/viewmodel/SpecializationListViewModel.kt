package ru.medbox.ui.viewmodel

import io.reactivex.Observable
import ru.medbox.api.Api
import ru.medbox.db.Dao
import ru.medbox.db.model.Specialization
import ru.medbox.ui.BaseViewModel
import ru.medbox.ui.adapter.SpecializationListAdapter
import javax.inject.Inject

class SpecializationListViewModel : BaseViewModel() {

    @Inject lateinit var api: Api
    @Inject lateinit var dao: Dao

    val adapter = SpecializationListAdapter()

    init {
        subscribeOnRequest(Observable.fromCallable { dao.specializations }
                .concatMap {
                    if (it.isEmpty())
                        api.getSpecializations().concatMap { category ->
                            dao.insertSpecialization(*category.toTypedArray())
                            Observable.just(category)
                        }
                    else
                        Observable.just(it)
                })
    }

    override fun onRetrieveDataSuccess(data: Any) {
        adapter.updateList((data as List<*>).filterIsInstance(Specialization::class.java))
    }
}