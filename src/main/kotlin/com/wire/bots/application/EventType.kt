package com.wire.bots.application

import com.fasterxml.jackson.annotation.JsonProperty

enum class EventType {
    @JsonProperty("conversation.bot_request")
    BOT_REQUEST,

    @JsonProperty("conversation.bot_removed")
    BOT_REMOVED,

    @JsonProperty("conversation.init")
    CONVERSATION_INIT,

    @JsonProperty("conversation.new_text")
    NEW_TEXT,

    @JsonProperty("conversation.image.preview")
    IMAGE_PREVIEW,

    @JsonProperty("conversation.file.preview")
    FILE_PREVIEW,

    @JsonProperty("conversation.audio.preview")
    AUDIO_PREVIEW,

    @JsonProperty("conversation.asset.data")
    ASSET_DATA,

    @JsonProperty("conversation.poll.action")
    POLL_ACTION,

    @JsonProperty("conversation.user_joined")
    USER_JOINED,

    @JsonProperty("conversation.reaction")
    REACTION,
}