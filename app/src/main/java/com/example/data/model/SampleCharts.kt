package com.example.data.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

data class SampleChartInfo(
    val id: String,
    val pair: String,
    val timeframe: String,
    val type: String,
    val description: String
)

object SampleCharts {
    val samples = listOf(
        SampleChartInfo(
            id = "btcusdm_m5",
            pair = "BTCUSDm",
            timeframe = "M5",
            type = "Bitcoin / US Dollar",
            description = "Institutional Re-Distribution at 60k Resistance"
        ),
        SampleChartInfo(
            id = "xauusd_m5",
            pair = "XAUUSD",
            timeframe = "M5",
            type = "Gold / US Dollar",
            description = "SMC Bullish Liquidity Sweep & FVG Tap"
        ),
        SampleChartInfo(
            id = "eurusd_h1",
            pair = "EURUSD",
            timeframe = "H1",
            type = "Euro / US Dollar",
            description = "London Session Range Manipulation to Expansion"
        )
    )

    fun getSampleAnalysis(sampleId: String): ChartAnalysis {
        return when (sampleId) {
            "btcusdm_m5" -> ChartAnalysis(
                pair = "BTCUSDm",
                timeframe = "M5",
                direction = "SELL",
                confidence = 87,
                headline = "BTCUSDm: Institutional Re-Distribution at 60k Resistance for Bearish Continuation",
                strategy = "Institutional Re-Distribution",
                entryPrice = "60081.16",
                stopLoss = "60420.00",
                takeProfit = "58200.00",
                tp2 = "57100.00",
                tp3 = "55400.00",
                confluences = listOf(
                    "Smart Money false breakout trap above 60,000 psychological barrier",
                    "Massive sell-side imbalance created after liquidity sweep of Asian session high",
                    "Bearish Order Block rejected with high institutional volume spike",
                    "Lower timeframe Market Structure Shift (MSS) confirmed on M1/M5"
                ),
                phase = "Re-Distribution",
                setupGrade = "Grade A",
                macroBias = "BEARISH",
                keyLevels = listOf("60,000 (Psychological Invalidation)", "58,200 (Demand TP1)", "56,500 (Liquidity Target)"),
                riskReward = "1:1.5 - 1:7.59",
                executionQuality = 87,
                structuralConfidence = 85,
                poiScore = 90,
                status = "OPEN",
                keyTags = listOf("Strong Bearish", "Re-Distribution", "False Breakout Trap"),
                notes = "Enter on bearish confirmation candle close below 60,080. Invalidate if 15m candle closes above 60,420."
            )
            "xauusd_m5" -> ChartAnalysis(
                pair = "XAUUSD",
                timeframe = "M5",
                direction = "BUY",
                confidence = 92,
                headline = "XAUUSD: Bullish Liquidity Sweep & FVG Mitigation for Long Continuation",
                strategy = "SMC FVG Mitigation Long",
                entryPrice = "2345.50",
                stopLoss = "2338.20",
                takeProfit = "2362.00",
                tp2 = "2375.00",
                tp3 = "2390.00",
                confluences = listOf(
                    "Sell-side liquidity swept below equal lows at London open",
                    "Bullish displacement candle created 5-minute Fair Value Gap (FVG)",
                    "Premium / Discount equilibrium test: optimal trade entry (OTE) 62% retracement",
                    "Higher timeframe H4 trend is strongly bullish towards 2375"
                ),
                phase = "Expansion",
                setupGrade = "Grade A+",
                macroBias = "BULLISH",
                keyLevels = listOf("2338.00 (Structural SL)", "2345.50 (FVG POI)", "2362.00 (BSL Pool)"),
                riskReward = "1 : 3.8",
                executionQuality = 94,
                structuralConfidence = 91,
                poiScore = 95,
                status = "OPEN",
                keyTags = listOf("Strong Bullish", "Expansion", "FVG Mitigation"),
                notes = "Position size 1% risk. Move stop loss to breakeven once price reaches 2355.00."
            )
            else -> ChartAnalysis(
                pair = "EURUSD",
                timeframe = "H1",
                direction = "SELL",
                confidence = 84,
                headline = "EURUSD: London Session Range Manipulation to Bearish Expansion",
                strategy = "Judas Swing & Order Flow Shift",
                entryPrice = "1.08450",
                stopLoss = "1.08720",
                takeProfit = "1.07900",
                tp2 = "1.07500",
                tp3 = "1.07100",
                confluences = listOf(
                    "Asian high liquidity run followed by immediate rejection back into range",
                    "London session expansion initiating delivery of sell-side liquidity",
                    "Premium supply zone reaction with clean wick exhaustion"
                ),
                phase = "Manipulation",
                setupGrade = "Grade A",
                macroBias = "BEARISH",
                keyLevels = listOf("1.08720 (Asian High)", "1.08450 (Entry)", "1.07900 (Key Target)"),
                riskReward = "1 : 3.2",
                executionQuality = 85,
                structuralConfidence = 83,
                poiScore = 88,
                status = "OPEN",
                keyTags = listOf("Strong Bearish", "Manipulation", "Liquidity Sweep"),
                notes = "Target previous daily low at 1.07900."
            )
        }
    }

