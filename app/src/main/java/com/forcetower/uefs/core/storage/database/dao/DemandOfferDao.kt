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

package com.forcetower.uefs.core.storage.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.forcetower.sagres.database.model.SDemandOffer

@Dao
abstract class DemandOfferDao {
    @Insert(onConflict = REPLACE)
    protected abstract fun insert(offers: List<SDemandOffer>)

    @Query("DELETE FROM SDemandOffer")
    protected abstract fun deleteAll()

    @Query("SELECT * FROM SDemandOffer ORDER BY category, code ASC")
    abstract fun getAll(): LiveData<List<SDemandOffer>>

    @Transaction
    open fun defineDemandOffers(offers: List<SDemandOffer>) {
        deleteAll()
        insert(offers)
    }

    @Query("UPDATE SDemandOffer SET selected = :select WHERE uid = :uid")
    abstract fun updateOfferSelection(uid: Long, select: Boolean)

    @Query("SELECT * FROM SDemandOffer ORDER BY category, code ASC")
    abstract fun getAllDirect(): List<SDemandOffer>
}