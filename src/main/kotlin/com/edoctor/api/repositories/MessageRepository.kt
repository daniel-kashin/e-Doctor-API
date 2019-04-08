package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.MessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<MessageEntity, String> {

    fun findByTimestampGreaterThanAndConversationUuidOrderByTimestamp(
            fromTimestamp: Long,
            conversationUuid: String
    ): List<MessageEntity>

}