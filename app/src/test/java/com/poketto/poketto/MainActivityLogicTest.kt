package com.poketto.poketto

import com.poketto.poketto.models.DashboardTransactionViewModel
import com.poketto.poketto.models.Transaction
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class MainActivityLogicTest {

    @Test
    fun testBuildTransactionsViewModelsWithDateGrouping() {
        val date1 = Date(1600000000000L)
        val date2 = Date(1500000000000L)

        val tx1 = Transaction().apply {
            hash = "0x1"
            value = "1000"
            timeStamp = "1600000"
        }
        val tx2 = Transaction().apply {
            hash = "0x2"
            value = "2000"
            timeStamp = "1500000"
        }

        val groupedMap = HashMap<Date, ArrayList<Transaction>>()
        groupedMap[date1] = arrayListOf(tx1)
        groupedMap[date2] = arrayListOf(tx2)

        val keys = groupedMap.keys
        val viewModels = arrayListOf<DashboardTransactionViewModel>()
        for (key in keys.sortedByDescending { it.time }) {
            val dateHeaderVm = DashboardTransactionViewModel().apply {
                date = key
                transaction = null
            }
            viewModels.add(dateHeaderVm)

            for (tx in groupedMap[key]!!) {
                val itemVm = DashboardTransactionViewModel().apply {
                    date = null
                    transaction = tx
                }
                viewModels.add(itemVm)
            }
        }

        assertEquals(4, viewModels.size)
        // Header for date1
        assertNotNull(viewModels[0].date)
        assertNull(viewModels[0].transaction)
        // Item for tx1
        assertNull(viewModels[1].date)
        assertEquals("0x1", viewModels[1].transaction?.hash)
        // Header for date2
        assertNotNull(viewModels[2].date)
        assertNull(viewModels[2].transaction)
        // Item for tx2
        assertNull(viewModels[3].date)
        assertEquals("0x2", viewModels[3].transaction?.hash)
    }

    @Test
    fun testEmptyGroupedTransactionsReturnsEmptyViewModels() {
        val viewModels = arrayListOf<DashboardTransactionViewModel>()
        assertEquals(0, viewModels.size)
    }
}
