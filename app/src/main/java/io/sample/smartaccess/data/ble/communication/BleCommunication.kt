package io.sample.smartaccess.data.ble.communication

import no.nordicsemi.android.ble.data.Data

private val INCOMING_BYTES by lazy { byteArrayOf(0x01) }
private val OUTGOING_BYTES by lazy { byteArrayOf(0x02) }

internal interface BleCommunication {

    suspend operator fun invoke(data: Data): suspend () -> ByteArray
}

internal class SimpleBleCommunication : BleCommunication {

    private val protocolValidator: IncomingDataValidator by lazy(::IncomingDataValidator)

    override suspend fun invoke(data: Data): suspend () -> ByteArray {
        protocolValidator(data)
        return { OUTGOING_BYTES }
    }
}

private class IncomingDataValidator {

    operator fun invoke(data: Data) {
        if (!data.value.contentEquals(INCOMING_BYTES)) throw InvalidProtocolException
    }
}

internal object InvalidProtocolException : IllegalStateException()