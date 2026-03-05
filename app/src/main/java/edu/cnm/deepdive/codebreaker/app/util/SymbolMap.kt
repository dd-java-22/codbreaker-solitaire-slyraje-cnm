package edu.cnm.deepdive.codebreaker.app.util;

import android.content.Context;
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.cnm.deepdive.codebreaker.app.R
import jakarta.inject.Inject;
import javax.inject.Singleton;

class SymbolMap @Inject constructor(
    @param:ActivityContext private val context: Context
) {


    private val symbols: Map<Int, SymbolAttributes>

    val keys: List<Int> by lazy { symbols.keys.toList() }

    init {
        val names = context.resources.getStringArray(R.array.color_names)
        val valuesTyped = context.resources.obtainTypedArray(R.array.color_values)
        val values = mutableListOf<Int>()
        for (i in 0 until valuesTyped.length()) {
            val color = valuesTyped.getColor(i, Color.TRANSPARENT)
            values.add(color)
        }
        val keys = context.resources.getStringArray(R.array.color_keys)
        val drawableIds = context.resources.getIntArray(R.array.color_drawables)
        val drawables = getDrawables(context.resources)
        symbols = keys
            .mapIndexed { index, key ->
                key.codePointAt(0) to SymbolAttributes(
                    values[index],
                    names[index],
                    drawables[index]
                )
            }
            .toMap()
    }

    fun getColor(key: Int): Int = symbols.getValue(key).value

    fun getName(key: Int): String = symbols.getValue(key).name

    fun getDrawable(key: Int): Drawable = symbols.getValue(key).drawable

    private fun getDrawables(res: Resources): List<Drawable> {
        val typedArray = res.obtainTypedArray(R.array.color_drawables)
        return try {
            List(typedArray.length()) { i ->
                ContextCompat.getDrawable(context, typedArray.getResourceId(i, 0)) as Drawable
            }
        } finally {
            typedArray.recycle()
        }
    }

    private data class SymbolAttributes(
        val value: Int,
        val name: String,
        val drawable: Drawable
    )

}