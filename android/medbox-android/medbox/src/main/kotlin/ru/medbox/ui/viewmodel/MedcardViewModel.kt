package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import ru.medbox.api.Api
import ru.medbox.db.model.Medcard
import ru.medbox.ui.BaseViewModel
import ru.medbox.utils.Prefs
import java.time.Instant
import javax.inject.Inject

class MedcardViewModel : BaseViewModel() {

    @Inject lateinit var api: Api
    @Inject lateinit var prefs: Prefs

    val data = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()

    init {
        data.observeForever {
            val medcard = Medcard(data = it!!, dateTime = Instant.now().toString(), emrId = 0, id = 0)
            subscribeOnRequest(api.updateMedcard(medcard))
        }
        email.value = prefs.userEmail
        firstName.value = prefs.userName

    }

    override fun onRetrieveDataSuccess(data: Any) {
        if (data is ResponseBody) {
            log(this, data.string())
        } else {
            log(this, "Unknown response type!")
            log(this, data.toString())
        }
    }
}