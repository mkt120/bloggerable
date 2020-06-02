package com.mkt120.bloggerable.repository

class TimeRepository : Repository.ITimeRepository{
    override fun getCurrentTime(): Long = System.currentTimeMillis()
}