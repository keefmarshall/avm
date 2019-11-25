package uk.eleusis.challenges.avm

import org.springframework.stereotype.Component
import uk.eleusis.challenges.avm.Classification.*

@Component
class MasterClassifier(
        private val classifiers: List<Classifier>
) { // don't implement Classifier here, we don't want a circular dependency

    fun classify(word: String)
         = classifiers.asSequence()
                .map { it.classify(word) }
                .filterNot { it == UNKNOWN }
                .firstOrNull() ?: MINERAL
}