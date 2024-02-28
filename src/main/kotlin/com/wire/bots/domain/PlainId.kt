package com.wire.bots.domain

typealias MessageId = String
// These IDs are plain since Roman does not handle qualified IDs yet.
// We will need to change this in the future when Roman will be able to handle qualified IDs.
typealias PlainUserId = String
typealias PlainConversationId = String
typealias TaskId = String