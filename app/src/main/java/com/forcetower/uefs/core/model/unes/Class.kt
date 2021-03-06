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

package com.forcetower.uefs.core.model.unes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.forcetower.sagres.database.model.SDiscipline
import java.util.UUID

@Entity(
    foreignKeys = [
        ForeignKey(entity = Discipline::class, parentColumns = ["uid"], childColumns = ["discipline_id"], onDelete = CASCADE, onUpdate = CASCADE),
        ForeignKey(entity = Semester::class, parentColumns = ["uid"], childColumns = ["semester_id"], onDelete = CASCADE, onUpdate = CASCADE)
    ], indices = [
        Index(value = ["semester_id"]),
        Index(value = ["discipline_id", "semester_id"], unique = true),
        Index(value = ["uuid"], unique = true)
    ]
)
data class Class(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "discipline_id")
    val disciplineId: Long,
    @ColumnInfo(name = "semester_id")
    val semesterId: Long,
    var status: String? = null,
    @ColumnInfo(name = "final_score")
    var finalScore: Double? = null,
    val uuid: String = UUID.randomUUID().toString(),
    var missedClasses: Int = 0,
    var lastClass: String = "",
    var nextClass: String = ""
) {

    fun selectiveCopy(dis: SDiscipline) {
        if (!dis.nextClass.isNullOrBlank()) nextClass = dis.nextClass
        if (!dis.lastClass.isNullOrBlank()) lastClass = dis.lastClass
        if (dis.missedClasses >= 0) missedClasses = dis.missedClasses
        if (!dis.situation.isNullOrBlank()) status = dis.situation
    }
}