package com.no5ing.bbibbi.data.model

class Resource<T> {
    var status: Status = Status.LOADING
    var data: T? = null
    var error: ResourceError? = null

    fun success(data: T) {
        this.status = Status.SUCCESS
        this.data = data
    }

    fun error(error: ResourceError) {
        this.status = Status.ERROR
        this.error = error
    }

    fun loading() {
        this.status = Status.LOADING
        this.data = null
        this.error = null
    }
}