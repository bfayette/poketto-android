package com.poketto.poketto

import android.content.Context
import com.poketto.poketto.api.RetrofitInitializer
import com.poketto.poketto.models.Network
import com.poketto.poketto.services.TurnkeyKeyManager
import com.poketto.poketto.services.Wallet
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.web3j.crypto.RawTransaction
import java.math.BigInteger

class NetworkAndKeyManagerTest {

    @Test
    fun testNetworkResolutionFromChainId() {
        assertEquals(Network.GNOSIS, Network.fromChainId(100L))
        assertEquals(Network.ETHEREUM, Network.fromChainId(1L))
        assertEquals(Network.POLYGON, Network.fromChainId(137L))
        assertEquals(Network.ARBITRUM, Network.fromChainId(42161L))
        assertEquals(Network.OPTIMISM, Network.fromChainId(10L))
        assertEquals(Network.GNOSIS, Network.fromChainId(99999L)) // Fallback default
    }

    @Test
    fun testTurnkeyKeyManager() {
        val turnkeyManager = TurnkeyKeyManager(
            turnkeyOrganizationId = "org_12345",
            managedAddress = "0xManagedAddress123"
        )

        assertTrue(turnkeyManager.isTurnkeyManaged())
        assertEquals("0xManagedAddress123", turnkeyManager.getAddress())
        assertEquals("org_12345", turnkeyManager.turnkeyOrganizationId)

        val rawTx = RawTransaction.createTransaction(
            BigInteger.ONE,
            BigInteger.valueOf(1000000000L),
            BigInteger.valueOf(21000L),
            "0xToAddress",
            BigInteger.valueOf(1000000000000000000L),
            ""
        )

        val signedTxHex = turnkeyManager.signTransaction(rawTx, Network.POLYGON.chainId)
        assertNotNull(signedTxHex)
        assertTrue(signedTxHex.startsWith("0x"))
    }

    @Test
    fun testWalletWithTurnkeyManagerAndNetwork() {
        val mockContext = mock(Context::class.java)
        val turnkeyManager = TurnkeyKeyManager("org_abc", "0xOperatorManagedUser")
        val wallet = Wallet(mockContext, network = Network.ARBITRUM, customKeyManager = turnkeyManager)

        assertEquals("0xOperatorManagedUser", wallet.getAddress())
        assertEquals(Network.ARBITRUM, wallet.network)
    }

    @Test
    fun testRetrofitInitializerForNetwork() {
        val ethereumInitializer = RetrofitInitializer.forNetwork(Network.ETHEREUM)
        assertNotNull(ethereumInitializer.explorer())

        val polygonInitializer = RetrofitInitializer.forNetwork(Network.POLYGON)
        assertNotNull(polygonInitializer.explorer())
    }
}
