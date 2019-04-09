package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@NoArg
@Entity
@Table(name = "medical_events")
class MedicalEventEntity(

        givenUuid: UUID?,

        @Column(nullable = false)
        var timestamp: Long,

        @Column(nullable = false)
        val type: Int,

        @Column(nullable = true)
        var endTimestamp: Long? = null,

        @Column(nullable = true)
        var name: String? = null,

        @Column(nullable = true)
        var clinic: String? = null,

        @Column(nullable = true)
        var doctorName: String? = null,

        @Column(nullable = true)
        var doctorSpecialization: String? = null,

        @Column(nullable = true)
        var symptoms: String? = null,

        @Column(nullable = true)
        var diagnosis: String? = null,

        @Column(nullable = true)
        var recipe: String? = null,

        @Column(nullable = true)
        var comment: String? = null

) : RandomUuidEntity(givenUuid)

class MedicalEntityType(
        val type: Int
)