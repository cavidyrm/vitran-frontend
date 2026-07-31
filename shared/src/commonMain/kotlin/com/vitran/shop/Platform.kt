package com.vitran.shop

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform