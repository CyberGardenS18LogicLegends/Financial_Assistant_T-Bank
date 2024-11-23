package com.t_bank.financial_assistant.custom_view

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.RectF

class MultiColorProgressBarDrawable(private val categories: List<Category>) : Drawable() {

    data class Category(val color: Int, val percentage: Float)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cornerRadius = 10f // Радиус скругления

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        var startX = 0f

        categories.forEach { category ->
            paint.color = category.color
            val segmentWidth = width * category.percentage
            val rect = RectF(startX, 0f, startX + segmentWidth, height.toFloat())
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
            startX += segmentWidth
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}