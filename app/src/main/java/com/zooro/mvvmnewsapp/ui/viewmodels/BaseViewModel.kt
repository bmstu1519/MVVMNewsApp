package com.zooro.mvvmnewsapp.ui.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.*

abstract class BaseViewModel<T>(app: Application, initState: T) : AndroidViewModel(app) {

    val state : MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    protected val currentState
        get() = state.value!!

    @UiThread
    protected inline fun updateState(update:(currentState:T) -> T){
        val updatedState : T = update(currentState)
        state.value = updatedState
    }

    fun observeState(owner: LifecycleOwner, onChanged: (newState : T) -> Unit){
        state.observe(owner, Observer{ onChanged(it!!) })
    }
}
