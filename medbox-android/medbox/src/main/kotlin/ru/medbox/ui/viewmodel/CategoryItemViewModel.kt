package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.medbox.db.model.Category

class CategoryItemViewModel : ViewModel() {
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    fun bind(categoryArticle: Category) {
        title.value = categoryArticle.title
        description.value = categoryArticle.description
    }
}