package com.example.modelviewintent.utils

import com.example.firebasedb.AssessmentRecord
import com.example.firebasedb.AssessmentRecordBuilder

fun AssessmentRecord.builder() = AssessmentRecordBuilder(this)