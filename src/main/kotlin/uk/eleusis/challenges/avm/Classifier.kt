package uk.eleusis.challenges.avm

interface Classifier {
    fun classify(word: String): Classification
}