package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "messages")
data class Message(


        @Column(nullable = false)
        val timestamp: Long,

        @Column(nullable = false)
        val text: String,

        @Column(nullable = false)
        val isFromPatient: Boolean,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "conversationUuid", nullable = false, insertable = false, updatable = false)
        val conversation: Conversation
) : RandomUuidEntity()