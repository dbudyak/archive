package ru.medbox.di.component

import dagger.Component
import ru.medbox.di.module.NetworkModule
import ru.medbox.di.module.PersistenceModule
import ru.medbox.ui.viewmodel.*
import javax.inject.Singleton

@Singleton
@Component(modules = [(NetworkModule::class), (PersistenceModule::class)])
interface ViewModelInjector {

    fun inject(vm: CategoryListViewModel)
    fun inject(vm: ArticleListViewModel)
    fun inject(vm: ArticleDetailViewModel)
    fun inject(vm: LectureListViewModel)
    fun inject(vm: LectureDetailViewModel)
    fun inject(vm: SpecializationListViewModel)
    fun inject(vm: DoctorListViewModel)
    fun inject(vm: DoctorDetailViewModel)
    fun inject(vm: RegisterViewModel)
    fun inject(vm: MedcardViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector
        fun networkModule(networkModule: NetworkModule): Builder
        fun persistenceModule(persistenceModule: PersistenceModule): Builder
    }
}