package com.edoctor.api.entities.network.response

sealed class MessageResponse {
    abstract val uuid: String
    abstract val recipientEmail: String
    abstract val sendingTimestamp: Long
}

sealed class SystemMessageResponse : MessageResponse()

sealed class UserMessageResponse : MessageResponse() {
    abstract val senderEmail: String
}


data class CallStatusMessageResponse(
        override val uuid: String,
        override val senderEmail: String,
        override val recipientEmail: String,
        override val sendingTimestamp: Long,
        val callStatus: Int,
        val callUuid: String
) : UserMessageResponse() {

    companion object {
        const val CALL_STATUS_INITIATED = 1
        const val CALL_STATUS_STARTED = 2
        const val CALL_STATUS_CANCELLED = 3
    }

}

data class TextMessageResponse(
        override val uuid: String,
        // TODO: replace with uuid
        override val senderEmail: String,
        override val recipientEmail: String,
        override val sendingTimestamp: Long,
        val text: String
) : UserMessageResponse()




// TODO
data class ConsultationStatusMessageResponse(
        override val uuid: String,
        override val recipientEmail: String,
        override val sendingTimestamp: Long,
        val initiatorUuid: String,
        val statusType: StatusType
) : SystemMessageResponse() {

    sealed class StatusType {
        class Started(val startTimestamp: Long) : StatusType()
        class Missed(val initiationTimestamp: Long) : StatusType()
        class Ended(val endTimestamp: Long) : StatusType()
    }
}

data class MedicalRecordsAccessChangedMessageResponse(
        override val uuid: String,
        override val recipientEmail: String,
        override val sendingTimestamp: Long,
        val isAllowed: Boolean,
        val patientUuid: String,
        val medicalRecordAccessData: MedicalRecordAccessData
) : SystemMessageResponse() {

    sealed class MedicalRecordAccessData {
        object AllRecordsAccessData : MedicalRecordAccessData()
        class SomeRecordsAccessData(val medicalRecordUuids: List<String>) : MedicalRecordAccessData()
    }
}


data class DocumentMessageResponse(
        override val uuid: String,
        override val senderEmail: String,
        override val recipientEmail: String,
        override val sendingTimestamp: Long,
        val documentUuid: String
) : UserMessageResponse()
