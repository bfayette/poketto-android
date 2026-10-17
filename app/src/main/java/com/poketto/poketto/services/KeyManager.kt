package com.poketto.poketto.services

import android.content.Context
import de.adorsys.android.securestoragelibrary.SecurePreferences
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric

interface KeyManager {
    fun getAddress(): String?
    fun signTransaction(rawTransaction: RawTransaction, chainId: Long): String
    fun isTurnkeyManaged(): Boolean
}

class LocalKeyManager(private val context: Context) : KeyManager {

    override fun getAddress(): String? {
        val mnemonic = SecurePreferences.getStringValue("mnemonic", context, null) ?: return null
        return getCredentials(mnemonic).address
    }

    override fun signTransaction(rawTransaction: RawTransaction, chainId: Long): String {
        val mnemonic = SecurePreferences.getStringValue("mnemonic", context, null)
            ?: throw IllegalStateException("Mnemonic not found")
        val credentials = getCredentials(mnemonic)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId.toByte(), credentials)
        return Numeric.toHexString(signedMessage)
    }

    override fun isTurnkeyManaged(): Boolean = false

    private fun getCredentials(mnemonic: String): Credentials {
        val seed = MnemonicUtils.generateSeed(mnemonic, null)
        val masterKeypair = Bip32ECKeyPair.generateKeyPair(seed)
        val path = intArrayOf(44 or HARDENED_BIT, 60 or HARDENED_BIT, 0 or HARDENED_BIT, 0, 0)
        val derived = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path)
        return Credentials.create(derived)
    }
}

class TurnkeyKeyManager(
    val turnkeyOrganizationId: String,
    private var managedAddress: String? = null
) : KeyManager {

    fun setManagedAddress(address: String) {
        this.managedAddress = address
    }

    override fun getAddress(): String? = managedAddress

    override fun signTransaction(rawTransaction: RawTransaction, chainId: Long): String {
        checkNotNull(managedAddress) { "Turnkey managed address is not set" }
        val dummySignedBytes = TransactionEncoder.encode(rawTransaction, chainId.toByte())
        return Numeric.toHexString(dummySignedBytes)
    }

    override fun isTurnkeyManaged(): Boolean = true
}
