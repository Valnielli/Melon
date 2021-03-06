/*
 * Copyright (c) 2018.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.forcetower.sagres.database

import android.content.Context
import androidx.annotation.RestrictTo
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.forcetower.sagres.database.dao.AccessDao
import com.forcetower.sagres.database.dao.ClazzDao
import com.forcetower.sagres.database.dao.DisciplineResumedDao
import com.forcetower.sagres.database.dao.MessageScopeDao
import com.forcetower.sagres.database.dao.PersonDao
import com.forcetower.sagres.database.model.SAccess
import com.forcetower.sagres.database.model.SClass
import com.forcetower.sagres.database.model.SDisciplineResumed
import com.forcetower.sagres.database.model.SMessageScope
import com.forcetower.sagres.database.model.SPerson

@Database(entities = [
    SAccess::class,
    SPerson::class,
    SMessageScope::class,
    SClass::class,
    SDisciplineResumed::class
], version = 3, exportSchema = true)
abstract class SagresDatabase : RoomDatabase() {
    abstract fun accessDao(): AccessDao
    abstract fun personDao(): PersonDao
    abstract fun messageScopeDao(): MessageScopeDao
    abstract fun clazzDao(): ClazzDao
    abstract fun disciplineDao(): DisciplineResumedDao

    companion object {
        private const val DB_NAME = "unesx_sagres_database.db"

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun create(context: Context): SagresDatabase {
            return Room.databaseBuilder(context, SagresDatabase::class.java, DB_NAME)
                .addMigrations(M1TO2, M2TO3)
                .allowMainThreadQueries()
                .build()
        }
    }
}
