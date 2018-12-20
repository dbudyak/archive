package ru.medbox.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.medbox.di.component.DaggerViewModelInjector
import ru.medbox.di.component.ViewModelInjector
import ru.medbox.di.module.NetworkModule
import ru.medbox.di.module.PersistenceModule
import ru.medbox.ui.viewmodel.*
import ru.medbox.utils.Loggable

abstract class BaseViewModel : ViewModel(), Loggable {

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorClickListener = View.OnClickListener { }
    protected var subscription: Disposable? = null

    private val injector: ViewModelInjector = DaggerViewModelInjector
            .builder()
            .networkModule(NetworkModule)
            .persistenceModule(PersistenceModule)
            .build()

    init {
        inject()
    }

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
    }

    private fun inject() {
        when (this) {
            is CategoryListViewModel -> injector.inject(this)
            is ArticleListViewModel -> injector.inject(this)
            is ArticleDetailViewModel -> injector.inject(this)
            is LectureListViewModel -> injector.inject(this)
            is LectureDetailViewModel -> injector.inject(this)
            is SpecializationListViewModel -> injector.inject(this)
            is DoctorListViewModel -> injector.inject(this)
            is DoctorDetailViewModel -> injector.inject(this)
            is RegisterViewModel -> injector.inject(this)
            is MedcardViewModel -> injector.inject(this)
        }
    }

    fun <T : Any> subscribeOnRequest(observable: Observable<T>) {
        subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onRetrieveDataStart() }
                .doOnEach { each -> each.error?.let { log(this, " ERROR " + it.message) } }
                .doOnEach { each -> each.value?.let { log(this, " VERBOSE " + it.toString()) } }
                .doOnTerminate { onRetrieveDataFinish() }
                .subscribe(
                        { onRetrieveDataSuccess(it) },
                        { onRetrieveError(it.message) }
                )
    }

    protected fun onRetrieveDataStart() {
        loadingVisibility.value = View.VISIBLE
        errorMessage.value = null
    }

    protected fun onRetrieveDataFinish() {
        loadingVisibility.value = View.GONE
    }

    protected fun onRetrieveError(message: String?) {
        errorMessage.value = message
    }

    protected abstract fun onRetrieveDataSuccess(data: Any)

}