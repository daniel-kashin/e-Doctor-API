package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessagesRepository : JpaRepository<Message, String> {

    fun findByTimestampGreaterThanAndConversationUuidOrderByTimestamp(
            fromTimestamp: Long,
            conversationUuid: String
    ): List<Message>

}