package com.wire.bots.domain.usecase

// Validate reminder to be scheduled.
// - reminder should not be empty
// - scheduled time should be in the future
// - number of reminders per conversation should be limited to MAX_REMINDERS_PER_CONVERSATION = 3
