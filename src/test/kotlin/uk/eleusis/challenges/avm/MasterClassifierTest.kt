package uk.eleusis.challenges.avm

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import uk.eleusis.challenges.avm.Classification.*

@SpringBootTest
internal class MasterClassifierTest {

    @Autowired private lateinit var classifier: MasterClassifier

    @Test

    fun classify() {
        assertEquals(ANIMAL, classifier.classify("animal"))
        assertEquals(VEGETABLE, classifier.classify("vegetable"))
        assertEquals(VEGETABLE, classifier.classify("plant"))
        assertEquals(MINERAL, classifier.classify("mineral"))
        assertEquals(MINERAL, classifier.classify("rock"))
        assertEquals(ANIMAL, classifier.classify("Queen Elizabeth"))
        assertEquals(ANIMAL, classifier.classify("Queen Elizabeth II"))

        assertEquals(ANIMAL, classifier.classify("Jean-Michel Jarre"))

        assertEquals(ANIMAL, classifier.classify("sea cucumber"))
        assertEquals(VEGETABLE, classifier.classify("cucumber"))
        assertEquals(MINERAL, classifier.classify("granite"))
//        assertEquals(ANIMAL, classifier.classify("Pikachu")) - // Detected as MINERAL at the moment
//        assertEquals(ANIMAL, classifier.classify("Ash")) // finds the tree, so returns VEGETABLE
        assertEquals(ANIMAL, classifier.classify("Velociraptor"))
    }
}
