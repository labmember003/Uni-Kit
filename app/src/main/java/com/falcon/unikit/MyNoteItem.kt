package com.falcon.unikit

import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.models.item.YearItem

data class MyNoteItem (
    val notesName: String? = null,
    val pdf: String? = null,
    val college: List<CollegeItem>? = null,
    val course: List<CourseItem>? = null,
    val branch: List<BranchItem>? = null,
    val year: List<YearItem>? = null,
    val subject: List<SubjectItem>? = null,

    val like: List<String>? = null,
    val dislike: List<String>? = null,

    val itemType: String? = null
)
