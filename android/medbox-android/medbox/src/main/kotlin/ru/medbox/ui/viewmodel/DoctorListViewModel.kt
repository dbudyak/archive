package ru.medbox.ui.viewmodel

import io.reactivex.Observable
import ru.medbox.api.Api
import ru.medbox.db.Dao
import ru.medbox.db.model.Doctor
import ru.medbox.ui.BaseViewModel
import ru.medbox.ui.adapter.DoctorListAdapter
import javax.inject.Inject

class DoctorListViewModel(params: HashMap<String, Any?>) : BaseViewModel() {

    @Inject lateinit var api: Api
    @Inject lateinit var dao: Dao

    val adapter = DoctorListAdapter()

//    private var specId: Int = params[SPECIALIZATION_KEY].let { it as Int }

    init {
        subscribeOnRequest(Observable.fromCallable { dao.doctors }
                .concatMap {
                    if (it.isEmpty())
                        api.getDoctors().concatMap { doctors ->
                            dao.insertDoctor(*doctors.toTypedArray())
                            Observable.just(doctors)
                        }
                    else
                        Observable.just(it)
                }
        )

    }

    override fun onRetrieveDataSuccess(data: Any) {
        adapter.updateList((data as List<*>).filterIsInstance(Doctor::class.java))
    }
}