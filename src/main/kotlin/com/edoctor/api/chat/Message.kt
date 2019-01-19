package com.edoctor.api.chat

abstract class Message {
    abstract val uuid: String
    abstract val recipientUuid: String
    abstract val sendingTimestamp: Long
}

abstract class SystemMessage : Message()

abstract class UserMessage : Message() {
    abstract val senderUuid: String
}


// TODO
data class ConsultationStatusMessage(
        override val uuid: String,
        override val recipientUuid: String,
        override val sendingTimestamp: Long,
        val initiatorUuid: String,
        val statusType: StatusType
) : SystemMessage() {

    sealed class StatusType {
        class Started(val startTimestamp: Long) : StatusType()
        class Missed(val initiationTimestamp: Long) : StatusType()
        class Ended(val endTimestamp: Long) : StatusType()
    }
}

data class CallStatusMessage(
        override val uuid: String,
        override val recipientUuid: String,
        override val sendingTimestamp: Long,
        val initiatorUuid: String,
        val statusType: StatusType
) : SystemMessage() {

    sealed class StatusType {
        class Started(val startTimestamp: Long) : StatusType()
        class Missed(val initiationTimestamp: Long) : StatusType()
        class Ended(val endTimestamp: Long) : StatusType()
    }
}

data class MedicalRecordsAccessChangedMessage(
        override val uuid: String,
        override val recipientUuid: String,
        override val sendingTimestamp: Long,
        val isAllowed: Boolean,
        val patientUuid: String,
        val medicalRecordAccessData: MedicalRecordAccessData
) : SystemMessage() {

    sealed class MedicalRecordAccessData {
        class AllRecordsAccessData : MedicalRecordAccessData()
        class SomeRecordsAccessData(val medicalRecordUuids: List<String>) : MedicalRecordAccessData()
    }
}


data class DocumentMessage(
        override val uuid: String,
        override val senderUuid: String,
        override val recipientUuid: String,
        override val sendingTimestamp: Long,
        val documentUuid: String
) : UserMessage()

data class TextMessage(
        override val uuid: String,
        override val senderUuid: String,
        override val recipientUuid: String,
        override val sendingTimestamp: Long,
        val text: String
) : UserMessage()