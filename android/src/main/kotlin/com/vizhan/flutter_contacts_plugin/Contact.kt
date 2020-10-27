package com.vizhan.flutter_contacts_plugin

data class Contact(var id: String, var displayName: String? = null, var avatar: ByteArray? = null) {
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
                "id" to this.id,
                "displayName" to displayName.orEmpty()
        )

        map["avatar"] = if (this.avatar == null) ByteArray(0) else this.avatar as ByteArray

        return map
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (id != other.id) return false
        if (displayName != other.displayName) return false
        if (avatar != null) {
            if (other.avatar == null) return false
            if (!(avatar as ByteArray).contentEquals(other.avatar as ByteArray)) return false
        } else if (other.avatar != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (avatar?.contentHashCode() ?: 0)
        return result
    }
}