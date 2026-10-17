# Poketto 👛

![Design](https://repository-images.githubusercontent.com/191986354/943acc00-ebb5-11e9-9b5c-7a0ddc9ccdca)

Our goal with Poketto is to make it easy to send crypto assets to friends and pay for everyday items seamlessly. Poketto leverages an operator-managed key architecture (supporting Turnkey and non-custodial delegated signing) so end users do not manage raw private keys directly.

---

## Features

- **Operator Key Management & Turnkey Integration**: Leverages `KeyManager` abstraction for secure operator signing without end users managing private keys.
- **Multi-Network Support (5 EVM Chains)**: Gnosis Chain (xDai), Ethereum Mainnet, Polygon POS, Arbitrum One, and Optimism.
- **Send & Request Payments**: Send tokens to addresses or saved contacts; request funds via QR code or clipboard sharing.
- **Transaction Dashboard**: Categorize, group by date, search, and view complete transaction history.
- **Contacts Management**: Assign Ethereum addresses to device contacts with custom avatars.
- **Deterministic API Mocking**: Built-in support for offline MockWebServer and mock Web3j testing.

---

## System Architecture & Key Flow

```
+-----------------------------------------------------------------------+
|                            Poketto App                                 |
|                                                                       |
|  +---------------------+      +------------------------------------+  |
|  |     MainActivity    |      |         PaymentSendActivity        |  |
|  +----------+----------+      +------------------+-----------------+  |
|             |                                    |                    |
|             v                                    v                    |
|  +---------------------+      +------------------------------------+  |
|  | RetrofitInitializer |      |            Wallet Service          |  |
|  | (Blockscout/Etherscan)      |       (Web3j JSON-RPC Provider)    |  |
|  +----------+----------+      +------------------+-----------------+  |
+-------------|------------------------------------|--------------------+
              |                                    |
              v                                    v
+------------------------+          +-----------------------------------+
|  Explorer REST APIs    |          |    KeyManager Abstraction         |
|  - Gnosis (xDai)       |          |    - TurnkeyKeyManager (Delegated)|
|  - Ethereum Mainnet    |          |    - LocalKeyManager (Fallback)   |
|  - Polygon POS         |          +------------------+----------------+
|  - Arbitrum One        |                             |
|  - Optimism            |                             v
+------------------------+          +-----------------------------------+
                                    |    EVM Blockchain Nodes           |
                                    |    (JSON-RPC 2.0 / Web3)          |
                                    +-----------------------------------+
```

---

## Development & Testing

### Building the Project
```bash
./gradlew assembleDebug
```

### Running E2E & Unit Test Suites
```bash
./gradlew test
```

### Test Suite Summary
- **`WalletTest`**: Web3 balance retrieval via mock Web3j and message encoding/decoding.
- **`ExplorerApiTest`**: Blockscout REST API parsing and query parameter verification via `MockWebServer`.
- **`NetworkAndKeyManagerTest`**: Turnkey key manager integration, address management, and multi-network resolution for 5 EVM chains.
- **`MainActivityLogicTest`**: Dashboard transaction date grouping and view model mapping logic.
- **`PaymentFlowTest`**: Send payment gas cost calculations and note hex string formatting.

---

## License

poketto-android is licensed under the GPLv3 and MPLv2 licenses. See [LICENSE](LICENSE) for details.
