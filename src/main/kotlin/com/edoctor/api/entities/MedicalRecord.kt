package com.edoctor.api.entities

abstract class MedicalRecord {

}

data class BloodType(val number: BloodTypeNumber, val isPositive: Boolean) {

    enum class BloodTypeNumber {
        FIRST,
        SECOND,
        THIRD,
        FOURTH
    }

}