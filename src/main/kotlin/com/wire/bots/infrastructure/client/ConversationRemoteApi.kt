package com.wire.bots.infrastructure.client

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "wire-proxy-services-api")
@Path("/api/conversation")
interface ConversationRemoteApi {
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    fun sendMessage(
        @HeaderParam(AUTHORIZATION) token: BearerToken,
        messageContent: OutgoingMessage
    )
}