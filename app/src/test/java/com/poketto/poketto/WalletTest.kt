package com.poketto.poketto

import android.content.Context
import com.poketto.poketto.services.Wallet
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.Request
import org.web3j.protocol.core.methods.response.EthGetBalance

class WalletTest {

    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        Wallet.mockWeb3j = null
    }

    @Test
    fun testStrToHexAndHexToStr() {
        val wallet = Wallet(mockContext)
        val originalText = "Hello Poketto"
        val hex = wallet.strToHex(originalText)
        assertNotNull(hex)
        val decodedText = wallet.hexToStr(hex)
        assertEquals(originalText, decodedText)
    }

    @Test
    fun testBalanceFromWithMockWeb3j() {
        val mockWeb3j = mock(Web3j::class.java)
        @Suppress("UNCHECKED_CAST")
        val mockRequest = mock(Request::class.java) as Request<*, EthGetBalance>
        val mockResponse = EthGetBalance()
        mockResponse.result = "0x0de0b6b3a7640000" // 1 ETH / 1 xDai in wei

        `when`(mockWeb3j.ethGetBalance(eq("0x123"), any())).thenReturn(mockRequest)
        `when`(mockRequest.sendAsync()).thenReturn(java8.util.concurrent.CompletableFuture.completedFuture(mockResponse))

        Wallet.mockWeb3j = mockWeb3j
        val wallet = Wallet(mockContext)
        val balance = wallet.balanceFrom("0x123")

        assertEquals(1.0, balance, 0.0001)
    }
}
