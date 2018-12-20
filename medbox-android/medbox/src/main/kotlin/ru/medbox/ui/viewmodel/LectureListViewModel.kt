package ru.medbox.ui.viewmodel

import io.reactivex.Observable
import ru.medbox.api.Api
import ru.medbox.db.Dao


import ru.medbox.db.model.Lecture
import ru.medbox.ui.BaseViewModel
import ru.medbox.ui.adapter.LectureListAdapter
import javax.inject.Inject

class LectureListViewModel : BaseViewModel() {


    @Inject lateinit var api: Api
    @Inject lateinit var dao: Dao

    val adapter: LectureListAdapter = LectureListAdapter()

    init {
        subscribeOnRequest(Observable.fromCallable { dao.lectures }
                .concatMap {
                    if (it.isEmpty())
                        api.getLectures().concatMap { lectures ->
                            dao.insertLecture(*lectures.toTypedArray())
                            Observable.just(lectures)
                        }
                    else
                        Observable.just(it)
                }
        )
    }

    override fun onRetrieveDataSuccess(data: Any) {
        adapter.updateLectureList((data as List<*>).filterIsInstance(Lecture::class.java))
    }
}