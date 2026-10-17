package com.poketto.poketto.services

import android.content.Context
import android.util.Log
import de.adorsys.android.securestoragelibrary.SecurePreferences
import org.web3j.crypto.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import java.math.BigInteger
import java.security.SecureRandom
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT
import org.web3j.crypto.MnemonicUtils
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.utils.Convert
import java.math.BigDecimal
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt
import org.web3j.utils.Numeric
import java.net.URLDecoder
import android.R.string
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.charset.Charset


import com.poketto.poketto.models.Network

class Wallet(
    private val context: Context,
    var network: Network = Network.GNOSIS,
    private var customKeyManager: KeyManager? = null
) {

    val keyManager: KeyManager
        get() = customKeyManager ?: LocalKeyManager(context)

    companion object {
        var mockWeb3j: Web3j? = null
    }

    val GAS_PRICE = BigInteger.valueOf(1_000_000_000L)
    val GAS_LIMIT = BigInteger.valueOf(21000L)
    private val weiToDaiRate = 1000000000000000000

    fun getWeb3j(): Web3j {
        return mockWeb3j ?: Web3j.build(HttpService(network.rpcUrl))
    }

    fun getAddress() : String? {

        val address = keyManager.getAddress()
        if (address != null) {
            Log.d("getAddress", "address: $address")
            return address
        }

        return null
    }

    fun generate() {

        val initialEntropy = ByteArray(32)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(initialEntropy)

        val mnemonic = MnemonicUtils.generateMnemonic(initialEntropy)
        Log.d("generate", "mnemonic: $mnemonic")

        try {
            SecurePreferences.setValue("mnemonic", mnemonic, context)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    fun importSeed(seed: String) : Boolean {

        SecurePreferences.setValue("mnemonic", seed, context)
        return false
    }

    fun exportSeed() : String? {

        return SecurePreferences.getStringValue("mnemonic", context, null)
    }

    fun balanceFrom(address: String) : Double {

        // send asynchronous requests to get balance
        val ethGetBalance = getWeb3j()
            .ethGetBalance(address, DefaultBlockParameterName.LATEST)
            .sendAsync()
            .get()

        val wei = ethGetBalance.balance

        val dai = wei.toDouble() / weiToDaiRate

        return dai
    }

    private fun handleException(e: Exception) {
        Log.e("handleException", e.message ?: "SecureStorage error")
    }

    fun getCredentialsFromPrivateKey() : Credentials {

        val mnemonic = SecurePreferences.getStringValue("mnemonic", context, null)
        val seed = MnemonicUtils.generateSeed(mnemonic, null)
        val masterKeypair = Bip32ECKeyPair.generateKeyPair(seed)
        val path = intArrayOf(44 or HARDENED_BIT, 60 or HARDENED_BIT, 0 or HARDENED_BIT, 0, 0)
        val x = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path)
        return Credentials.create(x)
    }

    fun send(toAddress: String, value: String, message: String?, success: (result: TransactionReceipt) -> Unit, failure: (result: String) -> Unit) {

        val web3 = getWeb3j()

        try {

            val amountEther = BigDecimal.valueOf(value.toDouble())
            val amountWei = Convert.toWei(amountEther, Convert.Unit.ETHER).toBigInteger()

            var gasLimit = GAS_LIMIT
            var extraData = ""
            if(message != null) {
                gasLimit = BigInteger.valueOf(80000L)
                extraData = strToHex(message)
            }

            val ethGetTransactionCount = web3.ethGetTransactionCount(getAddress(), DefaultBlockParameterName.PENDING).send()
            val nonce = ethGetTransactionCount.transactionCount


            val rawTransaction = RawTransaction.createTransaction(nonce, GAS_PRICE, gasLimit, toAddress, amountWei, extraData)
            val hexValue = keyManager.signTransaction(rawTransaction, network.chainId)

            val ethSendTransaction = web3.ethSendRawTransaction(hexValue).send()
            val transactionHash = ethSendTransaction.transactionHash
            var transactionReceipt : EthGetTransactionReceipt

            var numberOfTries = 0
            while (true) {
                transactionReceipt = web3
                    .ethGetTransactionReceipt(transactionHash)
                    .send()
                if (transactionReceipt.result != null && transactionReceipt.result.blockHash != null) {
                    break
                }  else if(numberOfTries == 5) {
                    throw Exception("Failed executing transaction. Contact our support.")
                }
                Thread.sleep(1000)
                numberOfTries++
            }

            val txReceipt = transactionReceipt

            success(txReceipt.result)

        } catch (e: Exception) {

            Log.d("failed", "sending tx: " + e.toString())

            failure(e.localizedMessage)
        }
    }

    fun strToHex(str: String): String {

        return String.format("%04x", BigInteger(1, str.toByteArray(charset("UTF8"))))
    }

    fun hexToStr(hex: String): String {
        val buff = ByteBuffer.allocate(hex.length / 2)
        var i = 0
        while (i < hex.length) {
            buff.put(Integer.parseInt(hex.substring(i, i + 2), 16).toByte())
            i += 2
        }
        buff.rewind()
        val cs = Charset.forName("UTF-8")
        val cb = cs.decode(buff)

        return cb.toString()
    }

}