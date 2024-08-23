package org.koishi.launcher.h2co3.core.utils

import android.graphics.Color

/**
 * Created by HaiyuKing
 * Used Color工具类（color整型、rgb数组、16进制互相转换）
 */
object ColorUtils {
    /**
     * Color的Int整型转Color的16进制颜色值【方案一】
     * colorInt - -12590395
     * return Color的16进制颜色值——#3FE2C5
     */
    @JvmStatic
    fun int2Hex(colorInt: Int): String {
        val hexCode = String.format("#%06X", 16777215 and colorInt)
        return hexCode
    }

    /**
     * Color的Int整型转Color的16进制颜色值【方案二】
     * colorInt - -12590395
     * return Color的16进制颜色值——#3FE2C5
     */
    fun int2Hex2(colorInt: Int): String {
        val hexCode: String
        val rgb = int2Rgb(colorInt)
        hexCode = rgb2Hex(rgb)
        return hexCode
    }

    fun int2Hex3(colorInt: Int): String {
        return "#" +
                intToHex(Color.alpha(colorInt)) +
                intToHex(Color.red(colorInt)) +
                intToHex(Color.green(colorInt)) +
                intToHex(Color.blue(colorInt))
    }

    @JvmStatic
    fun intToHex(n: Int): String {
        var n = n
        var s = StringBuffer()
        val b = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F'
        )
        while (n != 0) {
            s = s.append(b[n % 16])
            n /= 16
        }
        var a = s.reverse().toString()
        a = addZore(a, 2)
        return a
    }

    private fun addZore(str: String, size: Int): String {
        var str = str
        if (str.length < size) {
            str = "0$str"
            str = addZore(str, size)
            return str
        } else {
            return str
        }
    }


    /**
     * Color的Int整型转Color的rgb数组
     * colorInt - -12590395
     * return Color的rgb数组 —— [63,226,197]
     */
    private fun int2Rgb(colorInt: Int): IntArray {
        val rgb = intArrayOf(0, 0, 0)

        val red = Color.red(colorInt)
        val green = Color.green(colorInt)
        val blue = Color.blue(colorInt)
        rgb[0] = red
        rgb[1] = green
        rgb[2] = blue

        return rgb
    }

    /**
     * rgb数组转Color的16进制颜色值
     * rgb - rgb数组——[63,226,197]
     * return Color的16进制颜色值——#3FE2C5
     */
    private fun rgb2Hex(rgb: IntArray): String {
        val hexCode = StringBuilder("#")
        for (value in rgb) {
            var rgbItem = value
            if (rgbItem < 0) {
                rgbItem = 0
            } else if (rgbItem > 255) {
                rgbItem = 255
            }
            val code = arrayOf(
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "A",
                "B",
                "C",
                "D",
                "E",
                "F"
            )
            val lCode = rgbItem / 16 //先获取商，例如，255 / 16 == 15
            val rCode = rgbItem % 16 //再获取余数，例如，255 % 16 == 15
            hexCode.append(code[lCode]).append(code[rCode]) //FF
        }
        return hexCode.toString()
    }

    /**
     * Color的16进制颜色值 转 Color的Int整型
     * colorHex - Color的16进制颜色值——#3FE2C5
     * return colorInt - -12590395
     */
    @JvmStatic
    fun hex2Int(colorHex: String?): Int {
        var colorInt: Int
        try {
            colorInt = Color.parseColor(colorHex)
        } catch (e: Exception) {
            e.printStackTrace()
            colorInt = 0
        }
        return colorInt
    }

    /**
     * Color的16进制颜色值 转 rgb数组
     * colorHex - Color的16进制颜色值——#3FE2C5
     * return Color的rgb数组 —— [63,226,197]
     */
    fun hex2Rgb(colorHex: String?): IntArray {
        val colorInt = hex2Int(colorHex)
        return int2Rgb(colorInt)
    }

    /**
     * Color的rgb数组转Color的Int整型
     * rgb - Color的rgb数组 —— [63,226,197]
     * return colorInt - -12590395
     */
    fun rgb2Int(rgb: IntArray): Int {
        val colorInt = Color.rgb(rgb[0], rgb[1], rgb[2])
        return colorInt
    }

    @JvmStatic
    fun int2rgba(colorInt: Int): IntArray {
        val rgba = IntArray(4)

        val red = Color.red(colorInt)
        val green = Color.green(colorInt)
        val blue = Color.blue(colorInt)
        val alpha = Color.alpha(colorInt)
        rgba[0] = red
        rgba[1] = green
        rgba[2] = blue
        rgba[3] = alpha

        return rgba
    }
}
