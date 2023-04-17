package com.mgobeaalcoba.earthquakemonitor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mgobeaalcoba.earthquakemonitor.Earthquake

@Database(entities = [Earthquake::class], version = 1)
abstract class EqDatabase: RoomDatabase {
    // Declaramos el eqDao pero no lo vamos a usar acá sino en el MainRepository:
    abstract val eqDao: EqDao
}

private lateinit var INSTANCE: EqDatabase

fun getDatabase(context: Context): EqDatabase {
    // Sincronizo a todos los hilos o threads para que sepan que estoy usando la database:
    synchronized(EqDatabase::class.java) {
        // Si no se instanció la base entonces lo hago. Si ya está instancia entonces solo la retorno.
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                EqDatabase::class.java,
                // nombre de la database. No confundir con el nombre de la tabla
                "earthquake_db"
            ).build()
        }
        return INSTANCE
    }
}