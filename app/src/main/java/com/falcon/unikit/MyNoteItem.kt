package com.falcon.unikit

import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.models.item.YearItem

data class MyNoteItem (
    val notesName: String? = null,
    val pdf: String? = null,
    val college: CollegeItem? = null,
    val course: CourseItem? = null,
    val branch: BranchItem? = null,
    val year: YearItem? = null,
    val subject: SubjectItem? = null
)
