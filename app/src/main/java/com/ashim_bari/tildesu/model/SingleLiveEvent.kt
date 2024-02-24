package com.ashim_bari.tildesu.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pendingObservers = mutableListOf<Observer<in T>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer<T> { value ->
            if (pendingObservers.contains(observer)) {
                observer.onChanged(value)
                pendingObservers.remove(observer)
            }
        })
        if (!pendingObservers.contains(observer)) {
            pendingObservers.add(observer)
        }
    }

    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(Observer<T> { value ->
            if (pendingObservers.contains(observer)) {
                observer.onChanged(value)
                pendingObservers.remove(observer)
            }
        })
        if (!pendingObservers.contains(observer)) {
            pendingObservers.add(observer)
        }
    }
}
