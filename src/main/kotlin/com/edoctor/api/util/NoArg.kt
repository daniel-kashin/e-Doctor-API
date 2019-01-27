package com.edoctor.api.util

import kotlin.annotation.Retention
import kotlin.annotation.Target

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class NoArg()
