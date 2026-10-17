package com.poketto.poketto.models

enum class Network(
    val id: String,
    val displayName: String,
    val symbol: String,
    val chainId: Long,
    val rpcUrl: String,
    val explorerApiUrl: String,
    val explorerTxUrl: String
) {
    GNOSIS(
        id = "gnosis",
        displayName = "Gnosis Chain (xDai)",
        symbol = "xDai",
        chainId = 100L,
        rpcUrl = "https://rpc.gnosischain.com",
        explorerApiUrl = "https://blockscout.com/poa/dai/",
        explorerTxUrl = "https://blockscout.com/poa/dai/tx/"
    ),
    ETHEREUM(
        id = "ethereum",
        displayName = "Ethereum Mainnet",
        symbol = "ETH",
        chainId = 1L,
        rpcUrl = "https://cloudflare-eth.com",
        explorerApiUrl = "https://api.etherscan.io/",
        explorerTxUrl = "https://etherscan.io/tx/"
    ),
    POLYGON(
        id = "polygon",
        displayName = "Polygon POS",
        symbol = "MATIC",
        chainId = 137L,
        rpcUrl = "https://polygon-rpc.com",
        explorerApiUrl = "https://api.polygonscan.com/",
        explorerTxUrl = "https://polygonscan.com/tx/"
    ),
    ARBITRUM(
        id = "arbitrum",
        displayName = "Arbitrum One",
        symbol = "ETH",
        chainId = 42161L,
        rpcUrl = "https://arb1.arbitrum.io/rpc",
        explorerApiUrl = "https://api.arbiscan.io/",
        explorerTxUrl = "https://arbiscan.io/tx/"
    ),
    OPTIMISM(
        id = "optimism",
        displayName = "Optimism",
        symbol = "ETH",
        chainId = 10L,
        rpcUrl = "https://mainnet.optimism.io",
        explorerApiUrl = "https://api-optimistic.etherscan.io/",
        explorerTxUrl = "https://optimistic.etherscan.io/tx/"
    );

    companion object {
        fun fromChainId(chainId: Long): Network {
            return values().firstOrNull { it.chainId == chainId } ?: GNOSIS
        }
    }
}
