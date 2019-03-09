package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "conversations", uniqueConstraints = [UniqueConstraint(columnNames = ["patientUuid", "doctorUuid"])])
class ConversationEntity constructor(

        givenUuid: UUID?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "patientUuid", nullable = false)
        val patient: PatientEntity,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "doctorUuid", nullable = false)
        val doctor: DoctorEntity,

        @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL])
        val messages: MutableList<MessageEntity>

) : RandomUuidEntity(givenUuid)