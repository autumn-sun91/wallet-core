package com.wallet.domain.common

import com.github.f4b6a3.ulid.UlidCreator

object ULIDGenerator {
    fun generate(): String = UlidCreator.getMonotonicUlid().toString()
}
