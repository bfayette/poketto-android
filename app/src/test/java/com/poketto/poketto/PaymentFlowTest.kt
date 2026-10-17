package com.poketto.poketto

import android.content.Context
import com.poketto.poketto.services.Wallet
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.Locale

class PaymentFlowTest {

    @Test
    fun testMaxBalanceWithDefaultTransactionCost() {
        val balance = 10.0
        val transactionCost = 0.000021f
        val maxAmount = String.format(Locale.US, "%.6f", balance - transactionCost)
        assertEquals("9.999979", maxAmount)
    }

    @Test
    fun testMaxBalanceWithNoteTransactionCost() {
        val balance = 10.0
        val transactionCost = 0.00008f
        val maxAmount = String.format(Locale.US, "%.6f", balance - transactionCost)
        assertEquals("9.999920", maxAmount)
    }

    @Test
    fun testNoteHexEncoding() {
        val mockContext = mock(Context::class.java)
        val wallet = Wallet(mockContext)
        val note = "Lunch repayment"
        val hex = wallet.strToHex(note)
        assertTrue(hex.isNotEmpty())
        val decoded = wallet.hexToStr(hex)
        assertEquals(note, decoded)
    }
}
