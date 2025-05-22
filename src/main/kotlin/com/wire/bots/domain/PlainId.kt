package com.wire.bots.domain

import com.wire.integrations.jvm.model.QualifiedId

typealias MessageId = String

// These IDs are plain since Roman does not handle qualified IDs yet.
// We will need to change this in the future when Roman will be able to handle qualified IDs.


typealias ConversationId = QualifiedId
typealias PlainUserId = String
typealias PlainConversationId = String
typealias TaskId = String
