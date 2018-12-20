package ru.medbox.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.medbox.db.model.Doctor

class DoctorItemViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val bio = MutableLiveData<String>()
    val rating = MutableLiveData<Int>()
    val photoUrl = MutableLiveData<String>()

    fun bind(doctor: Doctor) {
        name.value = doctor.name
        bio.value = doctor.bio
        rating.value = doctor.rating
        photoUrl.value = doctor.photoUrl
    }
}