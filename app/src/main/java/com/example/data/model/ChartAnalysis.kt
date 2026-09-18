package com.example.data.model

data class ChartAnalysis(
    val pair: String = "XAUUSD",
    val timeframe: String = "M5",
    val direction: String = "BUY", // BUY | SELL | NEUTRAL / WAIT
    val confidence: Int = 85,
    val headline: String = "Institutional Structure Confirmation",
    val strategy: String = "SMC Liquidity Sweep + Fair Value Gap (FVG)",
    val entryPrice: String = "2345.50",
    val takeProfit: String = "2362.00",
    val stopLoss: String = "2338.20",
    val tp2: String = "",
    val tp3: String = "",
    val confluences: List<String> = listOf(
        "Sell-side liquidity (SSL) swept below previous low",
        "Bullish Market Structure Shift (MSS) confirmed on M5",
        "Displacement created unmitigated Bullish FVG at entry",
        "Aligned with London session order flow expansion"
    ),
    val phase: String = "Expansion",
    val setupGrade: String = "Grade A",
    val macroBias: String = "BULLISH",
    val keyLevels: List<String> = listOf("2335.00 (Major Demand)", "2350.00 (Psych Level)", "2365.00 (Liquidity Pool)"),
    val riskReward: String = "1 : 3.2",
    val executionQuality: Int = 87,
    val structuralConfidence: Int = 85,
    val poiScore: Int = 90,
    val status: String = "OPEN", // OPEN | WIN | LOSS
    val keyTags: List<String> = listOf("Strong Bullish", "FVG Mitigation", "Liquidity Sweep"),
    val notes: String = "Wait for minor pullback tap into the FVG before entering long."
) {
    val isBuy: Boolean get() = direction.equals("BUY", ignoreCase = true)
    val isSell: Boolean get() = direction.equals("SELL", ignoreCase = true)
    val isNeutral: Boolean get() = direction.equals("NEUTRAL", ignoreCase = true) || direction.equals("WAIT", ignoreCase = true)
    val isWin: Boolean get() = status.equals("WIN", ignoreCase = true)
    val isLoss: Boolean get() = status.equals("LOSS", ignoreCase = true)
    val isOpen: Boolean get() = status.equals("OPEN", ignoreCase = true)
}
