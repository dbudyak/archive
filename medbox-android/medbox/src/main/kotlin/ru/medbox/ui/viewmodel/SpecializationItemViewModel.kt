package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.medbox.db.model.Specialization

class SpecializationItemViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()

    fun bind(specialization: Specialization) {
        name.value = specialization.name
        description.value = specialization.description
        imageUrl.value = specialization.imageUrl
    }
}