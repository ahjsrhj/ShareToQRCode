package cn.imrhj.sharetoqrcode.util

import java.io.BufferedReader


val BufferedReader.lines: Iterator<String>
    get() = object : Iterator<String> {
        var line = this@lines.readLine()
        override fun next(): String {
            if (line == null) {
                throw NoSuchElementException()
            }
            val result = line
            line = this@lines.readLine()
            return result
        }

        override fun hasNext() = line != null
    }