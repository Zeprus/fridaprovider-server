package com.zeprus.fridaprovider.model

class ApplicationInfo(val packageName: String) {

    override fun equals(other: Any?): Boolean {
        return this.packageName == (other as ApplicationInfo).packageName
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}