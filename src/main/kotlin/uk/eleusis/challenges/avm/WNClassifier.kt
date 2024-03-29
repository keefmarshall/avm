package uk.eleusis.challenges.avm

import net.sf.extjwnl.data.PointerUtils
import net.sf.extjwnl.data.Synset
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import net.sf.extjwnl.dictionary.Dictionary
import org.springframework.core.annotation.Order
import uk.eleusis.challenges.avm.Classification.*

@Component
@Order(10)
class WNClassifier : Classifier {

    private lateinit var dictionary: Dictionary

    @PostConstruct
    fun init() {
        dictionary = Dictionary.getDefaultResourceInstance()
    }

    // This loops through all senses of the word
    fun allHypernyms(word: String) : List<Long> {
        val indexWords = dictionary.lookupAllIndexWords(word)
        return if (indexWords.size() > 0) {
            indexWords.indexWordArray
                    .flatMap { it.senses }
                    .flatMap { PointerUtils.getHypernymTree(it).toList() } // -> PointerTargetNodeList
                    .flatMap { it.asIterable() } // -> PointerTargetNode
                    .map { it.pointerTarget.synset.offset }
//                    .map { it.senseNumber }
                    .distinct()
        } else {
            emptyList()
        }
    }


    fun isAnimal(word: String) = allHypernyms(word).intersect(animalHypernyms).isNotEmpty()

    fun isPlant(word: String) = allHypernyms(word).intersect(vegHypernyms).isNotEmpty()

    override fun classify(word: String): Classification {
        val hypernyms = allHypernyms(word)
        return when {
            dictionary.lookupAllIndexWords(word).size() == 0 -> UNKNOWN
            hypernyms.isEmpty() -> UNKNOWN
            else -> classifyFromHypernyms(hypernyms)
        }
    }

    private val animalHypernyms  = listOf<Long>(15568, 7846, 1328932)
//    private val animalHypernyms  = listOf("animal", "person", "microorganism")
    private val vegHypernyms = listOf<Long>(17402, 7721456, 13107668, 13013628, 1399755)
//    private val vegHypernyms = listOf("plant", "produce", "plant part", "fungus", "algae")

    fun classifyFromHypernyms(hypernyms: List<Long>) =
            when {
                hypernyms.intersect(vegHypernyms).isNotEmpty() -> VEGETABLE
                hypernyms.intersect(animalHypernyms).isNotEmpty() -> ANIMAL
                else -> MINERAL
            }

    fun lookupWord(word: String) = dictionary.lookupAllIndexWords(word)
}
