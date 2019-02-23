package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.util.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "conversations", uniqueConstraints = [UniqueConstraint(columnNames = ["patientUuid", "doctorUuid"])])
data class Conversation(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "patientUuid", nullable = false, insertable = false, updatable = false)
        val patient: Patient,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "doctorUuid", nullable = false, insertable = false, updatable = false)
        val doctor: Doctor,

        @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL])
        val messages: MutableList<Message>
) : RandomUuidEntity()