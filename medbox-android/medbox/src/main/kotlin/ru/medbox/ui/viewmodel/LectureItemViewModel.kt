package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.medbox.db.model.Lecture

class LectureItemViewModel : ViewModel() {
    private val title = MutableLiveData<String>()
    private val videoUrl = MutableLiveData<String>()
    private val thumbUrl = MutableLiveData<String>()
    private val content = MutableLiveData<String>()

    fun bind(article: Lecture) {
        title.value = article.title
        videoUrl.value = article.videoUrl
        thumbUrl.value = article.thumbUrl
        content.value = article.content
    }

    fun getTitle(): MutableLiveData<String> = title
    fun getVideoUrl(): MutableLiveData<String> = videoUrl
    fun getThumbUrl(): MutableLiveData<String> = thumbUrl
    fun getContent(): MutableLiveData<String> = content
}