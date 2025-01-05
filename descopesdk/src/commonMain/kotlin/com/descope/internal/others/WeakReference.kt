package com.descope.internal.others

expect class WeakReference<T: Any>(referred: T) {
    fun get(): T?
}