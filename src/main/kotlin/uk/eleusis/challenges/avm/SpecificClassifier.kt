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
            "rock" to MINERAL
    )

    override fun classify(word: String): Classification {
        return classifications.getOrDefault(word.toLowerCase(), UNKNOWN)
    }
}