package com.machina.playtify.model

import com.machina.playtify.model.Status.*

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?) = Resource(SUCCESS, data, null)

        fun <T> error(message: String?, data: T?) = Resource(ERROR, data, message)

        fun <T> loading(data: T?) = Resource(LOADING, data, null)
    }
}


enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}