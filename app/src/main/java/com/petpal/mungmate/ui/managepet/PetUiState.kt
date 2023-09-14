package com.petpal.mungmate.ui.managepet

data class PetUiState(
    val name: String,
    val breed: String,
    val sex: String,
    val age: Long,
    val character: String,
    val weigh: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PetUiState

        if (name != other.name) return false
        if (breed != other.breed) return false
        if (sex != other.sex) return false
        if (age != other.age) return false
        if (character != other.character) return false
        if (weigh != other.weigh) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + breed.hashCode()
        result = 31 * result + sex.hashCode()
        result = 31 * result + age.hashCode()
        result = 31 * result + character.hashCode()
        result = 31 * result + weigh.hashCode()
        return result
    }
}
