package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "messages")
class Message constructor(

        givenUuid: UUID?,

        @Column(nullable = false)
        val timestamp: Long,

        @Column(nullable = false)
        val text: String,

        @Column(nullable = false)
        val isFromPatient: Boolean,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "conversationUuid", nullable = false)
        val conversation: Conversation

) : RandomUuidEntity(givenUuid)