package ru.netology.nmedia.util

object NiceNumberDisplay {
    fun shortNumber(number: Int): String {
        return when (number) {
            in 0..999 -> number.toString()
            in 1000..1099 -> "1K"
            in 1100..9999 -> (number / 1000).toString() + "." + ((number % 1000) / 100).toString() + "K"
            in 10000..999999 -> (number / 1000).toString() + "K"
            in 1000000..1099999 -> (number / 1000000).toString() + "M"
            in 1100000..9999999 -> (number / 1000000).toString() + "." + ((number % 1000000) / 100000).toString() + "M"
            else -> "Many"
        }
    }
}