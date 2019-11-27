package uk.eleusis.challenges.avm

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import uk.eleusis.challenges.avm.Classification.*

@Component
@Order(1)
class SpecificClassifier : Classifier {

    private val classifications = mapOf(
            "keith" to EVIL_GENIUS_ROBOT,
            "keith marshall" to EVIL_GENIUS_ROBOT,

            // WN finds 'jean' i.e. denim and so gives MINERAL
            "jean-michel jarre" to ANIMAL,
            "jean michel jarre" to ANIMAL,

            // WN finds 'John Rock' so gives ANIMAL
            "rock" to MINERAL,
            "wood" to VEGETABLE, // because lots of surnames

            "salad" to VEGETABLE
    )

    override fun classify(word: String): Classification {
        return classifications.getOrDefault(word.toLowerCase(), UNKNOWN)
    }
}