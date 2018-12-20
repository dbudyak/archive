package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import io.reactivex.Observable
import ru.medbox.api.Api
import ru.medbox.db.Dao
import ru.medbox.db.model.Lecture
import ru.medbox.ui.BaseViewModel
import ru.medbox.utils.LECTURE_KEY
import javax.inject.Inject

class LectureDetailViewModel(params: HashMap<String, Any?>) : BaseViewModel() {

    @Inject lateinit var api: Api
    @Inject lateinit var dao: Dao

    val title = MutableLiveData<String>()
    val content = MutableLiveData<String>()
    val thumbUrl = MutableLiveData<String>()
    val videoUrl = MutableLiveData<String>()

    private var lectureId: Int = params[LECTURE_KEY].let { it as Int }

    init {
        subscribeOnRequest(Observable.fromCallable { dao.getLectureById(lectureId) })
    }

    override fun onRetrieveDataSuccess(data: Any) {
        bind(data as Lecture)
    }

    fun bind(lecture: Lecture) {
        title.value = lecture.title
        thumbUrl.value = lecture.thumbUrl
        content.value = lecture.content
        videoUrl.value = lecture.videoUrl
    }
}