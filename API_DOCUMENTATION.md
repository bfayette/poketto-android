# Poketto API, Multi-Network & Asset Management Architecture Documentation

Poketto provides crypto asset tracking and transaction management via an operator-managed key architecture (supporting Turnkey and non-custodial delegated key management) so end users do not need to directly handle private key mechanics.

---

## 1. Supported EVM Networks

Poketto supports 5 EVM networks out of the box:

| Network | Chain ID | Native Token | RPC Endpoint | Explorer API URL |
|---------|----------|--------------|--------------|------------------|
| **Gnosis Chain (xDai)** | `100` | xDai | `https://rpc.gnosischain.com` | `https://blockscout.com/poa/dai/` |
| **Ethereum Mainnet** | `1` | ETH | `https://cloudflare-eth.com` | `https://api.etherscan.io/` |
| **Polygon POS** | `137` | MATIC | `https://polygon-rpc.com` | `https://api.polygonscan.com/` |
| **Arbitrum One** | `42161` | ETH | `https://arb1.arbitrum.io/rpc` | `https://api.arbiscan.io/` |
| **Optimism** | `10` | ETH | `https://mainnet.optimism.io` | `https://api-optimistic.etherscan.io/` |

---

## 2. Key Management & Turnkey Integration Architecture (`KeyManager`)

To support asset leverage operators and security solutions such as **Turnkey**, key signing and key derivation are abstracted via the `KeyManager` interface:

```
                  +------------------------+
                  |   KeyManager Interface |
                  +-----------+------------+
                              |
         +--------------------+--------------------+
         |                                         |
         v                                         v
+------------------------+             +------------------------+
|   TurnkeyKeyManager    |             |    LocalKeyManager     |
| (Operator / Enclave)   |             |  (Android SecurePref)  |
+------------------------+             +------------------------+
```

```kotlin
interface KeyManager {
    fun getAddress(): String?
    fun signTransaction(rawTransaction: RawTransaction, chainId: Long): String
    fun isTurnkeyManaged(): Boolean
}
```

### Implementations
1. **`TurnkeyKeyManager`**:
   - Manages sub-organization asset credentials and delegates signing to the Turnkey enclave operator on behalf of end users.
   - End users do not hold or manage raw private keys directly.

2. **`LocalKeyManager`**:
   - Fallback local key management using Android `SecurePreferences`.

---

## 3. Explorer Service API Contract (Blockscout / Etherscan-compatible)

* **Endpoint**: `/api` (or `/poa/dai/api`)
* **HTTP Method**: `GET`

### Query Parameters
| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `module`  | String | Requested API module | `account` |
| `action`  | String | Requested action | `txlist` |
| `address` | String | Target Ethereum/EVM address | `0x1234...` |

### Mocking the Explorer API
```kotlin
val mockWebServer = MockWebServer()
mockWebServer.enqueue(MockResponse().setBody(jsonResponseBody))
val initializer = RetrofitInitializer(mockWebServer.url("/").toString())
```

---

## 4. Web3 JSON-RPC API Contract

* **Protocol**: JSON-RPC 2.0 over HTTP

### Supported JSON-RPC Methods
- `eth_getBalance`: Returns native account balance in Wei.
- `eth_getTransactionCount`: Returns current pending transaction count / nonce.
- `eth_sendRawTransaction`: Broadcasts a signed raw transaction.
- `eth_getTransactionReceipt`: Retrieves mined transaction receipt status.
- `eth_getTransactionByHash`: Retrieves transaction parameters by transaction hash.

### Mocking Web3 RPC Node
```kotlin
val mockWeb3j = Mockito.mock(Web3j::class.java)
Wallet.mockWeb3j = mockWeb3j
```

---

## 5. End-to-End Test Verification Report

All 13 unit and end-to-end integration test cases pass successfully across `debug` and `release` build variants:

```
BUILD SUCCESSFUL in 1s
43 actionable tasks: 43 up-to-date
Passed tests:
- ExplorerApiTest.testTransactionsApiParsing (MockWebServer HTTP 200)
- NetworkAndKeyManagerTest.testNetworkResolutionFromChainId
- NetworkAndKeyManagerTest.testTurnkeyKeyManager
- NetworkAndKeyManagerTest.testWalletWithTurnkeyManagerAndNetwork
- NetworkAndKeyManagerTest.testRetrofitInitializerForNetwork
- MainActivityLogicTest.testBuildTransactionsViewModelsWithDateGrouping
- MainActivityLogicTest.testEmptyGroupedTransactionsReturnsEmptyViewModels
- PaymentFlowTest.testMaxBalanceWithDefaultTransactionCost
- PaymentFlowTest.testMaxBalanceWithNoteTransactionCost
- PaymentFlowTest.testNoteHexEncoding
- WalletTest.testStrToHexAndHexToStr
- WalletTest.testBalanceFromWithMockWeb3j
- ExampleUnitTest.addition_isCorrect
```
