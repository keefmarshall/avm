package uk.eleusis.challenges.avm

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import uk.eleusis.challenges.avm.Classification.*

internal class WNClassifierTest {

    companion object {
        private val wnc = WNClassifier()

        @JvmStatic
        @BeforeAll
        fun init() {
            wnc.init()
        }
    }

    @Test
    fun allHypernyms() {
        val hypernyms = wnc.allHypernyms("dog")
        assertTrue(hypernyms.size > 50)
    }

    @Test
    fun isAnimal() {
        assertTrue(wnc.isAnimal("animal"))
        assertTrue(wnc.isAnimal("dog"))
        assertTrue(wnc.isAnimal("greyhound"))
        assertTrue(wnc.isAnimal("cocker spaniel"))

        assertTrue(wnc.isAnimal("dinosaur"))
        assertTrue(wnc.isAnimal("coral"))

        assertFalse(wnc.isAnimal("table"))
    }

    @Test
    fun isPlant() {
        assertTrue(wnc.isPlant("violet"))
        assertTrue(wnc.isPlant("broccoli"))
        assertTrue(wnc.isPlant("pansy"))

        assertTrue(wnc.isPlant("tree"))

        assertTrue(wnc.isPlant("pollen"))

        assertFalse(wnc.isPlant("table"))
        assertFalse(wnc.isPlant("dog"))
    }


    @Test
    fun classify() {
        assertEquals(ANIMAL, wnc.classify("animal"))
        assertEquals(VEGETABLE, wnc.classify("vegetable"))
        assertEquals(MINERAL, wnc.classify("mineral"))

        //        assertEquals(MINERAL, wnc.classify("rock")) // currently fails, finds "John Rock"

        assertEquals(ANIMAL, wnc.classify("dog"))
        assertEquals(ANIMAL, wnc.classify("Elizabeth"))
        assertEquals(UNKNOWN, wnc.classify("Keith")) // should be EVIL_GENIUS_ROBOT

        assertEquals(ANIMAL, wnc.classify("coral"))

        assertEquals(VEGETABLE, wnc.classify("pollen"))

        assertEquals(ANIMAL, wnc.classify("dinoflagellates"))
        assertEquals(ANIMAL, wnc.classify("Tony Blair")) // bizarrely he's in WordNet!
//        assertEquals(ANIMAL, wnc.classify("Jean-Michel Jarre"))  // not in WordNet

        // finds "plant" as in "manufacturing plant" - need to be more specific about hypernym terms
//        assertEquals(MINERAL, wnc.classify("battery")) // currently returns VEGETABLE!!
//        assertEquals(ANIMAL, wnc.classify("Cubby Broccoli")) // currently MINERAL
    }

    @Test
    fun lookupWord() {
        assertEquals(2, wnc.lookupWord("rock").size()) // noun and verb
    }
}
