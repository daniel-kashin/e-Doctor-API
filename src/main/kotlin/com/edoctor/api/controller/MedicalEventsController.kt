package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper
import com.edoctor.api.entities.network.model.record.SynchronizeEventsModel
import com.edoctor.api.entities.network.response.MedicalEventsResponse
import com.edoctor.api.entities.storage.MedicalEventEntity
import com.edoctor.api.entities.storage.MedicalEventEntityType
import com.edoctor.api.mapper.MedicalEventMapper
import com.edoctor.api.mapper.MedicalRecordTypeMapper
import com.edoctor.api.mapper.MedicalRecordTypeMapper.toDomain
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.MedicalAccessesRepository
import com.edoctor.api.repositories.MedicalEventRepository
import com.edoctor.api.repositories.PatientRepository
import com.edoctor.api.utils.MutexFactory
import com.edoctor.api.utils.currentUnixTime
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
class MedicalEventsController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var medicalEventRepository: MedicalEventRepository

    @Autowired
    private lateinit var medicalAccessesRepository: MedicalAccessesRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    private val mutexFactory: MutexFactory<String> = MutexFactory()

    @GetMapping("/medicalEventsForDoctor")
    @Transactional
    fun getEventsForDoctor(
            authentication: OAuth2Authentication,
            @RequestParam patientUuid: String
    ): ResponseEntity<MedicalEventsResponse> {
        val principal = authentication.principal as User

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val patient = patientRepository.findById(patientUuid).orElse(null)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val domainAccesses = medicalAccessesRepository.findAllByDoctorUuidAndPatientUuid(doctor.uuid, patientUuid)
                .map { MedicalRecordTypeMapper.toDomain(it) }

        val events = patient.medicalEvents
                .asSequence()
                .filter { !it.isDeleted }
                .filter { toDomain(MedicalEventEntityType(it.type)) in domainAccesses }
                .map { MedicalEventMapper.toWrapperFromEntity(it) }
                .toList()

        return ResponseEntity.ok(MedicalEventsResponse(events))
    }


    @PostMapping("/synchronizeEventsForPatient")
    @Transactional
    fun synchronizeEvents(
            @RequestBody request: SynchronizeEventsModel,
            authentication: OAuth2Authentication
    ): ResponseEntity<SynchronizeEventsModel> {
        val principal = authentication.principal as User

        synchronized(mutexFactory.getMutex(principal.username)) {
            val currentTimestamp = currentUnixTime()

            log.info { "currentTimestamp = $currentTimestamp" }

            val patient = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                    ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

            log.info { "synchronizeTimestamp = ${request.synchronizeTimestamp}" }

            val localEvents = medicalEventRepository
                    .getMedicalEventEntitiesByUpdateTimestampGreaterThanAndPatientUuid(
                            request.synchronizeTimestamp,
                            patient.uuid
                    )

            log.info { "localEvents = $localEvents" }

            val remoteEvents = request.events

            log.info { "remoteEvents = $remoteEvents" }

            val mergedEvents = localEvents
                    .asSequence()
                    .map { it.uuid }
                    .plus(remoteEvents.map { it.uuid })
                    .distinct()
                    .filterNotNull()
                    .mapNotNull<String, Any> { uuid ->
                        val remote = remoteEvents.firstOrNull { it.uuid == uuid }
                        val local = localEvents.firstOrNull { it.uuid == uuid }
                        when {
                            remote != null -> remote
                            local != null -> local
                            else -> null
                        }
                    }
                    .toList()

            log.info { "mergedEvents = $mergedEvents" }

            val eventsToSave = mergedEvents.mapNotNull {
                when (it) {
                    is MedicalEventWrapper -> {
                        val doctorCreator = it.doctorCreatorUuid
                                ?.let { uuid -> doctorRepository.findById(uuid) }
                                ?.orElse(null)
                        MedicalEventMapper.toEntityFromWrapper(it, patient, doctorCreator, currentTimestamp)
                    }
                    else -> null
                }
            }
            medicalEventRepository.saveAll(eventsToSave)

            val eventsToReturn = mergedEvents.mapNotNull {
                when (it) {
                    is MedicalEventWrapper -> it
                    is MedicalEventEntity -> MedicalEventMapper.toWrapperFromEntity(it)
                    else -> null
                }
            }
            return ResponseEntity.ok(SynchronizeEventsModel(eventsToReturn, currentTimestamp))
        }
    }

}