package ru.medbox.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import ru.medbox.ui.viewmodel.*

@Suppress("UNCHECKED_CAST")
class ViewModelFactory() : ViewModelProvider.Factory {

    private lateinit var params: HashMap<String, Any?>

    constructor(params: HashMap<String, Any?>) : this() {
        this.params = params
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!::params.isInitialized) params = hashMapOf()
        return when {
            modelClass.isAssignableFrom(CategoryListViewModel::class.java) -> CategoryListViewModel() as T
            modelClass.isAssignableFrom(ArticleListViewModel::class.java) -> ArticleListViewModel(params) as T
            modelClass.isAssignableFrom(ArticleDetailViewModel::class.java) -> ArticleDetailViewModel(params) as T
            modelClass.isAssignableFrom(LectureDetailViewModel::class.java) -> LectureDetailViewModel(params) as T
            modelClass.isAssignableFrom(LectureListViewModel::class.java) -> LectureListViewModel() as T
            modelClass.isAssignableFrom(SpecializationListViewModel::class.java) -> SpecializationListViewModel() as T
            modelClass.isAssignableFrom(DoctorListViewModel::class.java) -> DoctorListViewModel(params) as T
            modelClass.isAssignableFrom(DoctorDetailViewModel::class.java) -> DoctorDetailViewModel(params) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel() as T
            modelClass.isAssignableFrom(MedcardViewModel::class.java) -> MedcardViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}