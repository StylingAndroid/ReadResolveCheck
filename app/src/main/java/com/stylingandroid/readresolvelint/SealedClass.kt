package com.stylingandroid.readresolvelint

import java.io.Serializable

sealed class SealedClass : Serializable {

    object ObjectWithoutReadResolve : SealedClass()

    object ObjectWithReadResolve : SealedClass() {
        @Suppress("UnusedPrivateMember")
        private fun readResolve(): Any =
            ObjectWithReadResolve
    }
}