    fun generateSampleChartBitmap(pair: String, timeframe: String, isBullish: Boolean = true): Bitmap {
        val width = 900
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background dark theme
        val bgPaint = Paint().apply { color = Color.parseColor("#0F141C") }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Grid lines
        val gridPaint = Paint().apply {
            color = Color.parseColor("#1B2332")
            strokeWidth = 1.5f
        }
        for (x in 60 until width step 90) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridPaint)
        }
        for (y in 50 until height step 70) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridPaint)
        }

        // Header info text
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            isAntiAlias = true
            isFakeBoldText = true
        }
        canvas.drawText("$pair - $timeframe (OANDA / BINANCE)", 40f, 50f, textPaint)

        val subPaint = Paint().apply {
            color = Color.parseColor("#00FF66")
            textSize = 20f
            isAntiAlias = true
        }
        canvas.drawText("GENERATOR X CHART SCANNER ENGINE V4.8", 40f, 80f, subPaint)

        // Draw synthetic candlestick series
        val candleGreen = Color.parseColor("#00E676")
        val candleRed = Color.parseColor("#FF334B")
        val wickPaint = Paint().apply {
            strokeWidth = 2.5f
            isAntiAlias = true
        }
        val candleBodyPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        var currentPrice = if (pair == "XAUUSD") 2340.0f else if (pair == "BTCUSD") 68200.0f else 1.0850f
        val candleWidth = 18f
        val startX = 60f
        val numCandles = 32

        val prices = mutableListOf<Float>()
        for (i in 0 until numCandles) {
            val delta = if (isBullish) {
                if (i > 22) (Math.random() * 8 - 1).toFloat() else (Math.random() * 6 - 3).toFloat()
            } else {
                if (i > 22) (Math.random() * 8 - 7).toFloat() else (Math.random() * 6 - 3).toFloat()
            }
            currentPrice += delta
            prices.add(currentPrice)
        }

        val minP = prices.minOrNull() ?: 0f
        val maxP = prices.maxOrNull() ?: 1f
        val priceRange = (maxP - minP).coerceAtLeast(1f)
        val chartTop = 120f
        val chartBottom = height - 80f
        val chartHeight = chartBottom - chartTop

        fun toY(p: Float): Float {
            return chartBottom - ((p - minP) / priceRange) * chartHeight
        }

        // Draw FVG zone box
        val fvgPaint = Paint().apply {
            color = Color.parseColor(if (isBullish) "#3300FF66" else "#33FF334B")
            style = Paint.Style.FILL
        }
        val fvgStroke = Paint().apply {
            color = Color.parseColor(if (isBullish) "#00FF66" else "#FF334B")
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        val fvgTop = toY(minP + priceRange * 0.45f)
        val fvgBottom = toY(minP + priceRange * 0.35f)
        canvas.drawRect(RectF(180f, fvgTop, 820f, fvgBottom), fvgPaint)
        canvas.drawRect(RectF(180f, fvgTop, 820f, fvgBottom), fvgStroke)

        val fvgLabelPaint = Paint().apply {
            color = Color.parseColor(if (isBullish) "#00FF66" else "#FF334B")
            textSize = 18f
            isAntiAlias = true
        }
        canvas.drawText("FAIR VALUE GAP (M5 FVG ZONE)", 190f, fvgTop - 8f, fvgLabelPaint)

        // Draw Candlesticks
        for (i in 0 until numCandles) {
            val cx = startX + i * 25f
            val open = prices[i]
            val close = if (i < numCandles - 1) prices[i + 1] else open + (if (isBullish) 3f else -3f)
            val high = maxOf(open, close) + (Math.random() * 2f).toFloat()
            val low = minOf(open, close) - (Math.random() * 2f).toFloat()

            val isGreen = close >= open
            val cColor = if (isGreen) candleGreen else candleRed

            wickPaint.color = cColor
            candleBodyPaint.color = cColor

            val yHigh = toY(high)
            val yLow = toY(low)
            val yOpen = toY(open)
            val yClose = toY(close)

            // Wick
            canvas.drawLine(cx, yHigh, cx, yLow, wickPaint)
            // Body
            val top = minOf(yOpen, yClose)
            val bottom = maxOf(yOpen, yClose).coerceAtLeast(top + 2f)
            canvas.drawRect(cx - candleWidth / 2f, top, cx + candleWidth / 2f, bottom, candleBodyPaint)
        }

        // Live price line
        val lastY = toY(prices.last())
        val linePaint = Paint().apply {
            color = Color.parseColor("#00FF66")
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(0f, lastY, width.toFloat(), lastY, linePaint)

        // Price label on right axis
        val priceTagPaint = Paint().apply {
            color = Color.parseColor("#00FF66")
            style = Paint.Style.FILL
        }
        canvas.drawRect(width - 130f, lastY - 18f, width.toFloat(), lastY + 18f, priceTagPaint)

        val priceTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(String.format("%.2f", prices.last()), width - 120f, lastY + 7f, priceTextPaint)

        return bitmap
    }
}
