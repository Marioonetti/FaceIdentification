package com.marioonetti.pruebatecnicareact.ui.di

import com.marioonetti.pruebatecnicareact.ui.screen.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { MainViewModel(get()) }
}