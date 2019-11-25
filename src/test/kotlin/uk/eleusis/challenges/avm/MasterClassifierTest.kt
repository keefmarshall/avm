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
    }
}