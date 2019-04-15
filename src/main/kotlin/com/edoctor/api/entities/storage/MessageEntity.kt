package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "messages")
class MessageEntity constructor(

        givenUuid: UUID?,

        @Column(nullable = false)
        val timestamp: Long,

        @Column(nullable = true)
        val text: String? = null,

        @Column(nullable = true)
        val type: Int? = null,

        @Column(nullable = true)
        val imageUuid: String? = null,

        @Column(nullable = true)
        val callStatus: Int? = null,

        @Column(nullable = true)
        val callUuid: String? = null,

        @Column(nullable = false)
        val isFromPatient: Boolean,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "conversationUuid", nullable = false)
        val conversation: ConversationEntity

) : RandomUuidEntity(givenUuid)