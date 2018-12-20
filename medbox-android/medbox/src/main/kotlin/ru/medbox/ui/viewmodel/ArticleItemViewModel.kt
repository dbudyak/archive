package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.medbox.db.model.Article

class ArticleItemViewModel : ViewModel() {
    private val title = MutableLiveData<String>()
    private val description = MutableLiveData<String>()
    private val content = MutableLiveData<String>()
    private val imageUrl = MutableLiveData<String>()

    fun bind(article: Article) {
        title.value = article.title
        description.value = article.description
        content.value = article.content
        imageUrl.value = article.imageUrl
    }

    fun getTitle(): MutableLiveData<String> = title
    fun getDescription(): MutableLiveData<String> = description
    fun getContent(): MutableLiveData<String> = content
    fun getImageUrl(): MutableLiveData<String> = imageUrl
}