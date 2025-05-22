package com.wire.bots.infrastructure.repository

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import com.wire.integrations.jvm.model.QualifiedId
import java.util.UUID

@Converter(autoApply = true)
class QualifiedIdConverter : AttributeConverter<QualifiedId, String> {
    override fun convertToDatabaseColumn(attribute: QualifiedId?): String? =
        attribute?.let {
            val stringId = attribute.id
            val stringDomain = attribute.domain
            ("$stringId@$stringDomain")
        }
    override fun convertToEntityAttribute(dbData: String?): QualifiedId? =
        dbData?.let {
            val splitQualifiedId = dbData.split("@")
            if (splitQualifiedId.size != 2) return null
            val uuid = try { UUID.fromString(splitQualifiedId[0]) } catch (e: IllegalArgumentException) { return null }
            val domain = splitQualifiedId[1]
            QualifiedId(uuid, domain)
        }
}
