package com.arsartificia.dev.initiativetracker

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class Player (var name: String, var initiative: Int, var turn: Boolean = false) : Serializable {
    private fun writeObject(oos: ObjectOutputStream) {
        return oos.defaultWriteObject()
    }

    private fun readObject(ois: ObjectInputStream) {
        return ois.defaultReadObject()
    }

    private fun readObjectNoData() {
        return
    }
}