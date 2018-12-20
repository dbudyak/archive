package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import io.reactivex.Observable
import ru.medbox.api.Api
import ru.medbox.db.Dao
import ru.medbox.db.model.Doctor
import ru.medbox.db.model.Specialization
import ru.medbox.ui.BaseViewModel
import ru.medbox.utils.DOCTOR_KEY
import javax.inject.Inject

class DoctorDetailViewModel(params: HashMap<String, Any?>) : BaseViewModel() {

    @Inject lateinit var api: Api
    @Inject lateinit var dao: Dao

    val name = MutableLiveData<String>()
    val bio = MutableLiveData<String>()
    val rating = MutableLiveData<Int>()
    val photoUrl = MutableLiveData<String>()
    val specName = MutableLiveData<String>()

    private var doctorId: Int = params[DOCTOR_KEY].let { it as Int }

    init {
        subscribeOnRequest(Observable.fromCallable { dao.getDoctorById(doctorId) })
    }

    override fun onRetrieveDataSuccess(data: Any) {
        when (data) {
            is Doctor -> {
                name.value = data.name
                bio.value = data.bio
                rating.value = data.rating
                photoUrl.value = data.photoUrl
                subscribeOnRequest(Observable.fromCallable { dao.getSpecializationById(data.specializationId) })
            }
            is Specialization -> {
                specName.value = data.name
            }
        }
    }
}