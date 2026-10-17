package com.poketto.poketto

import com.poketto.poketto.api.RetrofitInitializer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExplorerApiTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testTransactionsApiParsing() {
        val jsonResponseBody = """
            {
                "status": "1",
                "message": "OK",
                "result": [
                    {
                        "blockNumber": "1000",
                        "timeStamp": "1600000000",
                        "hash": "0xhash123",
                        "from": "0xfromAddress",
                        "to": "0xtoAddress",
                        "value": "2000000000000000000",
                        "input": "0x"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponseBody))

        val baseUrl = mockWebServer.url("/").toString()
        val retrofitInitializer = RetrofitInitializer(baseUrl)
        val call = retrofitInitializer.explorer().transactions("account", "txlist", "0xtoAddress")

        val response = call.execute()
        assertTrue(response.isSuccessful)

        val transactions = response.body()
        assertNotNull(transactions)
        assertEquals("1", transactions?.status)
        assertEquals(1, transactions?.result?.size)

        val tx = transactions?.result?.get(0)
        assertEquals("0xhash123", tx?.hash)
        assertEquals("0xfromAddress", tx?.from)
        assertEquals("0xtoAddress", tx?.to)
        assertEquals("2000000000000000000", tx?.value)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertTrue(recordedRequest.path!!.contains("module=account"))
        assertTrue(recordedRequest.path!!.contains("action=txlist"))
        assertTrue(recordedRequest.path!!.contains("address=0xtoAddress"))
    }
}
