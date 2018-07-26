package cn.imrhj.sharetoqrcode.util

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

object Commander {
    private var mHaveRoot = false
    fun haveRoot(): Boolean {
        if (!mHaveRoot) {
            val result = execRootCmdSilent("echo test")
            Log.d(Thread.currentThread().name, "class = Commander rhjlog haveRoot: root echo: $result")
            if (result != -1) {
                Log.d(Thread.currentThread().name, "class = Commander rhjlog haveRoot: true")
                mHaveRoot = true
            } else {
                Log.d(Thread.currentThread().name, "class = Commander rhjlog haveRoot: false")
            }
        }
        return mHaveRoot
    }

    fun execRootCmdSilent(cmd: String): Int {
        var result = -1
        var dos: DataOutputStream? = null
        try {
            val process = Runtime.getRuntime().exec("su")
            dos = DataOutputStream(process.outputStream)

            Log.d(Thread.currentThread().name, "class = Commander rhjlog execRootCmdSilent: $cmd")
            dos.writeBytes(cmd + "\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            process.waitFor()
            for (line in BufferedReader(InputStreamReader(process.inputStream)).lines) {
                Log.d(Thread.currentThread().name, "class = Commander rhjlog execRootCmdSilent: $line")
            }
            result = process.exitValue()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                dos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }
}