package com.edoctor.api.entities.network.response

import com.edoctor.api.entities.network.model.user.DoctorModel

data class DoctorsResponse(
        val doctors: List<DoctorModel>
)